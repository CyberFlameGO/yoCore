package me.yochran.yocore.commands;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor {

    private final yoCore plugin;

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
            yoPlayer player = new yoPlayer((Player) sender);

            if (args[0].equalsIgnoreCase("off")) {
                if (!player.isNicked()) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.NotNicked")));
                    return true;
                }

                player.removeNick();

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.FormatOffSelf")));
            } else {
                for (String players : plugin.playerData.config.getKeys(false)) {
                    if (plugin.playerData.config.getString(players + ".Name").equalsIgnoreCase(args[0].replace("&", ""))) {
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

                player.removeNick();
                player.setNickname(args[0].replace("&", ""));
            }
        } else {
            yoPlayer target = new yoPlayer(args[1]);

            if (!plugin.playerData.config.contains(target.getPlayer().getUniqueId().toString())) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.InvalidPlayer")));
                return true;
            }

            if (args[0].equalsIgnoreCase("off")) {
                if (!target.isNicked()) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.NotNicked")));
                    return true;
                }

                target.removeNick();

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Nickname.FormatOffOther")
                        .replace("%target%", target.getDisplayName())));
            } else {
                for (String players : plugin.playerData.config.getKeys(false)) {
                    if (plugin.playerData.config.getString(players + ".Name").equalsIgnoreCase(args[0].replace("&", ""))) {
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
                        .replace("%target%", target.getDisplayName())));

                target.removeNick();
                target.setNickname(args[0].replace("&", ""));
            }
        }

        return true;
    }
}
