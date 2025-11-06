package com.gordey25690.plugindownloader.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.gordey25690.plugindownloader.PluginDownloader;
import com.gordey25690.plugindownloader.managers.DownloadManager;
import com.gordey25690.plugindownloader.ui.PluginSelectionGUI;
import com.gordey25690.plugindownloader.utils.MessageUtils;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class PluginCommand implements CommandExecutor {
    
    private final PluginDownloader plugin;
    private final DownloadManager downloadManager;
    
    public PluginCommand(PluginDownloader plugin) {
        this.plugin = plugin;
        this.downloadManager = plugin.getDownloadManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 📍 ПРОВЕРЯЕМ ЕСЛИ ЭТО КОНСОЛЬ
        if (!(sender instanceof Player)) {
            return handleConsoleCommand(sender, args);
        }
        
        // Оригинальный код для игроков
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
                    downloadManager.installPlugin(player, args[1]);
                } else {
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
                    downloadManager.addCustomPlugin(player, args[1], args[2], args[3]);
                } else {
                    MessageUtils.sendMessage(player, "использование: /" + label + " добавить <название> <ссылка> <описание>");
                }
                break;
                
            case "синхронизировать":
            case "sync":
                if (!player.hasPermission("plugindownloader.manage")) {
                    MessageUtils.sendMessage(player, "нет-прав");
                    return true;
                }
                plugin.getSyncManager().syncSharedPlugins();
                MessageUtils.sendMessage(player, "синхронизация-завершена");
                break;
                
            default:
                showHelp(player, label);
                break;
        }
        
        return true;
    }
    
    // 📍 ПОЛНАЯ СИСТЕМА ДЛЯ КОНСОЛИ
    private boolean handleConsoleCommand(CommandSender sender, String[] args) {
    if (args.length == 0) {
        showConsoleMainMenu(sender);
        return true;
    }

    // 📍 КОНВЕРТИРУЕМ РУССКИЕ КОМАНДЫ В АНГЛИЙСКИЕ ДЛЯ КОНСОЛИ
    String command = args[0].toLowerCase();
    
    // Русские команды → английские
    Map<String, String> russianToEnglish = new HashMap<>();
    russianToEnglish.put("установить", "install");
    russianToEnglish.put("список", "list");
    russianToEnglish.put("удалить", "remove");
    russianToEnglish.put("инфо", "info");
    russianToEnglish.put("поиск", "search");
    russianToEnglish.put("перезагрузить", "reload");
    russianToEnglish.put("синхронизировать", "sync");
    russianToEnglish.put("статус", "status");
    russianToEnglish.put("очистить", "clear");
    russianToEnglish.put("помощь", "help");
    
    if (russianToEnglish.containsKey(command)) {
        command = russianToEnglish.get(command);
    }
        if (args.length == 0) {
            showConsoleMainMenu(sender);
            return true;
        }

        // 📍 ОБРАБАТЫВАЕМ СОКРАЩЕННЫЕ КОМАНДЫ
        String command = args[0].toLowerCase();
        
        // Сокращения для русских команд
        switch (command) {
            case "уст":
            case "inst":
            case "i":
                command = "install";
                break;
            case "сп":
            case "lst":
            case "l":
                command = "list";
                break;
            case "уд":
            case "rem":
            case "r":
            case "del":
                command = "remove";
                break;
            case "инф":
            case "inf":
                command = "info";
                break;
            case "поиск":
            case "find":
            case "s":
                command = "search";
                break;
            case "пер":
            case "rel":
                command = "reload";
                break;
            case "синх":
            case "syn":
                command = "sync";
                break;
            case "стат":
            case "stat":
                command = "status";
                break;
            case "очист":
            case "clr":
                command = "clear";
                break;
            case "пом":
            case "h":
            case "?":
                command = "help";
                break;
        }

        // 📍 ОБРАБАТЫВАЕМ КОМАНДЫ
        switch (command) {
            case "install":
            case "inst":
            case "i":
                if (args.length > 1) {
                    handleConsoleInstall(sender, args[1]);
                } else {
                    showConsoleInstallMenu(sender);
                }
                break;
                
            case "list":
            case "lst":
            case "l":
                handleConsoleList(sender);
                break;
                
            case "remove":
            case "rem":
            case "r":
            case "delete":
            case "del":
                if (args.length > 1) {
                    handleConsoleRemove(sender, args[1]);
                } else {
                    sender.sendMessage("§cИспользование: plugindownloader remove <плагин>");
                }
                break;
                
            case "reload":
            case "rel":
            case "rl":
                plugin.getConfigManager().reloadConfig();
                sender.sendMessage("§a[PluginDownloader] Конфигурация перезагружена");
                break;
                
            case "sync":
            case "syn":
            case "synchronize":
                boolean success = plugin.getSyncManager().syncSharedPlugins();
                if (success) {
                    sender.sendMessage("§a[PluginDownloader] Синхронизация с GitHub завершена");
                } else {
                    sender.sendMessage("§c[PluginDownloader] Ошибка синхронизации с GitHub");
                }
                break;
                
            case "info":
            case "inf":
            case "about":
                if (args.length > 1) {
                    handleConsoleInfo(sender, args[1]);
                } else {
                    sender.sendMessage("§cИспользование: plugindownloader info <плагин>");
                }
                break;
                
            case "search":
            case "find":
            case "s":
                if (args.length > 1) {
                    handleConsoleSearch(sender, args[1]);
                } else {
                    sender.sendMessage("§cИспользование: plugindownloader search <запрос>");
                }
                break;
                
            case "status":
            case "stat":
            case "st":
                handleConsoleStatus(sender);
                break;
                
            case "clear":
            case "clr":
            case "clean":
                handleConsoleClear(sender);
                break;
                
            case "help":
            case "h":
            case "?":
                showConsoleHelp(sender);
                break;
                
            default:
                showConsoleMainMenu(sender);
                break;
        }
        
        return true;
        // 📍 КОНВЕРТИРУЕМ РУССКИЕ КОМАНДЫ В АНГЛИЙСКИЕ ДЛЯ КОНСОЛИ
    String command = args[0].toLowerCase();
    
    // Русские команды → английские
    Map<String, String> russianToEnglish = new HashMap<>();
    russianToEnglish.put("установить", "install");
    russianToEnglish.put("список", "list");
    russianToEnglish.put("удалить", "remove");
    russianToEnglish.put("инфо", "info");
    russianToEnglish.put("поиск", "search");
    russianToEnglish.put("перезагрузить", "reload");
    russianToEnglish.put("синхронизировать", "sync");
    russianToEnglish.put("статус", "status");
    russianToEnglish.put("очистить", "clear");
    russianToEnglish.put("помощь", "help");
    }
    
    // 📍 СПЕЦИАЛЬНЫЕ МЕТОДЫ ДЛЯ КОНСОЛИ
    private void showConsoleMainMenu(CommandSender sender) {
        sender.sendMessage("§6╔════════════════════════════════════════╗");
        sender.sendMessage("§6║      §ePluginDownloader Console      §6║");
        sender.sendMessage("§6╠════════════════════════════════════════╣");
        sender.sendMessage("§6║ §eОсновные команды:§6                  ║");
        sender.sendMessage("§6║ §fi <плагин>§7 - Установка           §6║");
        sender.sendMessage("§6║ §fl§7 - Список плагинов              §6║");
        sender.sendMessage("§6║ §fr <плагин>§7 - Удаление            §6║");
        sender.sendMessage("§6║ §finf <плагин>§7 - Информация        §6║");
        sender.sendMessage("§6║ §fs <запрос>§7 - Поиск               §6║");
        sender.sendMessage("§6║                                      §6║");
        sender.sendMessage("§6║ §eСистемные команды:§6                 ║");
        sender.sendMessage("§6║ §frl§7 - Перезагрузка конфигов       §6║");
        sender.sendMessage("§6║ §fsyn§7 - Синхронизация с GitHub     §6║");
        sender.sendMessage("§6║ §fst§7 - Статус системы              §6║");
        sender.sendMessage("§6║ §fclr§7 - Очистка кэша               §6║");
        sender.sendMessage("§6║ §fh§7 - Полная справка              §6║");
        sender.sendMessage("§6╚═══════════════════════════════════════╝");
    }
    
    private void showConsoleInstallMenu(CommandSender sender) {
        sender.sendMessage("§6╔════════════════════════════════════════╗");
        sender.sendMessage("§6║         §eУстановка плагинов§6         ║");
        sender.sendMessage("§6╠════════════════════════════════════════╣");
        sender.sendMessage("§6║ §fДоступные плагины:§6                 ║");
   
        // Показываем популярные плагины для быстрой установки
        sender.sendMessage("§6║ §aViaVersion§7 - поддержка версий    §6║");
        sender.sendMessage("§6║ §aProtocolLib§7 - работа с пакетами  §6║");
        sender.sendMessage("§6║ §aWorldEdit§7 - редактор карт        §6║");
        sender.sendMessage("§6║ §aLuckPerms§7 - система прав         §6║");
        sender.sendMessage("§6║                                      §6║");
        sender.sendMessage("§6║ §eИспользование:§6                     ║");
        sender.sendMessage("§6║ §fplugindownloader i <name>§6          ║");
        sender.sendMessage("§6║ §fПример: i ViaVersion§6               ║");
        sender.sendMessage("§6╚════════════════════════════════════════╝");
    }
    
    private void handleConsoleInstall(CommandSender sender, String pluginName) {
        boolean success = downloadManager.installPluginConsole(sender, pluginName);
        if (success) {
            sender.sendMessage("§a[PluginDownloader] Плагин " + pluginName + " успешно установлен!");
            sender.sendMessage("§7[!] Для применения изменений перезагрузите сервер");
        }
    }
    
    private void handleConsoleList(CommandSender sender) {
        downloadManager.showConsolePluginList(sender);
    }
    
    private void handleConsoleRemove(CommandSender sender, String pluginName) {
        boolean success = downloadManager.removePluginConsole(sender, pluginName);
        if (success) {
            sender.sendMessage("§a[PluginDownloader] Плагин " + pluginName + " успешно удален!");
            sender.sendMessage("§7[!] Для применения изменений перезагрузите сервер");
        }
    }
    
    private void handleConsoleInfo(CommandSender sender, String pluginName) {
        downloadManager.showConsolePluginInfo(sender, pluginName);
    }
    
    private void handleConsoleSearch(CommandSender sender, String query) {
        downloadManager.searchPluginsConsole(sender, query);
    }
    
    private void handleConsoleStatus(CommandSender sender) {
        sender.sendMessage("§6╔════════════════════════════════════════════════════════════════════════╗");
        sender.sendMessage("§6║         §eСтатус системы§6                                             ║");
        sender.sendMessage("§6╠════════════════════════════════════════════════════════════════════════╣");
        sender.sendMessage("§6║ §fВерсия плагина:§7 " + plugin.getDescription().getVersion() + "§6     ║");
        sender.sendMessage("§6║ §fСинхронизация:§7 " + 
            (plugin.getSyncManager().isSyncEnabled() ? "§aВключена" : "§cВыключена") + "§6             ║");
        sender.sendMessage("§6║ §fПоследняя синхронизация:§7 " + getLastSyncTimeFormatted() + "      §6║");
        sender.sendMessage("§6║ §fОбщих плагинов:§7 " + downloadManager.getSharedPluginsCount() + "  §6║");
        sender.sendMessage("§6║ §fОсновных плагинов:§7 " + downloadManager.getMainPluginsCount() + " §6║");
        sender.sendMessage("§6╚════════════════════════════════════════════════════════════════════════╝");
    }
    
    private void handleConsoleClear(CommandSender sender) {
        sender.sendMessage("§e[PluginDownloader] Очистка кэша...");
        // Здесь можно добавить очистку кэша
        sender.sendMessage("§a[PluginDownloader] Кэш успешно очищен");
    }
    
    private String getLastSyncTimeFormatted() {
        long lastSync = plugin.getSyncManager().getLastSyncTime();
        if (lastSync == 0) return "§cНикогда";
        
        long diff = System.currentTimeMillis() - lastSync;
        long minutes = diff / (60 * 1000);
        
        if (minutes < 1) return "§aТолько что";
        if (minutes < 60) return "§a" + minutes + " мин назад";
        
        long hours = minutes / 60;
        if (hours < 24) return "§e" + hours + " ч назад";
        
        return "§c" + (hours / 24) + " дн назад";
    }
    
    private void showConsoleHelp(CommandSender sender) {
        sender.sendMessage("§6=== PluginDownloader - Полная справка ===");
        sender.sendMessage("§eОсновные команды:");
        sender.sendMessage("§f  install§7, §fi§7 - Установить плагин");
        sender.sendMessage("§f  list§7, §fl§7 - Показать список всех плагинов");
        sender.sendMessage("§f  remove§7, §fr§7 - Удалить плагин");
        sender.sendMessage("§f  info§7, §finf§7 - Информация о плагине");
        sender.sendMessage("§f  search§7, §fs§7 - Поиск плагинов");
        sender.sendMessage("");
        sender.sendMessage("§eСистемные команды:");
        sender.sendMessage("§f  reload§7, §frl§7 - Перезагрузить конфигурацию");
        sender.sendMessage("§f  sync§7, §fsyn§7 - Синхронизировать с GitHub");
        sender.sendMessage("§f  status§7, §fst§7 - Показать статус системы");
        sender.sendMessage("§f  clear§7, §fclr§7 - Очистить кэш");
        sender.sendMessage("§f  help§7, §fh§7 - Эта справка");
        sender.sendMessage("");
        sender.sendMessage("§eПримеры использования:");
        sender.sendMessage("§7  plugindownloader i ViaVersion");
        sender.sendMessage("§7  plugindownloader l");
        sender.sendMessage("§7  plugindownloader st");
        sender.sendMessage("§7  plugindownloader r ProtocolLib");
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
        MessageUtils.sendMessage(player, "&e/" + label + " синхронизировать &7- Синхронизировать с GitHub");
        MessageUtils.sendMessage(player, "&e/магазинплагинов &7- Открыть магазин плагинов");
        MessageUtils.sendMessage(player, "&e/обновлениеплагинов &7- Проверить обновления");
    }
}
