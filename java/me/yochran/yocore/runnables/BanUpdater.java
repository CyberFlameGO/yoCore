package me.yochran.yocore.runnables;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.punishments.Punishment;
import me.yochran.yocore.punishments.PunishmentType;
import me.yochran.yocore.yoCore;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class BanUpdater extends BukkitRunnable {

    private final yoCore plugin;

    public BanUpdater() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void run() {
        if (plugin.punishmentData.config.contains("BannedPlayers")) {
            for (String entry : plugin.punishmentData.config.getConfigurationSection("BannedPlayers").getKeys(false)) {
                yoPlayer player = new yoPlayer(UUID.fromString(entry));

                if (Punishment.getPunishments(player).size() <= 0)
                    return;

                for (Map.Entry<Integer, Punishment> punishment : Punishment.getPunishments(player).entrySet()) {
                    if (punishment.getValue().getType() == PunishmentType.BAN) {
                        if (punishment.getValue().getStatus().equalsIgnoreCase("Active")) {
                            if (!(punishment.getValue().getDuration() instanceof String)) {
                                if ((long) punishment.getValue().getDuration() <= System.currentTimeMillis())
                                    punishment.getValue().expire();
                            }
                        }
                    }
                }
            }
        }
    }
}
