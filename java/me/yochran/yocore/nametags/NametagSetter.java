package me.yochran.yocore.nametags;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@SuppressWarnings("deprecation")
public class NametagSetter {

    private final yoCore plugin;

    public NametagSetter() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setNametag(Player player1, Player player2) {
        Scoreboard scoreboard = player1.getScoreboard();

        if (plugin.frozen_players.contains(player2.getUniqueId())) {
            frozenNametag(scoreboard, player2);
        } else {
            if (!plugin.vanished_players.contains(player2.getUniqueId()) && !plugin.modmode_players.contains(player2.getUniqueId()))
                rankNametag(scoreboard, player2);

            if (plugin.vanished_players.contains(player2.getUniqueId()) && plugin.getConfig().getBoolean("Nametags.Vanish.Enabled"))
                vanishNametag(scoreboard, player2);

            if (plugin.modmode_players.contains(player2.getUniqueId()) && !plugin.vanished_players.contains(player2.getUniqueId()) && plugin.getConfig().getBoolean("Nametags.ModMode.Enabled"))
                modmodeNametag(scoreboard, player2);
        }
    }

    public void rankNametag(Scoreboard scoreboard, Player player) {
        yoPlayer yoPlayer = new yoPlayer(player);

        Rank rank = yoPlayer.getRank();
        if (yoPlayer.isRankDisguised())
            rank = yoPlayer.getRankDisguise();

        Team team = scoreboard.getTeam(rank.getTabIndex());
        if (team != null) {
            team.setPrefix(Utils.translate(rank.getColor()));
            try { team.setColor(ChatColor.getByChar(rank.getColor().replace("&", "")));
            } catch (NoSuchMethodError ignored) {}
        }

        if (scoreboard.getTeam(rank.getTabIndex()) != null) {
            if (!scoreboard.getTeam(rank.getTabIndex()).hasPlayer(player)) {
                for (Team teams : scoreboard.getTeams()) {
                    if (teams.hasPlayer(player))
                        teams.removePlayer(player);
                }

                scoreboard.getTeam(rank.getTabIndex()).addPlayer(player);
            }
        } else scoreboard.registerNewTeam(rank.getTabIndex());
    }

    public void vanishNametag(Scoreboard scoreboard, Player player) {
        if (scoreboard.getTeam("zz") != null) {
            if (!scoreboard.getTeam("zz").hasPlayer(player)) {
                for (Team teams : scoreboard.getTeams()) {
                    if (teams.hasPlayer(player))
                        teams.removePlayer(player);
                }

                scoreboard.getTeam("zz").addPlayer(player);
            }
        } else {
            Team team = scoreboard.registerNewTeam("zz");
            team.setPrefix(Utils.translate("&7[V] &7"));
            try { team.setColor(ChatColor.valueOf(plugin.getConfig().getString("Nametags.Vanish.Color")));
            } catch (NoSuchMethodError ignored) {}
            scoreboard.getTeam("zz").addPlayer(player);
        }
    }

    public void modmodeNametag(Scoreboard scoreboard, Player player) {
        if (scoreboard.getTeam("zzz") != null) {
            if (!scoreboard.getTeam("zzz").hasPlayer(player)) {
                for (Team teams : scoreboard.getTeams()) {
                    if (teams.hasPlayer(player))
                        teams.removePlayer(player);
                }

                scoreboard.getTeam("zzz").addPlayer(player);
            }
        } else {
            Team team = scoreboard.registerNewTeam("zzz");
            team.setPrefix(Utils.translate("&7[M] &7"));
            try { team.setColor(ChatColor.valueOf(plugin.getConfig().getString("Nametags.ModMode.Color")));
            } catch (NoSuchMethodError ignored) {}
            scoreboard.getTeam("zzz").addPlayer(player);
        }
    }

    public void frozenNametag(Scoreboard scoreboard, Player player) {
        if (scoreboard.getTeam("zzzz") != null) {
            if (!scoreboard.getTeam("zzzz").hasPlayer(player)) {
                for (Team teams : scoreboard.getTeams()) {
                    if (teams.hasPlayer(player))
                        teams.removePlayer(player);
                }

                scoreboard.getTeam("zzzz").addPlayer(player);
            }
        } else {
            Team team = scoreboard.registerNewTeam("zzzz");
            team.setPrefix(Utils.translate("&c[F] &c"));
            try { team.setColor(ChatColor.valueOf(plugin.getConfig().getString("Nametags.Frozen.Color")));
            } catch (NoSuchMethodError ignored) {}
            scoreboard.getTeam("zzzz").addPlayer(player);
        }
    }
}
