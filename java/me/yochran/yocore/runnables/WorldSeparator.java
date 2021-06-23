package me.yochran.yocore.runnables;

import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldSeparator extends BukkitRunnable {

    private final yoCore plugin;

    public WorldSeparator() {

        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void run() {
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            for (Player player2 : Bukkit.getOnlinePlayers()) {
                if (!plugin.worldData.config.getString("Servers." + player1.getWorld().getName() + ".World").equalsIgnoreCase(player2.getWorld().getName()))
                    player1.hidePlayer(player2);
                else {
                    if (!plugin.vanished_players.contains(player2.getUniqueId()))
                        player1.showPlayer(player2);
                }
            }
        }
    }
}
