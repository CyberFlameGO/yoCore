package me.yochran.yocore.runnables;

import me.yochran.yocore.scoreboard.ScoreboardSetter;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdater extends BukkitRunnable {

    private final yoCore plugin;
    private final ScoreboardSetter scoreboardSetter = new ScoreboardSetter();

    public ScoreboardUpdater() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void run() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (!plugin.tsb.contains(players.getUniqueId()))
                scoreboardSetter.scoreboard(players);
        }
    }
}