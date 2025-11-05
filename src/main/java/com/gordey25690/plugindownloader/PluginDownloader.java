package com.gordey25690.plugindownloader;

import org.bukkit.plugin.java.JavaPlugin;
import com.gordey25690.plugindownloader.managers.*;
import com.gordey25690.plugindownloader.commands.PluginCommand;
import com.gordey25690.plugindownloader.commands.TabComplete;

public class PluginDownloader extends JavaPlugin {
    
    private static PluginDownloader instance;
    private DownloadManager downloadManager;
    private PluginManager pluginManager;
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Инициализация менеджеров
        this.configManager = new ConfigManager(this);
        this.downloadManager = new DownloadManager(this);
        this.pluginManager = new PluginManager(this);
        
        // Регистрация команд
        getCommand("plugindownloader").setExecutor(new PluginCommand(this));
        getCommand("магазинплагинов").setExecutor(new PluginCommand(this));
        getCommand("обновлениеплагинов").setExecutor(new PluginCommand(this));
        
        // Регистрация таб-комплита
        getCommand("plugindownloader").setTabCompleter(new TabComplete());
        getCommand("магазинплагинов").setTabCompleter(new TabComplete());
        getCommand("обновлениеплагинов").setTabCompleter(new TabComplete());
        
        // Загрузка конфигурации
        configManager.loadConfig();
        
        getLogger().info("PluginDownloader успешно запущен!");
        getLogger().info("Авторы: gordey25690 & DeepSeek");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("PluginDownloader выключен!");
    }
    
    public static PluginDownloader getInstance() {
        return instance;
    }
    
    public DownloadManager getDownloadManager() {
        return downloadManager;
    }
    
    public PluginManager getPluginManager() {
        return pluginManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
