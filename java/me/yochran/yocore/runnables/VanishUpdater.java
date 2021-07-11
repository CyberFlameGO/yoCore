package me.yochran.yocore.runnables;

import me.yochran.yocore.management.ServerManagement;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VanishUpdater extends BukkitRunnable {

    private final yoCore plugin;
    private final ServerManagement serverManagement = new ServerManagement();

    public VanishUpdater() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void run() {
        for (Player staff : Bukkit.getOnlinePlayers()) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (plugin.vanished_players.contains(staff.getUniqueId())) {
                    if (!players.hasPermission("yocore.vanish"))
                        players.hidePlayer(staff);
                    else {
                        if (serverManagement.getServer(staff).equalsIgnoreCase(serverManagement.getServer(players)))
                            players.showPlayer(staff);
                    }
                }
            }
        }
    }
}
