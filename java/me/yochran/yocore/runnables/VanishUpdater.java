package me.yochran.yocore.runnables;

import me.yochran.yocore.server.Server;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VanishUpdater extends BukkitRunnable {

    private final yoCore plugin;

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
                        if (Server.getServer(staff) == Server.getServer(players))
                            players.showPlayer(staff);
                    }
                }
            }
        }
    }
}
