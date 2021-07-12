package me.yochran.yocore.commands;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.ServerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ManagementChatCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final ServerManagement serverManagement = new ServerManagement();

    public ManagementChatCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ManagementChat.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.chats.management")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ManagementChat.NoPermission")));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ManagementChat.IncorrectUsage")));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
            if (plugin.mchat_toggle.contains(((Player) sender).getUniqueId())) {
                plugin.mchat_toggle.remove(((Player) sender).getUniqueId());
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("ManagementChat.ToggleOff")));
            } else {
                plugin.mchat_toggle.add(((Player) sender).getUniqueId());
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("ManagementChat.ToggleOn")));
            }

            return true;
        }

        String message = "";
        for (int i = 0; i < args.length; i++) {
            message = message + args[i] + " ";
        }

        for (Player managers : Bukkit.getOnlinePlayers()) {
            if (managers.hasPermission("yocore.chats.management")) {
                managers.sendMessage(Utils.translate(plugin.getConfig().getString("ManagementChat.Format")
                        .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                        .replace("%message%", message)
                        .replace("%server%", serverManagement.getName(serverManagement.getServer((Player) sender)))
                        .replace("%world%", ((Player) sender).getWorld().getName())));
            }
        }

        return true;
    }
}
