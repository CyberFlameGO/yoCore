package me.yochran.yocore.commands;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.List;

public class ListCommand implements CommandExecutor, Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public ListCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!plugin.vanished_players.contains(player.getUniqueId())) {
                players.add(playerManagement.getPlayerColor(player));
            }
        }

        String rankMessage = "";
        for (String rank : plugin.getConfig().getConfigurationSection("Ranks").getKeys(false)) {
            if (rankMessage.equalsIgnoreCase("")) rankMessage = plugin.getConfig().getString("Ranks." + rank + ".Display");
            else rankMessage = rankMessage + "&7, " + plugin.getConfig().getString("Ranks." + rank + ".Display");
        }

        String playerMessage = "";
        for (String player : players) {
            if (playerMessage.equalsIgnoreCase("")) playerMessage = player;
            else playerMessage = playerMessage + "&7, " + player;
        }

        int online = Bukkit.getOnlinePlayers().size() - plugin.vanished_players.size();
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
