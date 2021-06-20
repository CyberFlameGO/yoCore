package me.yochran.yocore.management;

import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class GrantManagement {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public GrantManagement() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setupPlayer(OfflinePlayer target) {
        plugin.grantData.config.set(target.getUniqueId().toString() + ".GrantsAmount", 0);
        plugin.grantData.saveData();
    }

    public int getGrantsAmount(OfflinePlayer target) {
        if (!plugin.playerData.config.contains(target.getUniqueId().toString()))
            return 0;

        if (!plugin.grantData.config.contains(target.getUniqueId().toString()))
            return 0;

        return plugin.grantData.config.getInt(target.getUniqueId().toString() + ".GrantsAmount");
    }

    public void addGrant(OfflinePlayer target, String executor, String type, String grant, Object duration, long date, String reason, String previousRank) {
        int ID = getGrantsAmount(target) + 1;

        plugin.grantData.config.set(target.getUniqueId().toString() + ".GrantsAmount", ID);
        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + ID + ".ID", ID);
        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + ID + ".Type", type);
        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + ID + ".Grant", grant);
        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + ID + ".Executor", executor);
        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + ID + ".Duration", duration);
        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + ID + ".Date", date);
        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + ID + ".Reason", reason);
        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + ID + ".PreviousRank", previousRank);
        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + ID + ".Status", "Active");

        plugin.grantData.saveData();
    }

    public long getGrantDuration(String duration) {
        long ms = (3600 * 1000) + System.currentTimeMillis();

        if (duration.equalsIgnoreCase("10 Seconds"))
            return 1000 * 10 + System.currentTimeMillis();
        if (duration.equalsIgnoreCase("1 Minute"))
            return 1000 * 60 + System.currentTimeMillis();
        if (duration.equalsIgnoreCase("5 Minutes"))
            return (1000 * 60) * 5 + System.currentTimeMillis();
        if (duration.equalsIgnoreCase("30 Minutes"))
            return (1000 * 60) * 30 + System.currentTimeMillis();
        if (duration.equalsIgnoreCase("1 Hour"))
            return (1000 * 60) * 60 + System.currentTimeMillis();
        if (duration.equalsIgnoreCase("3 Hours"))
            return ((1000 * 60) * 60) * 3 + System.currentTimeMillis();
        if (duration.equalsIgnoreCase("1 Day"))
            return ((1000 * 60) * 60) * 24 + System.currentTimeMillis();
        if (duration.equalsIgnoreCase("1 Week"))
            return (((1000 * 60) * 60) * 24) * 7 + System.currentTimeMillis();
        if (duration.equalsIgnoreCase("1 Month"))
            return (((1000 * 60) * 60) * 24) * 31 + System.currentTimeMillis();
        if (duration.equalsIgnoreCase("1 Year"))
            return ((((1000 * 60) * 60) * 24) * 7) * 52 + System.currentTimeMillis();

        return ms;
    }

    public void revokeGrant(OfflinePlayer target, int id) {
        if (!plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + id + ".Status").equalsIgnoreCase("Active"))
            return;

        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + id + ".Status", "Revoked");
        plugin.grantData.saveData();

        if (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + id + ".Type").equalsIgnoreCase("RANK"))
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + target.getName() + " " + plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + id + ".PreviousRank"));
        else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + target.getName() + " remove " + plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + id + ".Grant"));
    }

    public void clearHistory(OfflinePlayer target) {
        setupPlayer(target);

        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants", null);
        plugin.grantData.saveData();
    }
}
