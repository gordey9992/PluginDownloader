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
import com.gordey25690.plugindownloader.PluginDownloader;

public class AuthListener implements Listener {
    private final PluginDownloader plugin;

    public AuthListener(PluginDownloader plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getAuthManager().onPlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getAuthManager().onPlayerQuit(event.getPlayer());
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getAuthManager().canUseCommand(player, event.getMessage())) {
            event.setCancelled(true);
            player.sendMessage("❌ Вы не можете использовать команды до авторизации!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getAuthManager().canBuild(player)) {
            event.setCancelled(true);
            player.sendMessage("❌ Вы не можете ломать блоки до авторизации!");
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getAuthManager().canBuild(player)) {
            event.setCancelled(true);
            player.sendMessage("❌ Вы не можете ставить блоки до авторизации!");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getAuthManager().canMove(player)) {
            // Блокируем движение если изменились координаты
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                event.setTo(event.getFrom());
            }
        }
    }
}
