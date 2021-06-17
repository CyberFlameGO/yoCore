package me.yochran.yocore.scoreboard;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScoreboardSetter implements Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    public List<UUID> enabled = new ArrayList<>();

    public ScoreboardSetter() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        scoreboard(event.getPlayer());
        enabled.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) { enabled.remove(event.getPlayer().getUniqueId()); }

    public static String getEntryFromScore(Objective objective, int score) {
        if (objective == null) return null;
        if (!hasScoreTaken(objective, score)) return null;

        for (String entry : objective.getScoreboard().getEntries()) {
            if (objective.getScore(entry).getScore() == score)
                return objective.getScore(entry).getEntry();
        }

        return null;
    }

    public static boolean hasScoreTaken(Objective objective, int score) {
        for (String entry : objective.getScoreboard().getEntries()) {
            if (objective.getScore(entry).getScore() == score)
                return true;
        }

        return false;
    }

    public static void replaceScore(Objective objective, int score, String name) {
        if (hasScoreTaken(objective, score)) {
            if (getEntryFromScore(objective, score).equalsIgnoreCase(name))
                return;

            if (!getEntryFromScore(objective, score).equalsIgnoreCase(name))
                objective.getScoreboard().resetScores(getEntryFromScore(objective, score));
        }

        objective.getScore(name).setScore(score);
    }

    public void scoreboard(Player player) {
        if (!plugin.getConfig().getBoolean("Scoreboard.Enabled"))
            return;

        if (enabled.contains(player.getUniqueId()))
            return;

        if (player.getScoreboard().equals(Bukkit.getServer().getScoreboardManager().getMainScoreboard()))
            player.setScoreboard(Bukkit.getServer().getScoreboardManager().getNewScoreboard());

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = (scoreboard.getObjective(player.getName()) == null) ? scoreboard.registerNewObjective(player.getName(), "dummy") : scoreboard.getObjective(player.getName());

        String rank = plugin.playerData.config.getString(player.getUniqueId().toString() + ".Rank");
        String rankDisplay = plugin.getConfig().getString("Ranks." + rank.toUpperCase() + ".Display");


        if (plugin.getConfig().getBoolean("Scoreboard.Global.Enabled")) {
            objective.setDisplayName(Utils.translate(plugin.getConfig().getString("Scoreboard.Global.Title")));

            int row = plugin.getConfig().getStringList("Scoreboard.Global.Board").size();
            for (String score : plugin.getConfig().getStringList("Scoreboard.Global.Board")) {
                row--;
                String rowFormat = score
                        .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size() - plugin.vanished_players.size()))
                        .replace("%rank%", rankDisplay);
                replaceScore(objective, row, Utils.translate(rowFormat));
            }
        } else {
            String world = player.getWorld().getName();

            objective.setDisplayName(Utils.translate(plugin.getConfig().getString("Scoreboard.Worlds." + world + ".Title")));

            int row = plugin.getConfig().getStringList("Scoreboard.Worlds." + world + ".Board").size();
            for (String score : plugin.getConfig().getStringList("Scoreboard.Worlds." + world + ".Board")) {
                row--;
                String rowFormat = score
                        .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size() - plugin.vanished_players.size()))
                        .replace("%rank%", rankDisplay);
                replaceScore(objective, row, Utils.translate(rowFormat));
            }
        }

        if (objective.getDisplaySlot() != DisplaySlot.SIDEBAR)
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(scoreboard);
    }
}
