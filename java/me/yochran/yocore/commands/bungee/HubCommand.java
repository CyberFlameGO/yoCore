package me.yochran.yocore.commands.bungee;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.ServerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HubCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final ServerManagement serverManagement = new ServerManagement();

    public HubCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Servers.Hub.Command.MustBePlayer")));
            return true;
        }

        if (!plugin.getConfig().getBoolean("Servers.Hub.Command.Enabled")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Servers.Hub.Command.CommandNotEnabled")));
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

        playerManagement.sendToSpawn(plugin.getConfig().getString("Servers.Hub.Server"), (Player) sender);

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Servers.Hub.Command.Format")));

        return true;
    }
}
