package me.yochran.yocore.commands.bungee;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.server.Server;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GListCommand implements CommandExecutor {

    private final yoCore plugin;

    public GListCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.glist")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("GList.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("GList.IncorrectUsage")));
            return true;
        }

        if (!args[0].equalsIgnoreCase("showall")) {
            Server server = Server.getServer(args[0]);

            if (server == null || !Server.getServers().containsKey(server.getName())) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("GList.InvalidServer")));
                return true;
            }

            List<String> players = new ArrayList<>();
            for (Map.Entry<String, Rank> rank : Rank.getRanks().entrySet()) {
                for (Player player : Server.getPlayers(server)) {
                    yoPlayer yoPlayer = new yoPlayer(player);

                    if (yoPlayer.getRank() == rank.getValue())
                        players.add(yoPlayer.getDisplayName());
                }
            }

            String player_message = "";
            for (String player : players) {
                if (player_message.equalsIgnoreCase("")) player_message = player;
                else player_message = player_message + "&7, " + player;
            }

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("GList.Format")
                    .replace("%server%", server.getName())
                    .replace("%server_online%", String.valueOf(Server.getPlayers(server).size()))
                    .replace("%online_players%", player_message)));

        } else {
            String server_message = "";

            for (Map.Entry<String, Server> entry : Server.getServers().entrySet()) {
                List<String> players = new ArrayList<>();

                for (Map.Entry<String, Rank> rank : Rank.getRanks().entrySet()) {
                    for (Player player : Server.getPlayers(entry.getValue())) {
                        yoPlayer yoPlayer = new yoPlayer(player);

                        if (yoPlayer.getRank() == rank.getValue())
                            players.add(yoPlayer.getDisplayName());
                    }
                }

                String player_message = "";
                for (String player : players) {
                    if (player_message.equalsIgnoreCase("")) player_message = player;
                    else player_message = player_message + "&7, " + player;
                }

                if (server_message.equalsIgnoreCase("")) server_message = plugin.getConfig().getString("GList.Format")
                        .replace("%server%", entry.getValue().getName())
                        .replace("%server_online%", String.valueOf(Server.getPlayers(entry.getValue()).size()))
                        .replace("%online_players%", player_message);
                else server_message = server_message + "\n" + plugin.getConfig().getString("GList.Format")
                        .replace("%server%", entry.getValue().getName())
                        .replace("%server_online%", String.valueOf(Server.getPlayers(entry.getValue()).size()))
                        .replace("%online_players%", player_message);
            }

            sender.sendMessage(Utils.translate(server_message));
        }

        return true;
    }
}
