package me.yochran.yocore.runnables;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.punishments.Punishment;
import me.yochran.yocore.punishments.PunishmentType;
import me.yochran.yocore.yoCore;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class MuteUpdater extends BukkitRunnable {

    private final yoCore plugin;

    public MuteUpdater() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void run() {
        if (plugin.punishmentData.config.contains("MutedPlayers")) {
            for (String entry : plugin.punishmentData.config.getConfigurationSection("MutedPlayers").getKeys(false)) {
                yoPlayer player = new yoPlayer(UUID.fromString(entry));

                if (Punishment.getPunishments(player).size() <= 0)
                    return;

                for (Map.Entry<Integer, Punishment> punishment : Punishment.getPunishments(player).entrySet()) {
                    if (punishment.getValue().getType() == PunishmentType.MUTE) {
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
