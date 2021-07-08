package me.yochran.yocore.commands;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public StaffChatCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("StaffChat.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.chats.staff")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("StaffChat.NoPermission")));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("StaffChat.IncorrectUsage")));
            return true;
        }

        String message = "";
        for (int i = 0; i < args.length; i++) {
            message = message + args[i] + " ";
        }

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("yocore.chats.staff")) {
                staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffChat.Format")
                        .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                        .replace("%message%", message)
                        .replace("%server%", plugin.getConfig().getString("ServerName"))
                        .replace("%world%", ((Player) sender).getWorld().getName())));
            }
        }

        return true;
    }
}