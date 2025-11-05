package com.gordey25690.plugindownloader.ui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.FileConfiguration;
import com.gordey25690.plugindownloader.PluginDownloader;
import com.gordey25690.plugindownloader.utils.MessageUtils;
import java.util.Arrays;

public class PluginSelectionGUI {
    
    private static final PluginDownloader plugin = PluginDownloader.getInstance();
    
    public static void openPluginSelection(Player player) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        Inventory gui = Bukkit.createInventory(null, 27, MessageUtils.colorize("&6Магазин Плагинов"));
        
        int slot = 0;
        for (String pluginName : config.getConfigurationSection("библиотека-плагинов").getKeys(false)) {
            if (slot >= 27) break;
            
            String author = config.getString("библиотека-плагинов." + pluginName + ".автор");
            String version = config.getString("библиотека-плагинов." + pluginName + ".версия");
            String description = config.getString("библиотека-плагинов." + pluginName + ".описание");
            
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(MessageUtils.colorize("&e" + pluginName));
            
            boolean isInstalled = new java.io.File("plugins", pluginName + ".jar").exists();
            String status = isInstalled ? "&aУстановлен" : "&cНе установлен";
            
            meta.setLore(Arrays.asList(
                MessageUtils.colorize("&7Версия: &f" + version),
                MessageUtils.colorize("&7Автор: &f" + author),
                MessageUtils.colorize("&7Статус: " + status),
                MessageUtils.colorize("&7Описание: &f" + description),
                "",
                MessageUtils.colorize("&aЛКМ - Установить"),
                MessageUtils.colorize("&eПКМ - Информация")
            ));
            
            item.setItemMeta(meta);
            gui.setItem(slot, item);
            slot++;
        }
        
        // Кнопка выхода
        ItemStack exitItem = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitItem.getItemMeta();
        exitMeta.setDisplayName(MessageUtils.colorize("&cЗакрыть"));
        exitItem.setItemMeta(exitMeta);
        gui.setItem(26, exitItem);
        
        player.openInventory(gui);
    }
}
