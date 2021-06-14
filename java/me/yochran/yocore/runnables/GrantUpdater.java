package me.yochran.yocore.runnables;

import me.yochran.yocore.management.GrantManagement;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class GrantUpdater extends BukkitRunnable {

    private final yoCore plugin;
    private final GrantManagement grantManagement = new GrantManagement();

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
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + Bukkit.getOfflinePlayer(UUID.fromString(entry)).getName() + " " + plugin.grantData.config.getString(entry + ".Grants." + grantManagement.getGrantsAmount(Bukkit.getOfflinePlayer(UUID.fromString(entry))) + ".PreviousRank"));
                    }
                }
            }
        }
    }
}
