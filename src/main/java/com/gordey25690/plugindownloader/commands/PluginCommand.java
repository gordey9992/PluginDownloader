package com.gordey25690.plugindownloader.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import com.gordey25690.plugindownloader.PluginDownloader;
import com.gordey25690.plugindownloader.managers.DownloadManager;
import com.gordey25690.plugindownloader.managers.PluginManager;
import com.gordey25690.plugindownloader.managers.ConfigManager;
import com.gordey25690.plugindownloader.managers.SyncManager;

public class PluginCommand implements CommandExecutor {
    private final PluginDownloader plugin;
    private final DownloadManager downloadManager;
    private final PluginManager pluginManager;
    private final ConfigManager configManager;
    private final SyncManager syncManager;

    public PluginCommand(PluginDownloader plugin) {
        this.plugin = plugin;
        this.downloadManager = plugin.getDownloadManager();
        this.pluginManager = plugin.getPluginManager();
        this.configManager = plugin.getConfigManager();
        this.syncManager = plugin.getSyncManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("plugindownloader.use")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав для использования этой команды!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender, label);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "install":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Использование: /" + label + " install <plugin>");
                    return true;
                }
                downloadManager.downloadPlugin(sender, args[1]);
                break;
                
            case "list":
                pluginManager.sendPluginList(sender);
                break;
                
            case "remove":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Использование: /" + label + " remove <plugin>");
                    return true;
                }
                pluginManager.removePlugin(sender, args[1]);
                break;
                
            case "update":
                String pluginToUpdate = args.length > 1 ? args[1] : "all";
                downloadManager.updatePlugins(sender, pluginToUpdate);
                break;
                
            case "info":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Использование: /" + label + " info <plugin>");
                    return true;
                }
                pluginManager.sendPluginInfo(sender, args[1]);
                break;
                
            case "search":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Использование: /" + label + " search <query>");
                    return true;
                }
                downloadManager.searchPlugins(sender, args[1]);
                break;
                
            case "reload":
                configManager.loadConfig();
                sender.sendMessage(ChatColor.GREEN + "Конфигурация перезагружена!");
                break;
                
            case "sync":
                if (sender.hasPermission("plugindownloader.sync")) {
                    sender.sendMessage(ChatColor.YELLOW + "Запуск синхронизации...");
                    syncManager.syncSharedPlugins();
                    sender.sendMessage(ChatColor.GREEN + "Синхронизация завершена!");
                } else {
                    sender.sendMessage(ChatColor.RED + "У вас нет прав для синхронизации!");
                }
                break;
                
            default:
                sender.sendMessage(ChatColor.RED + "Неизвестная команда: " + subCommand);
                sendHelp(sender, label);
                break;
        }
        
        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.GOLD + "=== PluginDownloader ===");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " install <plugin> " + ChatColor.GRAY + "- Установить плагин");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " list " + ChatColor.GRAY + "- Список плагинов");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " remove <plugin> " + ChatColor.GRAY + "- Удалить плагин");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " update [plugin] " + ChatColor.GRAY + "- Обновить плагин");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " info <plugin> " + ChatColor.GRAY + "- Информация о плагине");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " search <query> " + ChatColor.GRAY + "- Поиск плагинов");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " reload " + ChatColor.GRAY + "- Перезагрузить конфиг");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " sync " + ChatColor.GRAY + "- Синхронизировать плагины");
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.GRAY + "Алиасы: /пдл, /плагины, /магазинплагинов, /обновлениеплагинов");
        }
    }
}
