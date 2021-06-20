package me.yochran.yocore.commands;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NickCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public NickCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.nick")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.NoPermission")));
            return true;
        }

        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.IncorrectUsage")));
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("off")) {
                if (!plugin.nickname.containsKey(((Player) sender).getUniqueId())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.NotNicked")));
                    return true;
                }

                plugin.nickname.remove(((Player) sender).getUniqueId());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.FormatOffSelf")));
            } else {
                for (String player : plugin.playerData.config.getKeys(false)) {
                    if (plugin.playerData.config.getString(player + ".Name").equalsIgnoreCase(args[0].replace("&", ""))) {
                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.CannotNickAsPlayer")));
                        return true;
                    }
                }

                if (plugin.nickname.containsValue(args[0].replace("&", ""))) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.NickIsTaken")));
                    return true;
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.FormatOnSelf")
                        .replace("%nickname%", args[0].replace("&", ""))));

                plugin.nickname.remove(((Player) sender).getUniqueId());
                plugin.nickname.put(((Player) sender).getUniqueId(), args[0].replace("&", ""));
            }
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.InvalidPlayer")));
                return true;
            }

            if (args[0].equalsIgnoreCase("off")) {
                if (!plugin.nickname.containsKey(((Player) sender).getUniqueId())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.TargetNotNicked")));
                    return true;
                }

                plugin.nickname.remove(target.getUniqueId());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.FormatOffOther")
                        .replace("%target%", playerManagement.getPlayerColor(target))));
            } else {
                for (String player : plugin.playerData.config.getKeys(false)) {
                    if (plugin.playerData.config.getString(player + ".Name").equalsIgnoreCase(args[0].replace("&", ""))) {
                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.CannotNickAsPlayer")));
                        return true;
                    }
                }

                if (plugin.nickname.containsValue(args[0].replace("&", ""))) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.NickIsTaken")));
                    return true;
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.FormatOnOther")
                        .replace("%nickname%", args[0].replace("&", ""))
                        .replace("%target%", playerManagement.getPlayerColor(target))));

                plugin.nickname.remove(target.getUniqueId());
                plugin.nickname.put(target.getUniqueId(), args[0].replace("&", ""));
            }
        }

        return true;
    }
}
