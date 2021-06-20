package me.yochran.yocore.commands;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public class RankDisguiseCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public RankDisguiseCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.rankdisguise")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.NoPermission")));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.InvalidPlayer")));
            return true;
        }

        if (!args[1].equalsIgnoreCase("off")) {
            if (!plugin.ranks.contains(args[1].toUpperCase())) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.InvalidRank")));
                return true;
            }

            plugin.rank_disguise.put(target.getUniqueId(), args[1].toUpperCase());

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.FormatOn")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))));
        } else {
            if (!plugin.rank_disguise.containsKey(target.getUniqueId())) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.TargetNotDisguised")));
                return true;
            }

            plugin.rank_disguise.remove(target.getUniqueId());

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.FormatOff")
                    .replace("%target%", playerManagement.getPlayerColor(target))));
        }

        return true;
    }
}
