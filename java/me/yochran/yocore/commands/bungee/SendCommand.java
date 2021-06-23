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

public class SendCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public SendCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.send")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Send.NoPermission")));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Send.IncorrectUsage")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Send.InvalidPlayer")));
            return true;
        }

        List<String> servers = new ArrayList<>();
        for (String server : plugin.worldData.config.getConfigurationSection("Servers").getKeys(false)) {
            if (plugin.worldData.config.getBoolean("Servers." + server + ".Enabled"))
                servers.add(plugin.worldData.config.getString("Servers." + server + ".World").toUpperCase());
        }

        if (!servers.contains(args[1].toUpperCase())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Send.InvalidServer")));
            return true;
        }

        Location location = new Location(Bukkit.getWorld(args[1]), 0.5, 75, 0.5);
        target.teleport(location);

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Send.ExecutorMessage")
                .replace("%target%", playerManagement.getPlayerColor(target))
                .replace("%server%", Bukkit.getWorld(args[1]).getName())));

        target.sendMessage(Utils.translate(plugin.getConfig().getString("Send.TargetMessage")
                .replace("%server%", Bukkit.getWorld(args[1]).getName())));

        return true;
    }
}
