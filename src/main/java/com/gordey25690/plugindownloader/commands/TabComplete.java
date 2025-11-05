package com.gordey25690.plugindownloader.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Основные команды
            completions.add("установить");
            completions.add("список");
            completions.add("удалить");
            completions.add("обновить");
            completions.add("инфо");
            completions.add("поиск");
            completions.add("перезагрузить");
        } else if (args.length == 2) {
            // Подсказки для конкретных команд
            switch (args[0].toLowerCase()) {
                case "установить":
                case "удалить":
                case "обновить":
                case "инфо":
                    // Можно добавить список плагинов из конфига
                    completions.add("OperatorFromPerms");
                    completions.add("WorldEdit");
                    completions.add("LuckPerms");
                    break;
            }
        }
        
        return completions;
    }
}
