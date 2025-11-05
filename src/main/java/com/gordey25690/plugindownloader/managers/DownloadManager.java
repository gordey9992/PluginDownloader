package com.gordey25690.plugindownloader.managers;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration; // 📍 ДОБАВИТЬ ЭТОТ ИМПОРТ
import com.gordey25690.plugindownloader.PluginDownloader;
import com.gordey25690.plugindownloader.utils.MessageUtils;
import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DownloadManager {
    
    private final PluginDownloader plugin;
    private final File pluginsFolder;
    
    public DownloadManager(PluginDownloader plugin) {
        this.plugin = plugin;
        this.pluginsFolder = new File("plugins");
    }
    
    public void installPlugin(Player player, String pluginName) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        YamlConfiguration sharedPlugins = plugin.getSyncManager().getSharedPlugins(); // 📍 ИСПРАВЛЕНО
        
        // Сначала проверяем в общих плагинах (GitHub)
        if (sharedPlugins.contains("общие-плагины." + pluginName)) {
            installFromSharedLibrary(player, pluginName, sharedPlugins);
        }
        // Затем в основной библиотеке
        else if (config.contains("библиотека-плагинов." + pluginName)) {
            installFromLibrary(player, pluginName, "библиотека-плагинов");
        }
        // Затем в кастомных плагинах
        else if (config.contains("кастомные-плагины." + pluginName)) {
            installFromLibrary(player, pluginName, "кастомные-плагины");
        }
        else {
            MessageUtils.sendMessage(player, "плагин-не-найден", new String[]{"плагин", pluginName});
        }
    }
    
    private void installFromSharedLibrary(Player player, String pluginName, YamlConfiguration sharedPlugins) {
        String path = "общие-плагины." + pluginName + ".";
        
        String author = sharedPlugins.getString(path + "автор");
        String version = sharedPlugins.getString(path + "версия");
        String source = sharedPlugins.getString(path + "источник");
        String url = sharedPlugins.getString(path + "ссылка");
        String description = sharedPlugins.getString(path + "описание");
        
        MessageUtils.sendMessage(player, "плагин-скачивается", new String[]{"плагин", pluginName});
        
        // Проверка если плагин уже установлен
        File pluginFile = new File(pluginsFolder, pluginName + ".jar");
        if (pluginFile.exists()) {
            MessageUtils.sendMessage(player, "плагин-уже-установлен", new String[]{"плагин", pluginName});
            return;
        }
        
        // Скачивание плагина
        downloadPlugin(player, pluginName, url, pluginFile);
    }
    
    private void installFromLibrary(Player player, String pluginName, String librarySection) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        String path = librarySection + "." + pluginName + ".";
        
        String author = config.getString(path + "автор");
        String version = config.getString(path + "версия");
        String source = config.getString(path + "источник");
        String url = config.getString(path + "ссылка");
        
        MessageUtils.sendMessage(player, "плагин-скачивается", new String[]{"плагин", pluginName});
        
        // Проверка если плагин уже установлен
        File pluginFile = new File(pluginsFolder, pluginName + ".jar");
        if (pluginFile.exists()) {
            MessageUtils.sendMessage(player, "плагин-уже-установлен", new String[]{"плагин", pluginName});
            return;
        }
        
        // Скачивание плагина
        downloadPlugin(player, pluginName, url, pluginFile);
    }
    
    private void downloadPlugin(Player player, String pluginName, String urlString, File outputFile) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            
            // Проверка размера файла
            int fileSize = connection.getContentLength();
            int maxSize = plugin.getConfigManager().getConfig().getInt("настройки.максимальный-размер-файла-мб", 50) * 1024 * 1024;
            
            if (fileSize > maxSize) {
                MessageUtils.sendMessage(player, "ошибки.файл-слишком-большой", 
                    new String[]{"размер", String.valueOf(maxSize / 1024 / 1024)});
                return;
            }
            
            // Скачивание
            try (InputStream in = connection.getInputStream()) {
                Files.copy(in, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            MessageUtils.sendMessage(player, "плагин-установлен", new String[]{"плагин", pluginName});
            MessageUtils.sendMessage(player, "перезагрузка-сервера");
            
        } catch (Exception e) {
            plugin.getLogger().severe("Ошибка при скачивании плагина " + pluginName + ": " + e.getMessage());
            MessageUtils.sendMessage(player, "ошибка-скачивания", new String[]{"плагин", pluginName});
        }
    }
    
    // УБЕРИ СТАРЫЕ ВЕРСИИ ЭТИХ МЕТОДОВ ЕСЛИ ЕСТЬ!
    public void showPluginList(Player player) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        YamlConfiguration sharedPlugins = plugin.getSyncManager().getSharedPlugins();
        
        MessageUtils.sendMessage(player, "список-плагинов");
        
        boolean hasPlugins = false;
        
        // 📍 ОБЩИЕ ПЛАГИНЫ (с GitHub)
        if (sharedPlugins.contains("общие-плагины")) {
            player.sendMessage(MessageUtils.colorize("&2=== Общие плагины (GitHub) ==="));
            for (String pluginName : sharedPlugins.getConfigurationSection("общие-плагины").getKeys(false)) {
                showSharedPluginInfoLine(player, pluginName, sharedPlugins);
                hasPlugins = true;
            }
        }
        
        // Основные плагины
        if (config.contains("библиотека-плагинов")) {
            player.sendMessage(MessageUtils.colorize("&6=== Основные плагины ==="));
            for (String pluginName : config.getConfigurationSection("библиотека-плагинов").getKeys(false)) {
                showPluginInfoLine(player, pluginName, "библиотека-плагинов");
                hasPlugins = true;
            }
        }
        
        // Кастомные плагины
        if (config.contains("кастомные-плагины")) {
            player.sendMessage(MessageUtils.colorize("&e=== Кастомные плагины ==="));
            for (String pluginName : config.getConfigurationSection("кастомные-плагины").getKeys(false)) {
                showPluginInfoLine(player, pluginName, "кастомные-плагины");
                hasPlugins = true;
            }
        }
        
        if (!hasPlugins) {
            player.sendMessage(MessageUtils.colorize("&cПлагины не найдены в библиотеке"));
        }
    }
    
    private void showSharedPluginInfoLine(Player player, String pluginName, YamlConfiguration sharedPlugins) {
        String path = "общие-плагины." + pluginName + ".";
        
        String description = sharedPlugins.getString(path + "описание");
        String version = sharedPlugins.getString(path + "версия");
        
        // Проверяем статус установки
        File pluginFile = new File(pluginsFolder, pluginName + ".jar");
        String status = pluginFile.exists() ? "&a✓" : "&c✗";
        
        player.sendMessage(MessageUtils.colorize(" &2🌐 " + status + " &a" + pluginName + " &7v" + version + " &8- &f" + description));
    }
    
    private void showPluginInfoLine(Player player, String pluginName, String librarySection) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        String path = librarySection + "." + pluginName + ".";
        
        String description = config.getString(path + "описание");
        String version = config.getString(path + "версия");
        
        // Проверяем статус установки
        File pluginFile = new File(pluginsFolder, pluginName + ".jar");
        String status = pluginFile.exists() ? "&a✓" : "&c✗";
        
        String libraryIcon = librarySection.equals("библиотека-плагины") ? "&9★" : "&6☆";
        
        player.sendMessage(MessageUtils.colorize(" " + libraryIcon + " " + status + " &e" + pluginName + " &7v" + version + " &8- &f" + description));
    }
    
    // УБЕРИ СТАРУЮ ВЕРСИЮ ЕСЛИ ЕСТЬ!
    public void searchPlugins(Player player, String query) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        YamlConfiguration sharedPlugins = plugin.getSyncManager().getSharedPlugins();
        
        MessageUtils.sendMessage(player, "поиск-результаты", new String[]{"запрос", query});
        
        boolean found = false;
        
        // Поиск в общих плагинах
        if (sharedPlugins.contains("общие-плагины")) {
            for (String pluginName : sharedPlugins.getConfigurationSection("общие-плагины").getKeys(false)) {
                if (matchesSearch(pluginName, sharedPlugins.getString("общие-плагины." + pluginName + ".описание", ""), query)) {
                    showSharedPluginInfoLine(player, pluginName, sharedPlugins);
                    found = true;
                }
            }
        }
        
        // Поиск в основных плагинах
        if (config.contains("библиотека-плагинов")) {
            for (String pluginName : config.getConfigurationSection("библиотека-плагинов").getKeys(false)) {
                if (matchesSearch(pluginName, config.getString("библиотека-плагинов." + pluginName + ".описание", ""), query)) {
                    showPluginInfoLine(player, pluginName, "библиотека-плагинов");
                    found = true;
                }
            }
        }
        
        // Поиск в кастомных плагинах
        if (config.contains("кастомные-плагины")) {
            for (String pluginName : config.getConfigurationSection("кастомные-плагины").getKeys(false)) {
                if (matchesSearch(pluginName, config.getString("кастомные-плагины." + pluginName + ".описание", ""), query)) {
                    showPluginInfoLine(player, pluginName, "кастомные-плагины");
                    found = true;
                }
            }
        }
        
        if (!found) {
            player.sendMessage(MessageUtils.colorize(" &cПлагины не найдены по запросу: &e" + query));
        }
    }
    
    private boolean matchesSearch(String pluginName, String description, String query) {
        return pluginName.toLowerCase().contains(query.toLowerCase()) ||
               description.toLowerCase().contains(query.toLowerCase());
    }
    
    // Остальные методы (removePlugin, updatePlugin и т.д.) остаются без изменений
    public void removePlugin(Player player, String pluginName) {
        // ... существующий код
    }
    
    public void showPluginInfo(Player player, String pluginName) {
        // ... существующий код
    }
    
    public void updatePlugin(Player player, String pluginName) {
        // ... существующий код
    }
    
    public void checkAllUpdates(Player player) {
        // ... существующий код
    }
    
    public void addCustomPlugin(Player player, String pluginName, String url, String description) {
        // ... существующий код
    }
}
