package com.gordey25690.plugindownloader.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.gordey25690.plugindownloader.PluginDownloader;
import java.io.File;
import java.io.InputStreamReader;

public class ConfigManager {
    
    private final PluginDownloader plugin;
    private FileConfiguration config;
    private FileConfiguration messages;
    private File configFile;
    private File messagesFile;
    
    public ConfigManager(PluginDownloader plugin) {
        this.plugin = plugin;
        setupConfig();
    }
    
    public void setupConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        configFile = new File(plugin.getDataFolder(), "config.yml");
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        
        reloadConfig();
    }
    
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        
        // Загрузка дефолтных сообщений если нужно
        YamlConfiguration defaultMessages = YamlConfiguration.loadConfiguration(
            new InputStreamReader(plugin.getResource("messages.yml"))
        );
        messages.addDefaults(defaultMessages);
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public FileConfiguration getMessages() {
        return messages;
    }
    
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка при сохранении config.yml: " + e.getMessage());
        }
    }
    
    public void saveMessages() {
        try {
            messages.save(messagesFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка при сохранении messages.yml: " + e.getMessage());
        }
    }
}
