package me.yochran.yocore.nametags;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class NametagSetter {

    private final yoCore plugin;

    public NametagSetter() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setNametag(Player player1, Player player2) {
        Scoreboard scoreboard = player1.getScoreboard();

        if (!plugin.vanished_players.contains(player2.getUniqueId()) && !plugin.modmode_players.contains(player2.getUniqueId()))
            rankNametag(scoreboard, player2);

        if (plugin.vanished_players.contains(player2.getUniqueId()) && plugin.getConfig().getBoolean("Nametags.Vanish.Enabled"))
            vanishNametag(scoreboard, player2);

        if (plugin.modmode_players.contains(player2.getUniqueId()) && !plugin.vanished_players.contains(player2.getUniqueId()) && plugin.getConfig().getBoolean("Nametags.ModMode.Enabled"))
            modmodeNametag(scoreboard, player2);
    }

    public void rankNametag(Scoreboard scoreboard, Player player) {
        String rank = plugin.playerData.config.getString(player.getUniqueId().toString() + ".Rank");

        Map<String, Integer> priority = new HashMap<>();
        for (String ranks : plugin.ranks)
            priority.put(ranks, plugin.getConfig().getInt("Ranks." + rank + ".Priority"));

        if (scoreboard.getTeam(String.valueOf(priority.get(rank))) != null) {
            if (!scoreboard.getTeam(String.valueOf(priority.get(rank))).hasPlayer(player)) {
                for (Team teams : scoreboard.getTeams()) {
                    if (teams.hasPlayer(player)) teams.removePlayer(player);
                }

                scoreboard.getTeam(String.valueOf(priority.get(rank))).addPlayer(player);
            }
        } else {
            Team team = scoreboard.registerNewTeam(String.valueOf(priority.get(rank)));
            team.setPrefix(Utils.translate(plugin.getConfig().getString("Ranks." + rank + ".Color")));
            try { team.setColor(ChatColor.getByChar(plugin.getConfig().getString("Ranks." + rank + ".Color").replace("&", "")));
            } catch (NoSuchMethodError ignored) {}
            scoreboard.getTeam(String.valueOf(priority.get(rank))).addPlayer(player);
        }
    }

    public void vanishNametag(Scoreboard scoreboard, Player player) {
        if (scoreboard.getTeam("a") != null) {
            if (!scoreboard.getTeam("a").hasPlayer(player)) {
                for (Team teams : scoreboard.getTeams()) {
                    if (teams.hasPlayer(player)) teams.removePlayer(player);
                }

                scoreboard.getTeam("a").addPlayer(player);
            }
        } else {
            Team team = scoreboard.registerNewTeam("a");
            team.setPrefix(Utils.translate("&7[V] &7"));
            try { team.setColor(ChatColor.valueOf(plugin.getConfig().getString("Nametags.Vanish.Color")));
            } catch (NoSuchMethodError ignored) {}
            scoreboard.getTeam("a").addPlayer(player);
        }
    }

    public void modmodeNametag(Scoreboard scoreboard, Player player) {
        if (scoreboard.getTeam("b") != null) {
            if (!scoreboard.getTeam("b").hasPlayer(player)) {
                for (Team teams : scoreboard.getTeams()) {
                    if (teams.hasPlayer(player)) teams.removePlayer(player);
                }

                scoreboard.getTeam("b").addPlayer(player);
            }
        } else {
            Team team = scoreboard.registerNewTeam("b");
            team.setPrefix(Utils.translate("&7[M] &7"));
            try { team.setColor(ChatColor.valueOf(plugin.getConfig().getString("Nametags.ModMode.Color")));
            } catch (NoSuchMethodError ignored) {}
            scoreboard.getTeam("b").addPlayer(player);
        }
    }
}
