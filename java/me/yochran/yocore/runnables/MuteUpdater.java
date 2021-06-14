package me.yochran.yocore.runnables;

import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class MuteUpdater extends BukkitRunnable {

    private final yoCore plugin;
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

    public MuteUpdater() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void run() {
        if (plugin.punishmentData.config.contains("MutedPlayers")) {
            for (String players : plugin.punishmentData.config.getConfigurationSection("MutedPlayers").getKeys(false)) {
                if (plugin.punishmentData.config.getBoolean("MutedPlayers." + players + ".Temporary")) {
                    if (plugin.punishmentData.config.getLong(players + ".Mute." + punishmentManagement.getInfractionAmount(Bukkit.getOfflinePlayer(UUID.fromString(players)), "Mute") + ".Duration") <= System.currentTimeMillis()) {
                        plugin.muted_players.remove(UUID.fromString(players));
                        plugin.punishmentData.config.set("MutedPlayers." + players, null);
                        plugin.punishmentData.config.set(players + ".Mute." + punishmentManagement.getInfractionAmount(Bukkit.getOfflinePlayer(UUID.fromString(players)), "Mute") + ".Status", "Revoked");
                        plugin.punishmentData.saveData();
                    }
                }
            }
        }
    }
}
