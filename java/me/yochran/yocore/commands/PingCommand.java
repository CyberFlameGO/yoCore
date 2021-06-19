package me.yochran.yocore.commands;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class PingCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public PingCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ping.MustBePlayer")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ping.IncorrectUsage")));
            return true;
        }

        Player target;
        String ping = "&cNot Available";

        if (args.length == 0) { target = (Player) sender; } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ping.InvalidPlayer")));
                return true;
            }
        }

        try {
            Object entityPlayer = target.getClass().getMethod("getHandle").invoke(target);
            ping = String.valueOf((int) entityPlayer.getClass().getField("ping").get(entityPlayer));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ping.Format")
                .replace("%player%", playerManagement.getPlayerColor(target))
                .replace("%ping%", ping)));

        return true;
    }
}
