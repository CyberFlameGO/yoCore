package me.yochran.yocore.listeners;

import me.yochran.yocore.scoreboard.ScoreboardSetter;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldChangeListener implements Listener {

    private final yoCore plugin;
    private final ScoreboardSetter scoreboardSetter = new ScoreboardSetter();

    public WorldChangeListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        plugin.tsb.add(event.getPlayer().getUniqueId());

        new BukkitRunnable() {
            public void run() {
                event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }.runTaskLater(plugin, 5);

        plugin.tsb.remove(event.getPlayer().getUniqueId());
    }
}