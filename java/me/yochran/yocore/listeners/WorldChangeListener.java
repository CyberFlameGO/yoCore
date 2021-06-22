package me.yochran.yocore.listeners;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.scoreboard.ScoreboardSetter;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldChangeListener implements Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public WorldChangeListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (event.getPlayer().hasPermission("yocore.chats.staff")) {
            if (plugin.getConfig().getBoolean("WorldChangeAlerts.Enabled")) {
                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.chats.staff"))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("WorldChangeAlerts.Format")
                                .replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))
                                .replace("%old_world%", event.getFrom().getName())
                                .replace("%new_world%", event.getPlayer().getWorld().getName())));
                }
            }
        }

        plugin.tsb.add(event.getPlayer().getUniqueId());

        new BukkitRunnable() {
            public void run() {
                event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }.runTaskLater(plugin, 5);

        plugin.tsb.remove(event.getPlayer().getUniqueId());
    }
}