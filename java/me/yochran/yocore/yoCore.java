package me.yochran.yocore;

import me.yochran.yocore.commands.SetrankCommand;
import me.yochran.yocore.commands.punishments.*;
import me.yochran.yocore.data.PlayerData;
import me.yochran.yocore.data.PunishmentData;
import me.yochran.yocore.listeners.PlayerChatListener;
import me.yochran.yocore.listeners.PlayerLogListener;
import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.runnables.BanUpdater;
import me.yochran.yocore.runnables.MuteUpdater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class yoCore extends JavaPlugin {

    public PlayerData playerData;
    public PunishmentData punishmentData;

    private PunishmentManagement punishmentManagement;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("yoCore v1.0 by Yochran is loading...");

        saveDefaultConfig();
        registerData();
        registerCommands();
        registerListeners();
        runRunnables();
        registerRanks();

        punishmentManagement = new PunishmentManagement();

        refreshPunishments();

        Bukkit.getConsoleSender().sendMessage("yoCore v1.0 by Yochran has successfully loaded.");
    }

    public List<String> ranks = new ArrayList<>();
    public Map<UUID, Boolean> muted_players = new HashMap();
    public Map<UUID, Boolean> banned_players = new HashMap<>();
    public Map<String, String> blacklisted_ips = new HashMap<>();

    private void registerCommands() {
        getCommand("Setrank").setExecutor(new SetrankCommand());
        getCommand("Warn").setExecutor(new WarnCommand());
        getCommand("Mute").setExecutor(new MuteCommand());
        getCommand("Unmute").setExecutor(new UnmuteCommand());
        getCommand("Tempmute").setExecutor(new TempmuteCommand());
        getCommand("Ban").setExecutor(new BanCommand());
        getCommand("Unban").setExecutor(new UnbanCommand());
        getCommand("Tempban").setExecutor(new TempbanCommand());
        getCommand("Blacklist").setExecutor(new BlacklistCommand());
        getCommand("Unblacklist").setExecutor(new UnblacklistCommand());
    }

    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerLogListener(), this);
        manager.registerEvents(new PlayerChatListener(), this);
    }

    private void runRunnables() {
        new MuteUpdater().runTaskTimer(this, 10, 20);
        new BanUpdater().runTaskTimer(this, 10, 20);
    }

    private void registerData() {
        playerData = new PlayerData();

        playerData.setupData();
        playerData.saveData();
        playerData.reloadData();

        punishmentData = new PunishmentData();

        punishmentData.setupData();
        punishmentData.saveData();
        punishmentData.reloadData();

        new BukkitRunnable() {
            @Override
            public void run() { playerData.saveData(); }
        }.runTaskLater(this, 10);
        new BukkitRunnable() {
            @Override
            public void run() { punishmentData.saveData(); }
        }.runTaskLater(this, 10);
    }

    private void registerRanks() { ranks.addAll(getConfig().getConfigurationSection("Ranks").getKeys(false)); }

    private void refreshPunishments() {
        if (punishmentData.config.contains("MutedPlayers")) {
            for (String player : punishmentData.config.getConfigurationSection("MutedPlayers").getKeys(false)) {
                muted_players.put(UUID.fromString(player), punishmentData.config.getBoolean("MutedPlayers." + player + ".Temporary"));
            }
        }

        if (punishmentData.config.contains("BannedPlayers")) {
            for (String player : punishmentData.config.getConfigurationSection("BannedPlayers").getKeys(false)) {
                banned_players.put(UUID.fromString(player), punishmentData.config.getBoolean("BannedPlayers." + player + ".Temporary"));
            }
        }

        if (punishmentData.config.contains("BlacklistedPlayers")) {
            for (String uuid : punishmentData.config.getConfigurationSection("BlacklistedPlayers").getKeys(false)) {
                blacklisted_ips.put(punishmentData.config.getString("BlacklistedPlayers." + uuid + ".IP"), punishmentData.config.getString(uuid + ".Blacklist." + punishmentManagement.getInfractionAmount(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), "Blacklist") + ".Reason"));
            }
        }
    }
}
