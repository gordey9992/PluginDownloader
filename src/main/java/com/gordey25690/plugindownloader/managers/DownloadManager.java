package com.gordey25690.plugindownloader.managers;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
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
    YamlConfiguration sharedPlugins = plugin.getSyncManager().getSharedPlugins();
    
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
    
    // Остальные методы остаются без изменений...
    
    public void showPluginList(Player player) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        
        MessageUtils.sendMessage(player, "список-плагинов");
        
        boolean hasPlugins = false;
        
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
            player.sendMessage(MessageUtils.colorize("&6=== Кастомные плагины ==="));
            for (String pluginName : config.getConfigurationSection("кастомные-плагинов").getKeys(false)) {
                showPluginInfoLine(player, pluginName, "кастомные-плагины");
                hasPlugins = true;
            }
        }
        
        if (!hasPlugins) {
            player.sendMessage(MessageUtils.colorize("&cПлагины не найдены в библиотеке"));
        }
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
    
    public void searchPlugins(Player player, String query) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        
        MessageUtils.sendMessage(player, "поиск-результаты", new String[]{"запрос", query});
        
        boolean found = false;
        
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
    
    public void removePlugin(Player player, String pluginName) {
        File pluginFile = new File(pluginsFolder, pluginName + ".jar");
        
        if (!pluginFile.exists()) {
            MessageUtils.sendMessage(player, "плагин-не-найден", new String[]{"плагин", pluginName});
            return;
        }
        
        // Создание резервной копии
        createBackup(pluginFile);
        
        if (pluginFile.delete()) {
            MessageUtils.sendMessage(player, "плагин-удален", new String[]{"плагин", pluginName});
            MessageUtils.sendMessage(player, "перезагрузка-сервера");
        } else {
            MessageUtils.sendMessage(player, "ошибка-скачивания", new String[]{"плагин", pluginName});
        }
    }
    
    private void createBackup(File pluginFile) {
        if (!plugin.getConfigManager().getConfig().getBoolean("резервные-копии.сохранять-резервные-копии", true)) {
            return;
        }
        
        try {
            File backupDir = new File(plugin.getConfigManager().getConfig().getString("резервные-копии.папка-резервных-копий", "plugins/PluginDownloader/backups"));
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            
            String backupName = pluginFile.getName() + ".backup." + System.currentTimeMillis();
            File backupFile = new File(backupDir, backupName);
            
            Files.copy(pluginFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
        } catch (Exception e) {
            plugin.getLogger().warning("Не удалось создать резервную копию: " + e.getMessage());
        }
    }
    
    public void showPluginList(Player player) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        
        MessageUtils.sendMessage(player, "список-плагинов");
        
        for (String pluginName : config.getConfigurationSection("библиотека-плагинов").getKeys(false)) {
            String description = config.getString("библиотека-плагинов." + pluginName + ".описание");
            String version = config.getString("библиотека-плагинов." + pluginName + ".версия");
            
            player.sendMessage(MessageUtils.colorize(" &e" + pluginName + " &7v" + version + " &8- &f" + description));
        }
    }
    
    public void showPluginInfo(Player player, String pluginName) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        
        if (!config.contains("библиотека-плагинов." + pluginName)) {
            MessageUtils.sendMessage(player, "плагин-не-найден", new String[]{"плагин", pluginName});
            return;
        }
        
        String author = config.getString("библиотека-плагинов." + pluginName + ".автор");
        String version = config.getString("библиотека-плагинов." + pluginName + ".версия");
        String description = config.getString("библиотека-плагинов." + pluginName + ".описание");
        String source = config.getString("библиотека-плагинов." + pluginName + ".источник");
        
        File pluginFile = new File(pluginsFolder, pluginName + ".jar");
        String status = pluginFile.exists() ? "&aУстановлен" : "&cНе установлен";
        
        MessageUtils.sendMessage(player, "информация-о-плагине", new String[]{
            "название", pluginName,
            "версия", version,
            "автор", author,
            "описание", description,
            "статус", status
        });
    }
    
    public void searchPlugins(Player player, String query) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        
        MessageUtils.sendMessage(player, "поиск-результаты", new String[]{"запрос", query});
        
        boolean found = false;
        for (String pluginName : config.getConfigurationSection("библиотека-плагинов").getKeys(false)) {
            if (pluginName.toLowerCase().contains(query.toLowerCase()) ||
                config.getString("библиотека-плагинов." + pluginName + ".описание", "").toLowerCase().contains(query.toLowerCase())) {
                
                String description = config.getString("библиотека-плагинов." + pluginName + ".описание");
                String version = config.getString("библиотека-плагинов." + pluginName + ".версия");
                
                player.sendMessage(MessageUtils.colorize(" &e" + pluginName + " &7v" + version + " &8- &f" + description));
                found = true;
            }
        }
        
        if (!found) {
            player.sendMessage(MessageUtils.colorize(" &cПлагины не найдены по запросу: &e" + query));
        }
    }
    
    public void updatePlugin(Player player, String pluginName) {
        // Реализация обновления плагина
        MessageUtils.sendMessage(player, "обновление-проверка");
        // Здесь будет логика проверки и обновления
    }

public void addCustomPlugin(Player player, String pluginName, String url, String description) {
    FileConfiguration config = plugin.getConfigManager().getConfig();
    
    // Проверяем, нет ли уже плагина с таким именем
    if (config.contains("библиотека-плагинов." + pluginName) || 
        config.contains("кастомные-плагины." + pluginName)) {
        MessageUtils.sendMessage(player, "плагин-уже-существует", new String[]{"плагин", pluginName});
        return;
    }
    
    // Добавляем в кастомные плагины
    String path = "кастомные-плагины." + pluginName + ".";
    config.set(path + "автор", "Пользователь");
    config.set(path + "версия", "1.0.0");
    config.set(path + "источник", "прямая-ссылка");
    config.set(path + "описание", description);
    config.set(path + "ссылка", url);
    
    // Сохраняем конфиг
    plugin.getConfigManager().saveConfig();
    
    MessageUtils.sendMessage(player, "плагин-добавлен", new String[]{"плагин", pluginName});
}
    
    public void checkAllUpdates(Player player) {
        MessageUtils.sendMessage(player, "обновление-проверка");
        MessageUtils.sendMessage(player, "нет-обновлений");
    }
}
