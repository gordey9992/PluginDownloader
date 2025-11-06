package com.gordey25690.plugindownloader.managers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;
import com.gordey25690.plugindownloader.PluginDownloader;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class AuthManager {
    private final PluginDownloader plugin;
    private final Set<UUID> authenticatedPlayers;
    private final Set<UUID> registeredPlayers;
    private final Map<UUID, String> playerSessions; // UUID -> сессия (IP+ник)
    private final Map<UUID, Long> sessionExpiry; // UUID -> время истечения сессии
    private File authFile;
    private FileConfiguration authConfig;

    public AuthManager(PluginDownloader plugin) {
        this.plugin = plugin;
        this.authenticatedPlayers = new HashSet<>();
        this.registeredPlayers = new HashSet<>();
        this.playerSessions = new HashMap<>();
        this.sessionExpiry = new HashMap<>();
        setupAuthFile();
        loadRegisteredPlayers();
    }

    private void setupAuthFile() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        authFile = new File(plugin.getDataFolder(), "auth.yml");
        if (!authFile.exists()) {
            try {
                authFile.createNewFile();
                // Создаем базовую структуру
                authConfig.set("auth-enabled", true);
                authConfig.save(authFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать файл авторизации: " + e.getMessage());
            }
        }
        authConfig = YamlConfiguration.loadConfiguration(authFile);
    }

    private void loadRegisteredPlayers() {
        if (authConfig.contains("players")) {
            for (String uuidStr : authConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    registeredPlayers.add(uuid);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Неверный UUID в auth.yml: " + uuidStr);
                }
            }
        }
    }

    private void saveAuthData() {
        try {
            authConfig.save(authFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить файл авторизации: " + e.getMessage());
        }
    }

    public boolean isAuthEnabled() {
        return authConfig.getBoolean("auth-enabled", true);
    }

    public void setAuthEnabled(boolean enabled) {
        authConfig.set("auth-enabled", enabled);
        saveAuthData();
    }

    public boolean isPlayerRegistered(Player player) {
        return registeredPlayers.contains(player.getUniqueId());
    }

    public boolean isPlayerAuthenticated(Player player) {
        UUID uuid = player.getUniqueId();
        
        // Проверяем активную сессию
        if (playerSessions.containsKey(uuid)) {
            Long expiryTime = sessionExpiry.get(uuid);
            if (expiryTime != null && System.currentTimeMillis() < expiryTime) {
                String sessionKey = playerSessions.get(uuid);
                String currentKey = getPlayerSessionKey(player);
                
                if (sessionKey.equals(currentKey)) {
                    return true;
                } else {
                    // Сессия невалидна - удаляем
                    playerSessions.remove(uuid);
                    sessionExpiry.remove(uuid);
                }
            } else {
                // Сессия истекла
                playerSessions.remove(uuid);
                sessionExpiry.remove(uuid);
            }
        }
        
        return authenticatedPlayers.contains(uuid);
    }

    private String getPlayerSessionKey(Player player) {
        String ip = player.getAddress().getAddress().getHostAddress();
        return ip + ":" + player.getName().toLowerCase();
    }

    public boolean registerPlayer(Player player, String password, String confirmPassword) {
        UUID uuid = player.getUniqueId();
        
        if (isPlayerRegistered(player)) {
            player.sendMessage(ChatColor.RED + "❌ Вы уже зарегистрированы! Используйте /login");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            player.sendMessage(ChatColor.RED + "❌ Пароли не совпадают!");
            return false;
        }

        if (password.length() < 4) {
            player.sendMessage(ChatColor.RED + "❌ Пароль должен быть не менее 4 символов!");
            return false;
        }

        registeredPlayers.add(uuid);
        authenticatedPlayers.add(uuid);
        
        authConfig.set("players." + uuid.toString() + ".password", password);
        authConfig.set("players." + uuid.toString() + ".username", player.getName());
        authConfig.set("players." + uuid.toString() + ".registered", System.currentTimeMillis());
        saveAuthData();

        removeAuthEffects(player);
        playSuccessMusic(player);
        
        player.sendMessage(ChatColor.GREEN + "✅ Регистрация успешна! Добро пожаловать, " + player.getName() + "!");
        player.sendMessage(ChatColor.GRAY + "💡 Используйте /rememberme чтобы сервер запомнил вас на 1 день");
        return true;
    }

    public boolean authenticatePlayer(Player player, String password) {
        if (!isPlayerRegistered(player)) {
            player.sendMessage(ChatColor.RED + "❌ Вы не зарегистрированы! Используйте /register <пароль> <подтверждение>");
            return false;
        }

        String storedPassword = authConfig.getString("players." + player.getUniqueId() + ".password");
        if (storedPassword != null && storedPassword.equals(password)) {
            authenticatedPlayers.add(player.getUniqueId());
            removeAuthEffects(player);
            playSuccessMusic(player);
            
            player.sendMessage(ChatColor.GREEN + "✅ Авторизация успешна! Добро пожаловать, " + player.getName() + "!");
            player.sendMessage(ChatColor.GRAY + "💡 Используйте /rememberme чтобы сервер запомнил вас на 1 день");
            return true;
        }

        player.sendMessage(ChatColor.RED + "❌ Неверный пароль!");
        return false;
    }

    public boolean rememberPlayer(Player player) {
        if (!isPlayerAuthenticated(player)) {
            player.sendMessage(ChatColor.RED + "❌ Сначала авторизуйтесь с помощью /login!");
            return false;
        }

        UUID uuid = player.getUniqueId();
        String sessionKey = getPlayerSessionKey(player);
        long expiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24 часа

        playerSessions.put(uuid, sessionKey);
        sessionExpiry.put(uuid, expiryTime);

        player.sendMessage(ChatColor.GREEN + "✅ Сервер запомнил вас на 24 часа!");
        player.sendMessage(ChatColor.GRAY + "💡 При смене IP или ника потребуется повторная авторизация");
        return true;
    }

    public boolean resetPlayerPassword(Player admin, String targetPlayerName) {
        if (!admin.hasPermission("plugindownloader.resetpassword")) {
            admin.sendMessage(ChatColor.RED + "❌ У вас нет прав для сброса паролей!");
            return false;
        }

        // Ищем игрока по имени
        for (String uuidStr : authConfig.getConfigurationSection("players").getKeys(false)) {
            String username = authConfig.getString("players." + uuidStr + ".username");
            if (targetPlayerName.equalsIgnoreCase(username)) {
                try {
                    UUID targetUuid = UUID.fromString(uuidStr);
                    
                    // Удаляем сессию и аутентификацию
                    authenticatedPlayers.remove(targetUuid);
                    playerSessions.remove(targetUuid);
                    sessionExpiry.remove(targetUuid);
                    
                    // Сбрасываем пароль на "12345"
                    authConfig.set("players." + uuidStr + ".password", "12345");
                    saveAuthData();

                    admin.sendMessage(ChatColor.GREEN + "✅ Пароль игрока " + targetPlayerName + " сброшен на '12345'");
                    admin.sendMessage(ChatColor.YELLOW + "📝 Сообщите игроку новый пароль!");
                    
                    // Уведомляем игрока если онлайн
                    Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        applyAuthEffects(targetPlayer);
                        targetPlayer.sendMessage(ChatColor.RED + "🔒 Ваш пароль был сброшен администратором!");
                        targetPlayer.sendMessage(ChatColor.YELLOW + "📝 Используйте /login 12345 для входа");
                    }
                    
                    return true;
                } catch (IllegalArgumentException e) {
                    admin.sendMessage(ChatColor.RED + "❌ Ошибка при сбросе пароля!");
                    return false;
                }
            }
        }

        admin.sendMessage(ChatColor.RED + "❌ Игрок " + targetPlayerName + " не найден или не зарегистрирован!");
        return false;
    }

    public void applyAuthEffects(Player player) {
        if (!isAuthEnabled() || isPlayerAuthenticated(player)) {
            return;
        }

        // Полная блокировка движения и действий
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 255, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 255, true, false));

        // Запуск музыки
        playAuthMusic(player);
        
        // Сообщение
        player.sendMessage(ChatColor.YELLOW + "🔒 Требуется авторизация!");
        if (isPlayerRegistered(player)) {
            player.sendMessage(ChatColor.GRAY + "Используйте: " + ChatColor.WHITE + "/login <пароль>");
            player.sendMessage(ChatColor.GRAY + "Доступные команды: " + ChatColor.WHITE + "/login, /l, /вход, /логин");
        } else {
            player.sendMessage(ChatColor.GRAY + "Используйте: " + ChatColor.WHITE + "/register <пароль> <подтверждение>");
            player.sendMessage(ChatColor.GRAY + "Доступные команды: " + ChatColor.WHITE + "/register, /reg, /рег, /регистрация");
        }
        player.sendMessage(ChatColor.RED + "⚠️ Вы не можете двигаться и использовать команды до авторизации!");
    }

    public void removeAuthEffects(Player player) {
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.JUMP);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
    }

    private void playAuthMusic(Player player) {
        new BukkitRunnable() {
            int note = 0;
            final int[] melody = {0, 4, 7, 4, 0, 4, 7, 12, 7, 4, 0}; // Красивая арпеджио
            
            @Override
            public void run() {
                if (!player.isOnline() || isPlayerAuthenticated(player)) {
                    this.cancel();
                    return;
                }

                if (note < melody.length) {
                    float pitch = (float) Math.pow(2.0, (melody[note] - 12) / 12.0);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 0.8f, pitch);
                    note++;
                } else {
                    note = 0; // Повтор мелодии
                    // Небольшая пауза перед повторением
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 12L); // Каждые 0.6 секунды
    }

    private void playSuccessMusic(Player player) {
        int[] successMelody = {12, 14, 16, 19, 16, 14, 12}; // Красивая завершающая мелодия
        
        for (int i = 0; i < successMelody.length; i++) {
            final int noteIndex = i;
            new BukkitRunnable() {
                @Override
                public void run() {
                    float pitch = (float) Math.pow(2.0, (successMelody[noteIndex] - 12) / 12.0);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, pitch);
                }
            }.runTaskLater(plugin, i * 3L); // Растягиваем мелодию
        }
    }

    public void onPlayerJoin(Player player) {
        if (isAuthEnabled()) {
            if (!isPlayerAuthenticated(player)) {
                applyAuthEffects(player);
            }
        }
    }

    public void onPlayerQuit(Player player) {
        // При выходе с сервера сбрасываем аутентификацию (но не сессии)
        authenticatedPlayers.remove(player.getUniqueId());
    }

    // Проверка можно ли использовать команду
    public boolean canUseCommand(Player player, String command) {
        if (!isAuthEnabled()) return true;
        if (isPlayerAuthenticated(player)) return true;
        
        // Разрешаем только команды авторизации
        String cmd = command.toLowerCase().replace("/", "");
        return cmd.equals("login") || cmd.equals("l") || cmd.equals("логин") || cmd.equals("вход") ||
               cmd.equals("register") || cmd.equals("reg") || cmd.equals("рег") || cmd.equals("регистрация") ||
               cmd.equals("rememberme") || cmd.equals("rm") || cmd.equals("запомнить");
    }

    // Проверка можно ли ломать/ставить блоки
    public boolean canBuild(Player player) {
        return !isAuthEnabled() || isPlayerAuthenticated(player);
    }

    // Проверка можно ли двигаться
    public boolean canMove(Player player) {
        return !isAuthEnabled() || isPlayerAuthenticated(player);
    }
}
