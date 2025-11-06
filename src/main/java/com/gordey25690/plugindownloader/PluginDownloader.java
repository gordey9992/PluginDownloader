package com.gordey25690.plugindownloader.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import com.gordey25690.plugindownloader.PluginDownloader;

public class AuthListener implements Listener {
    private final PluginDownloader plugin;

    public AuthListener(PluginDownloader plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getAuthManager().onPlayerJoin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getAuthManager().onPlayerQuit(player);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();
        
        if (!plugin.getAuthManager().canUseCommand(player, command)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "❌ Вы не можете использовать команды до авторизации!");
            player.sendMessage(ChatColor.GRAY + "Доступные команды: /login, /register, /rememberme");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getAuthManager().canBuild(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "❌ Вы не можете ломать блоки до авторизации!");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getAuthManager().canBuild(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "❌ Вы не можете ставить блоки до авторизации!");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getAuthManager().canMove(player)) {
            // Блокируем движение если изменились координаты X или Z
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                event.setTo(event.getFrom());
                player.sendMessage(ChatColor.RED + "❌ Вы не можете двигаться до авторизации!");
            }
        }
    }
}
