package me.yochran.yocore.runnables;

import me.yochran.yocore.management.GrantManagement;
import me.yochran.yocore.management.PermissionManagement;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class GrantUpdater extends BukkitRunnable {

    private final yoCore plugin;
    private final GrantManagement grantManagement = new GrantManagement();
    private final PermissionManagement permissionManagement = new PermissionManagement();

    public GrantUpdater() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void run() {
        for (String entry : plugin.grantData.config.getKeys(false)) {
            if (plugin.grantData.config.contains(entry + ".Grants")) {
                if (!(plugin.grantData.config.get(entry + ".Grants." + grantManagement.getGrantsAmount(Bukkit.getOfflinePlayer(UUID.fromString(entry))) + ".Duration") instanceof String)) {
                    if (plugin.grantData.config.getLong(entry + ".Grants." + grantManagement.getGrantsAmount(Bukkit.getOfflinePlayer(UUID.fromString(entry))) + ".Duration") <= System.currentTimeMillis() && plugin.grantData.config.getString(entry + ".Grants." + grantManagement.getGrantsAmount(Bukkit.getOfflinePlayer(UUID.fromString(entry))) + ".Status").equalsIgnoreCase("Active")) {
                        plugin.grantData.config.set(entry + ".Grants." + grantManagement.getGrantsAmount(Bukkit.getOfflinePlayer(UUID.fromString(entry))) + ".Status", "Expired");
                        plugin.grantData.saveData();
                        if (plugin.grantData.config.getString(entry + ".Grants." + grantManagement.getGrantsAmount(Bukkit.getOfflinePlayer(UUID.fromString(entry))) + ".Type").equalsIgnoreCase("RANK"))
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + Bukkit.getOfflinePlayer(UUID.fromString(entry)).getName() + " " + plugin.grantData.config.getString(entry + ".Grants." + grantManagement.getGrantsAmount(Bukkit.getOfflinePlayer(UUID.fromString(entry))) + ".PreviousRank"));
                        else {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "user " + Bukkit.getOfflinePlayer(UUID.fromString(entry)).getName() + " remove " + plugin.grantData.config.getString(entry + ".Grants." + grantManagement.getGrantsAmount(Bukkit.getOfflinePlayer(UUID.fromString(entry))) + ".Grant"));
                            if (Bukkit.getPlayer(UUID.fromString(entry)) != null) permissionManagement.refreshPlayer(Bukkit.getPlayer(UUID.fromString(entry)));
                        }
                    }
                }
            }
        }
    }
}
