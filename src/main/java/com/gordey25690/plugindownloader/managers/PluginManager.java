package com.gordey25690.plugindownloader.managers;

import com.gordey25690.plugindownloader.PluginDownloader;

public class PluginManager {
    
    private final PluginDownloader plugin;
    
    public PluginManager(PluginDownloader plugin) {
        this.plugin = plugin;
    }
    
    // Дополнительные методы для управления плагинами
    public boolean isPluginInstalled(String pluginName) {
        return new java.io.File("plugins", pluginName + ".jar").exists();
    }
}
