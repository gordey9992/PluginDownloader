package com.gordey25690.plugindownloader.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import com.gordey25690.plugindownloader.PluginDownloader;
import java.util.regex.Pattern;

public class MessageUtils {
    
    private static PluginDownloader plugin = PluginDownloader.getInstance();
    private static final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");
    
    public static String colorize(String message) {
        if (message == null) return "";
        
        // Поддержка HEX цветов
        message = HEX_PATTERN.matcher(message).replaceAll(result -> {
            final String hexCode = result.group().substring(1);
            return net.md_5.bungee.api.ChatColor.of(hexCode).toString();
        });
        
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static void sendMessage(Player player, String key) {
        sendMessage(player, key, null);
    }
    
    public static void sendMessage(Player player, String key, String[] replacements) {
        FileConfiguration messages = plugin.getConfigManager().getMessages();
        String prefix = messages.getString("префикс", "&6[PluginDownloader]&r");
        String message = messages.getString("сообщения." + key, "&cСообщение не найдено: " + key);
        
        if (replacements != null) {
            for (int i = 0; i < replacements.length; i += 2) {
                if (i + 1 < replacements.length) {
                    message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
                }
            }
        }
        
        player.sendMessage(colorize(prefix + " " + message));
    }
    
    public static String getMessage(String key) {
        FileConfiguration messages = plugin.getConfigManager().getMessages();
        return colorize(messages.getString("сообщения." + key, "&cСообщение не найдено: " + key));
    }
}
