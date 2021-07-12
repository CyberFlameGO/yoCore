package me.yochran.yocore.commands.bungee;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.ServerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final ServerManagement serverManagement = new ServerManagement();

    public ServerCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerCommand.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.server")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerCommand.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerCommand.IncorrectUsage")));
            return true;
        }

        if (!serverManagement.getServers().contains(args[0].toUpperCase())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerCommand.InvalidServer")));
            return true;
        }

        if (plugin.modmode_players.contains(((Player) sender).getUniqueId())) {
            ((Player) sender).getInventory().clear();

            ((Player) sender).getInventory().setContents(plugin.inventory_contents.get(((Player) sender).getUniqueId()));
            ((Player) sender).getInventory().setArmorContents(plugin.armor_contents.get(((Player) sender).getUniqueId()));

            ((Player) sender).updateInventory();

            ((Player) sender).setAllowFlight(false);
            ((Player) sender).setFlying(false);

            plugin.modmode_players.remove(((Player) sender).getUniqueId());
        }

        if (plugin.last_location.get(((Player) sender).getUniqueId()) == null) {
            Map<String, Location> location = new HashMap<>();
            location.put(serverManagement.getServer((Player) sender), ((Player) sender).getLocation());
            plugin.last_location.put(((Player) sender).getUniqueId(), location);
        }

        plugin.last_location.get(((Player) sender).getUniqueId()).put(serverManagement.getServer((Player) sender), ((Player) sender).getLocation());

        if (plugin.getConfig().getBoolean("Spawn.SpawnOnServerChange"))
            playerManagement.sendToSpawn(args[0].toUpperCase(), (Player) sender);
        else {
            if (plugin.last_location.get(((Player) sender).getUniqueId()).containsKey(args[0].toUpperCase()))
                ((Player) sender).teleport(plugin.last_location.get(((Player) sender).getUniqueId()).get(args[0].toUpperCase()));
            else playerManagement.sendToSpawn(args[0].toUpperCase(), (Player) sender);
        }

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerCommand.Format")
                .replace("%server%", serverManagement.getName(args[0].toUpperCase()))));

        return true;
    }
}
