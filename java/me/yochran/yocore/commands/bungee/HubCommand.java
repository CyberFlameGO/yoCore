package me.yochran.yocore.commands.bungee;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubCommand implements CommandExecutor {

    private final yoCore plugin;

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

        Location location = new Location(Bukkit.getWorld(plugin.getConfig().getString("Servers.Hub.World")), 0.5, 75, 0.5, (float) 0.5, (float) 0.5);
        ((Player) sender).teleport(location);

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Servers.Hub.Command.Format")));

        return true;
    }
}
