package me.yochran.yocore.runnables;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TPAUpdater extends BukkitRunnable {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public TPAUpdater() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void run() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (plugin.tpa.containsKey(players.getUniqueId())
                    && plugin.tpa_timer.containsKey(players.getUniqueId())
                    && plugin.tpa_coords.containsKey(players.getUniqueId())) {
                if (plugin.tpa_timer.get(players.getUniqueId()) <= 0) {
                    players.teleport(Bukkit.getPlayer(plugin.tpa.get(players.getUniqueId())).getLocation());

                    players.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequestTeleported")
                            .replace("%target%", playerManagement.getPlayerColor(Bukkit.getPlayer(plugin.tpa.get(players.getUniqueId()))))));

                    plugin.tpa_timer.remove(players.getUniqueId());
                    plugin.tpa_coords.remove(players.getUniqueId());
                    plugin.tpa.remove(players.getUniqueId());
                } else plugin.tpa_timer.put(players.getUniqueId(), plugin.tpa_timer.get(players.getUniqueId()) - 1);
            }
        }
    }
}
