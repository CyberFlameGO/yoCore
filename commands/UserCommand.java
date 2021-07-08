package me.yochran.yocore.commands;

import me.yochran.yocore.management.PermissionManagement;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachment;

import java.util.List;

public class UserCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PermissionManagement permissionManagement = new PermissionManagement();

    public UserCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.user")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("UserCommand.NoPermission")));
            return true;
        }

        if (args.length < 2 || args.length > 3) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("UserCommand.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("UserCommand.InvalidPlayer")));
            return true;
        }

        if (!args[1].equalsIgnoreCase("add")
                && !args[1].equalsIgnoreCase("remove")
                && !args[1].equalsIgnoreCase("list")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("UserCommand.IncorrectUsage")));
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "add":
                if (args.length != 3) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("UserCommand.IncorrectUsage")));
                    return true;
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("UserCommand.PermissionAdded")
                        .replace("%target%", playerManagement.getPlayerColor(target))
                        .replace("%permission%", args[2])));

                permissionManagement.addPlayerPermission(target, args[2]);
                if (target.isOnline()) permissionManagement.refreshPlayer(Bukkit.getPlayer(target.getUniqueId()));

                break;
            case "remove":
                if (args.length != 3) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("UserCommand.IncorrectUsage")));
                    return true;
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("UserCommand.PermissionRemoved")
                        .replace("%target%", playerManagement.getPlayerColor(target))
                        .replace("%permission%", args[2])));

                permissionManagement.removePlayerPermission(target, args[2]);
                if (target.isOnline()) permissionManagement.refreshPlayer(Bukkit.getPlayer(target.getUniqueId()));

                break;
            case "list":
                if (args.length != 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("UserCommand.IncorrectUsage")));
                    return true;
                }

                List<String> player_permissions = plugin.permissionsData.config.getStringList("Players." + target.getUniqueId().toString() + ".Permissions");

                String permissions = "";
                for (String permission : player_permissions) {
                    if (permissions.equalsIgnoreCase("")) permissions = "&7 - " + permission;
                    else permissions = permissions + "\n&7 - " + permission;
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("UserCommand.PlayerPermissions")
                        .replace("%target%", playerManagement.getPlayerColor(target))
                        .replace("%permissions%", permissions)));

                break;
        }

        return true;
    }
}
