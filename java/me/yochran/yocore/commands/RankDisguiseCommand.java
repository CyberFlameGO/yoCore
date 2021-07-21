package me.yochran.yocore.commands;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankDisguiseCommand implements CommandExecutor {

    private final yoCore plugin;

    public RankDisguiseCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.rankdisguise")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.NoPermission")));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.InvalidPlayer")));
            return true;
        }

        if (!args[1].equalsIgnoreCase("off")) {
            if (!Rank.getRanks().containsKey(args[1].toUpperCase())) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.InvalidRank")));
                return true;
            }

            Rank disguise = Rank.getRank(args[1].toUpperCase());
            yoTarget.setRankDisguise(disguise);

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.FormatOn")
                    .replace("%target%", yoTarget.getDisplayName())
                    .replace("%rank%", disguise.getDisplay())));
        } else {
            if (!plugin.rank_disguise.containsKey(target.getUniqueId())) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.TargetNotDisguised")));
                return true;
            }

            plugin.rank_disguise.remove(target.getUniqueId());

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankDisguise.FormatOff")
                    .replace("%target%", yoTarget.getDisplayName())));
        }

        return true;
    }
}
