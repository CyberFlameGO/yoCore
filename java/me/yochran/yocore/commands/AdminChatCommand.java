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

public class AdminChatCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final ServerManagement serverManagement = new ServerManagement();

    public AdminChatCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.chats.admin")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.NoPermission")));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.IncorrectUsage")));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
            if (plugin.achat_toggle.contains(((Player) sender).getUniqueId())) {
                plugin.achat_toggle.remove(((Player) sender).getUniqueId());
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.ToggleOff")));
            } else {
                plugin.achat_toggle.add(((Player) sender).getUniqueId());
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Adminhat.ToggleOn")));
            }

            return true;
        }

        String message = "";
        for (int i = 0; i < args.length; i++) {
            message = message + args[i] + " ";
        }

        for (Player admins : Bukkit.getOnlinePlayers()) {
            if (admins.hasPermission("yocore.chats.admin")) {
                admins.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.Format")
                        .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                        .replace("%message%", message)
                        .replace("%server%", serverManagement.getName(serverManagement.getServer((Player) sender)))
                        .replace("%world%", ((Player) sender).getWorld().getName())));
            }
        }

        return true;
    }
}
