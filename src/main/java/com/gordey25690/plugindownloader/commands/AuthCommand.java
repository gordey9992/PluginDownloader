package com.gordey25690.plugindownloader.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import com.gordey25690.plugindownloader.PluginDownloader;
import com.gordey25690.plugindownloader.managers.AuthManager;

public class AuthCommand implements CommandExecutor {
    private final PluginDownloader plugin;
    private final AuthManager authManager;

    public AuthCommand(PluginDownloader plugin) {
        this.plugin = plugin;
        this.authManager = plugin.getAuthManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эта команда только для игроков!");
            return true;
        }

        Player player = (Player) sender;
        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "login":
            case "l":
            case "логин":
            case "вход":
                return handleLogin(player, args);
                
            case "register":
            case "reg":
            case "рег":
            case "регистрация":
                return handleRegister(player, args);
                
            case "rememberme":
            case "rm":
            case "запомнить":
                return handleRememberMe(player, args);
                
            case "resetpassword":
            case "rp":
            case "сброспароль":
            case "сброс":
                return handleResetPassword(player, args);
        }

        return false;
    }

    private boolean handleLogin(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "❌ Использование: /login <пароль>");
            player.sendMessage(ChatColor.GRAY + "Алиасы: /l, /вход, /логин");
            return true;
        }

        String password = args[0];
        authManager.authenticatePlayer(player, password);
        return true;
    }

    private boolean handleRegister(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "❌ Использование: /register <пароль> <подтверждение>");
            player.sendMessage(ChatColor.GRAY + "Алиасы: /reg, /рег, /регистрация");
            return true;
        }

        String password = args[0];
        String confirmPassword = args[1];
        authManager.registerPlayer(player, password, confirmPassword);
        return true;
    }

    private boolean handleRememberMe(Player player, String[] args) {
        authManager.rememberPlayer(player);
        return true;
    }

    private boolean handleResetPassword(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "❌ Использование: /resetpassword <игрок>");
            player.sendMessage(ChatColor.GRAY + "Алиасы: /rp, /сброспароль, /сброс");
            return true;
        }

        String targetPlayer = args[0];
        authManager.resetPlayerPassword(player, targetPlayer);
        return true;
    }
}
