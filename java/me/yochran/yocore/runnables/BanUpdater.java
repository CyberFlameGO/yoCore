package me.yochran.yocore.runnables;

import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class BanUpdater extends BukkitRunnable {

    private final yoCore plugin;
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

    public BanUpdater() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void run() {
        if (plugin.punishmentData.config.contains("BannedPlayers")) {
            for (String players : plugin.punishmentData.config.getConfigurationSection("BannedPlayers").getKeys(false)) {
                if (plugin.punishmentData.config.getBoolean("BannedPlayers." + players + ".Temporary")) {
                    if (plugin.punishmentData.config.getLong(players + ".Ban." + punishmentManagement.getInfractionAmount(Bukkit.getOfflinePlayer(UUID.fromString(players)), "Ban") + ".Duration") <= System.currentTimeMillis()) {
                        plugin.banned_players.remove(UUID.fromString(players));
                        plugin.punishmentData.config.set("BannedPlayers." + players, null);
                        plugin.punishmentData.config.set(players + ".Ban." + punishmentManagement.getInfractionAmount(Bukkit.getOfflinePlayer(UUID.fromString(players)), "Ban") + ".Status", "Expired");
                        plugin.punishmentData.saveData();
                    }
                }
            }
        }
    }
}
