package com.gordey25690.plugindownloader.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.gordey25690.plugindownloader.PluginDownloader;
import com.gordey25690.plugindownloader.managers.DownloadManager;
import com.gordey25690.plugindownloader.ui.PluginSelectionGUI;
import com.gordey25690.plugindownloader.utils.MessageUtils;

public class PluginCommand implements CommandExecutor {
    
    private final PluginDownloader plugin;
    private final DownloadManager downloadManager;
    
    public PluginCommand(PluginDownloader plugin) {
        this.plugin = plugin;
        this.downloadManager = plugin.getDownloadManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда только для игроков!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("plugindownloader.use")) {
            MessageUtils.sendMessage(player, "нет-прав");
            return true;
        }
        
        if (args.length == 0) {
            showHelp(player, label);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "установить":
            case "install":
                if (!player.hasPermission("plugindownloader.install")) {
                    MessageUtils.sendMessage(player, "нет-прав");
                    return true;
                }
                if (args.length > 1) {
                    // Установка конкретного плагина
                    downloadManager.installPlugin(player, args[1]);
                } else {
                    // Открытие GUI выбора плагина
                    PluginSelectionGUI.openPluginSelection(player);
                }
                break;
                
            case "список":
            case "list":
                downloadManager.showPluginList(player);
                break;
                
            case "удалить":
            case "remove":
                if (!player.hasPermission("plugindownloader.remove")) {
                    MessageUtils.sendMessage(player, "нет-прав");
                    return true;
                }
                if (args.length > 1) {
                    downloadManager.removePlugin(player, args[1]);
                } else {
                    MessageUtils.sendMessage(player, "использование: /" + label + " удалить <плагин>");
                }
                break;
                
            case "обновить":
            case "update":
                if (!player.hasPermission("plugindownloader.update")) {
                    MessageUtils.sendMessage(player, "нет-прав");
                    return true;
                }
                if (args.length > 1) {
                    downloadManager.updatePlugin(player, args[1]);
                } else {
                    downloadManager.checkAllUpdates(player);
                }
                break;
                
            case "перезагрузить":
            case "reload":
                if (!player.hasPermission("plugindownloader.reload")) {
                    MessageUtils.sendMessage(player, "нет-прав");
                    return true;
                }
                plugin.getConfigManager().reloadConfig();
                MessageUtils.sendMessage(player, "конфиг-перезагружен");
                break;
                
            case "инфо":
            case "info":
                if (args.length > 1) {
                    downloadManager.showPluginInfo(player, args[1]);
                } else {
                    MessageUtils.sendMessage(player, "использование: /" + label + " инфо <плагин>");
                }
                break;
                
            case "поиск":
            case "search":
                if (args.length > 1) {
                    downloadManager.searchPlugins(player, args[1]);
                } else {
                    MessageUtils.sendMessage(player, "использование: /" + label + " поиск <запрос>");
                }
                break;
                
            case "добавить":
            case "add":
                if (!player.hasPermission("plugindownloader.manage")) {
                    MessageUtils.sendMessage(player, "нет-прав");
                    return true;
                }
                if (args.length >= 4) {
                    // /пдл добавить <название> <ссылка> <описание>
                    downloadManager.addCustomPlugin(player, args[1], args[2], args[3]);
                } else {
                    MessageUtils.sendMessage(player, "использование: /" + label + " добавить <название> <ссылка> <описание>");
                }
                break;
                
            default:
                showHelp(player, label);
                break;
        }
        
        return true;
    }
    
    private void showHelp(Player player, String label) {
        MessageUtils.sendMessage(player, "&6=== Помощь PluginDownloader ===");
        MessageUtils.sendMessage(player, "&e/" + label + " установить [плагин] &7- Установить плагин");
        MessageUtils.sendMessage(player, "&e/" + label + " список &7- Список доступных плагинов");
        MessageUtils.sendMessage(player, "&e/" + label + " удалить <плагин> &7- Удалить плагин");
        MessageUtils.sendMessage(player, "&e/" + label + " обновить [плагин] &7- Обновить плагин(ы)");
        MessageUtils.sendMessage(player, "&e/" + label + " инфо <плагин> &7- Информация о плагине");
        MessageUtils.sendMessage(player, "&e/" + label + " поиск <запрос> &7- Поиск плагинов");
        MessageUtils.sendMessage(player, "&e/" + label + " перезагрузить &7- Перезагрузить конфиг");
        MessageUtils.sendMessage(player, "&e/" + label + " добавить <наз> <ссылка> <опис> &7- Добавить кастомный плагин");
        MessageUtils.sendMessage(player, "&e/магазинплагинов &7- Открыть магазин плагинов");
        MessageUtils.sendMessage(player, "&e/обновлениеплагинов &7- Проверить обновления");
    }
}
