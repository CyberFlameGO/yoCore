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
import java.util.List;

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

        playerManagement.sendToSpawn(args[0].toUpperCase(), (Player) sender);

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerCommand.Format")
                .replace("%server%", serverManagement.getName(args[0].toUpperCase()))));

        return true;
    }
}
