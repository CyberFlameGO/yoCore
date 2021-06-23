package me.yochran.yocore.runnables;

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
        for (Player players : Bukkit.getOnlinePlayers()) {
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (plugin.vanished_players.contains(staff.getUniqueId())) {
                    if (!players.hasPermission("yocore.vanish"))
                        players.hidePlayer(staff);
                }
            }
        }
    }
}
