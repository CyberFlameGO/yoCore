package me.yochran.yocore.runnables;

import me.yochran.yocore.scoreboard.ScoreboardSetter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdater extends BukkitRunnable {

    ScoreboardSetter scoreboardSetter = new ScoreboardSetter();

    @Override
    public void run() {
        for (Player players : Bukkit.getOnlinePlayers())
            scoreboardSetter.scoreboard(players);
    }
}
