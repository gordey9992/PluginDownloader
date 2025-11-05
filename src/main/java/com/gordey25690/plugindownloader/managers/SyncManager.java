package com.gordey25690.plugindownloader.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import com.gordey25690.plugindownloader.PluginDownloader;
import com.gordey25690.plugindownloader.utils.MessageUtils;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SyncManager {
    
    private final PluginDownloader plugin;
    private final File cacheFolder;
    private final File sharedPluginsFile;
    private long lastSyncTime = 0;
    
    public SyncManager(PluginDownloader plugin) {
        this.plugin = plugin;
        this.cacheFolder = new File(plugin.getDataFolder(), "cache");
        this.sharedPluginsFile = new File(cacheFolder, "shared_plugins.yml");
        setupCache();
    }
    
    private void setupCache() {
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs();
        }
    }
    
    public boolean syncSharedPlugins() {
        try {
            String syncUrl = plugin.getConfigManager().getConfig().getString("настройки.url-общих-плагинов");
            if (syncUrl == null || syncUrl.isEmpty()) {
                plugin.getLogger().warning("URL для синхронизации не указан в config.yml");
                return false;
            }
            
            URL url = new URL(syncUrl);
            try (InputStream in = url.openStream()) {
                Files.copy(in, sharedPluginsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                lastSyncTime = System.currentTimeMillis();
                plugin.getLogger().info("Общие плагины успешно синхронизированы");
                return true;
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка синхронизации общих плагинов: " + e.getMessage());
            return false;
        }
    }
    
    public YamlConfiguration getSharedPlugins() {
        // Если файла нет или прошло больше часа - синхронизируем
        if (!sharedPluginsFile.exists() || 
            (System.currentTimeMillis() - lastSyncTime) > 3600000) {
            syncSharedPlugins();
        }
        
        if (sharedPluginsFile.exists()) {
            return YamlConfiguration.loadConfiguration(sharedPluginsFile);
        } else {
            plugin.getLogger().warning("Файл общих плагинов не найден");
            return new YamlConfiguration();
        }
    }
    
    public boolean isSyncEnabled() {
        return plugin.getConfigManager().getConfig().getBoolean("настройки.авто-синхронизация", true);
    }
    
    public long getLastSyncTime() {
        return lastSyncTime;
    }
}
3. Обновляем Downlo
