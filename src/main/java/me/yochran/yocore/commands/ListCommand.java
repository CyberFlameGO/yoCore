package me.yochran.yocore.commands;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.server.Server;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListCommand implements CommandExecutor, Listener {

    private final yoCore plugin;

    public ListCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Server server = Server.getServer((Player) sender);

        List<String> players = new ArrayList<>();

        for (Map.Entry<String, Rank> rank : Rank.getRanks().entrySet()) {
            for (Player player : Server.getPlayers(server)) {
                yoPlayer yoPlayer = new yoPlayer(player);

                if (!plugin.vanished_players.contains(player.getUniqueId())
                        && yoPlayer.getRank() == rank.getValue()) {
                    players.add(yoPlayer.getDisplayName());
                }
            }
        }

        String rankMessage = "";
        for (Map.Entry<String, Rank> rank : Rank.getRanks().entrySet()) {
            if (rankMessage.equalsIgnoreCase("")) rankMessage = rank.getValue().getDisplay();
            else rankMessage = rankMessage + "&7, " + rank.getValue().getDisplay();
        }

        String playerMessage = "";
        for (String player : players) {
            if (playerMessage.equalsIgnoreCase("")) playerMessage = player;
            else playerMessage = playerMessage + "&7, " + player;
        }

        List<UUID> vanished = new ArrayList<>();
        for (Player player : Server.getPlayers(server)) {
            if (plugin.vanished_players.contains(player.getUniqueId()))
                vanished.add(player.getUniqueId());
        }

        int online = Server.getPlayers(server).size() - vanished.size();
        int max = plugin.getServer().getMaxPlayers();

        sender.sendMessage(Utils.translate(rankMessage + "\n&7(&f" + online + "/" + max + "&7) " + playerMessage));

        return true;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/list") || event.getMessage().startsWith("/minecraft:list")) {
            event.setCancelled(true);
            event.getPlayer().performCommand("onlineplayers");
        }
    }
}
