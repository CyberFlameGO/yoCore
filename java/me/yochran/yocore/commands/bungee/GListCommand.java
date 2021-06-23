package me.yochran.yocore.commands.bungee;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GListCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

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

        List<String> servers = new ArrayList<>();
        for (String server : plugin.worldData.config.getConfigurationSection("Servers").getKeys(false)) {
            if (plugin.worldData.config.getBoolean("Servers." + server + ".Enabled"))
                servers.add(plugin.worldData.config.getString("Servers." + server + ".World").toUpperCase());
        }

        if (!args[0].equalsIgnoreCase("showall")) {
            if (!servers.contains(args[0].toUpperCase())) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("GList.InvalidServer")));
                return true;
            }

            List<String> players = new ArrayList<>();
            for (String rank : plugin.ranks) {
                for (Player player : Bukkit.getWorld(args[0]).getPlayers()) {
                    if (plugin.playerData.config.getString(player.getUniqueId().toString() + ".Rank").equalsIgnoreCase(rank))
                        players.add(playerManagement.getPlayerColor(player));
                }
            }

            String player_message = "";
            for (String player : players) {
                if (player_message.equalsIgnoreCase("")) player_message = player;
                else player_message = player_message + "&7, " + player;
            }

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("GList.Format")
                    .replace("%server%", Bukkit.getWorld(args[0]).getName())
                    .replace("%server_online%", String.valueOf(Bukkit.getWorld(args[0]).getPlayers().size()))
                    .replace("%online_players%", player_message)));

        } else {
            String server_message = "";

            for (String server : servers) {
                List<String> players = new ArrayList<>();
                for (String rank : plugin.ranks) {
                    for (Player player : Bukkit.getWorld(server).getPlayers()) {
                        if (plugin.playerData.config.getString(player.getUniqueId().toString() + ".Rank").equalsIgnoreCase(rank))
                            players.add(playerManagement.getPlayerColor(player));
                    }
                }

                String player_message = "";
                for (String player : players) {
                    if (player_message.equalsIgnoreCase("")) player_message = player;
                    else player_message = player_message + "&7, " + player;
                }

                if (server_message.equalsIgnoreCase("")) server_message = plugin.getConfig().getString("GList.Format")
                        .replace("%server%", Bukkit.getWorld(server).getName())
                        .replace("%server_online%", String.valueOf(Bukkit.getWorld(server).getPlayers().size()))
                        .replace("%online_players%", player_message);
                else server_message = server_message + "\n" + plugin.getConfig().getString("GList.Format")
                        .replace("%server%", Bukkit.getWorld(server).getName())
                        .replace("%server_online%", String.valueOf(Bukkit.getWorld(server).getPlayers().size()))
                        .replace("%online_players%", player_message);
            }

            sender.sendMessage(Utils.translate(server_message));
        }

        return true;
    }
}
