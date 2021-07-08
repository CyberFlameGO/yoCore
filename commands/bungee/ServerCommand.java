package me.yochran.yocore.commands.bungee;

import me.yochran.yocore.management.PlayerManagement;
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

        List<String> servers = new ArrayList<>();
        for (String server : plugin.worldData.config.getConfigurationSection("Servers").getKeys(false)) {
            if (plugin.worldData.config.getBoolean("Servers." + server + ".Enabled"))
                servers.add(plugin.worldData.config.getString("Servers." + server + ".World").toUpperCase());
        }

        if (!servers.contains(args[0].toUpperCase())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerCommand.InvalidServer")));
            return true;
        }

        playerManagement.sendToSpawn(Bukkit.getWorld(args[0]).getName(), (Player) sender);

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerCommand.Format")
                .replace("%server%", Bukkit.getWorld(args[0]).getName())));

        return true;
    }
}
