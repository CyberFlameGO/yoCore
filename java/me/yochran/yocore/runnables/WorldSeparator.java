package me.yochran.yocore.runnables;

import me.yochran.yocore.management.ServerManagement;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldSeparator extends BukkitRunnable {

    private final yoCore plugin;
    private final ServerManagement serverManagement = new ServerManagement();

    public WorldSeparator() {

        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void run() {
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            for (Player player2 : Bukkit.getOnlinePlayers()) {
                if (!serverManagement.getServer(player1).equalsIgnoreCase(serverManagement.getServer(player2)))
                    player1.hidePlayer(player2);
                else {
                    if (!plugin.vanished_players.contains(player2.getUniqueId()) && !plugin.vanish_logged.contains(player2.getUniqueId())
                            && serverManagement.getServer(player1).equalsIgnoreCase(serverManagement.getServer(player2)))
                        player1.showPlayer(player2);
                }
            }
        }
    }
}
