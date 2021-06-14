package me.yochran.yocore.management;

import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;

public class PunishmentManagement {

    private final yoCore plugin;

    public PunishmentManagement() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setupPlayer(OfflinePlayer player) {
        plugin.punishmentData.config.set(player.getUniqueId().toString() + ".Name", player.getName());
        plugin.punishmentData.config.set(player.getUniqueId().toString() + ".WarnsAmount", 0);
        plugin.punishmentData.config.set(player.getUniqueId().toString() + ".KicksAmount", 0);
        plugin.punishmentData.config.set(player.getUniqueId().toString() + ".MutesAmount", 0);
        plugin.punishmentData.config.set(player.getUniqueId().toString() + ".BansAmount", 0);
        plugin.punishmentData.config.set(player.getUniqueId().toString() + ".BlacklistsAmount", 0);
        plugin.punishmentData.saveData();
    }

    public int getInfractionAmount(OfflinePlayer player, String type) {
        int amount = 0;

        switch (type.toUpperCase()) {
            case "WARN":
                amount = plugin.punishmentData.config.getInt(player.getUniqueId().toString() + ".WarnsAmount");
                break;
            case "KICK":
                amount = plugin.punishmentData.config.getInt(player.getUniqueId().toString() + ".KicksAmount");
                break;
            case "MUTE":
                amount = plugin.punishmentData.config.getInt(player.getUniqueId().toString() + ".MutesAmount");
                break;
            case "BAN":
                amount = plugin.punishmentData.config.getInt(player.getUniqueId().toString() + ".BansAmount");
                break;
            case "BLACKLIST":
                amount = plugin.punishmentData.config.getInt(player.getUniqueId().toString() + ".BlacklistsAmount");
                break;
        }

        return amount;
    }

    public void addInfraction(String type, OfflinePlayer target, String executor, String reason, long date, Object duration, boolean silent) {
        int ID = getInfractionAmount(target, type) + 1;
        plugin.punishmentData.config.set(target.getUniqueId().toString() + "." + type + "sAmount", ID);

        plugin.punishmentData.config.set(target.getUniqueId().toString() + "." + type + "." + ID + ".Executor", executor);
        plugin.punishmentData.config.set(target.getUniqueId().toString() + "." + type + "." + ID + ".Reason", reason);
        plugin.punishmentData.config.set(target.getUniqueId().toString() + "." + type + "." + ID + ".Silent", silent);
        plugin.punishmentData.config.set(target.getUniqueId().toString() + "." + type + "." + ID + ".Date", System.currentTimeMillis());
        plugin.punishmentData.config.set(target.getUniqueId().toString() + "." + type + "." + ID + ".Duration", duration);
        if (type.equalsIgnoreCase("Kick")) plugin.punishmentData.config.set(target.getUniqueId().toString() + "." + type + "." + ID + ".Status", "Expired");
        else plugin.punishmentData.config.set(target.getUniqueId().toString() + "." + type + "." + ID + ".Status", "Active");

        plugin.punishmentData.saveData();
    }

    public void addMute(OfflinePlayer target, boolean temporary) {
        plugin.punishmentData.config.set("MutedPlayers." + target.getUniqueId().toString() + ".Name", target.getName());
        plugin.punishmentData.config.set("MutedPlayers." + target.getUniqueId().toString() + ".Temporary", temporary);

        plugin.punishmentData.saveData();

        plugin.muted_players.put(target.getUniqueId(), temporary);
    }

    public void addBan(OfflinePlayer target, boolean temporary) {
        plugin.punishmentData.config.set("BannedPlayers." + target.getUniqueId().toString() + ".Name", target.getName());
        plugin.punishmentData.config.set("BannedPlayers." + target.getUniqueId().toString() + ".Temporary", temporary);

        plugin.punishmentData.saveData();

        plugin.banned_players.put(target.getUniqueId(), temporary);
    }

    public void addBlacklist(OfflinePlayer target, String targetIP, String reason) {
        plugin.punishmentData.config.set("BlacklistedPlayers." + target.getUniqueId().toString() + ".Name", target.getName());
        plugin.punishmentData.config.set("BlacklistedPlayers." + target.getUniqueId().toString() + ".IP", targetIP);
        plugin.punishmentData.saveData();

        plugin.blacklisted_ips.put(targetIP, reason);
    }

    public void clearHistory(OfflinePlayer target) {
        setupPlayer(target);

        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Warn", null);
        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Mute", null);
        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Kick", null);
        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Ban", null);
        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Blacklist", null);

        plugin.punishmentData.saveData();
    }
}
