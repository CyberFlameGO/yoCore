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

public class SetrankCommand implements CommandExecutor {

    private final yoCore plugin;

    public SetrankCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SetRank.ConsoleOnly")));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SetRank.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SetRank.InvalidPlayer")));
            return true;
        }

        if (!Rank.getRanks().containsKey(args[1].toUpperCase())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SetRank.InvalidRank")));
            return true;
        }

        Rank rank = Rank.getRank(args[1]);

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("SetRank.ExecutorMessage")
                .replace("%rank%", rank.getDisplay())
                .replace("%target%", yoTarget.getDisplayName())));

        if (target.isOnline())
            Bukkit.getPlayer(target.getUniqueId()).sendMessage(Utils.translate(plugin.getConfig().getString("SetRank.TargetMessage")
                    .replace("%rank%", rank.getDisplay())));

        yoTarget.setRank(rank);

        return true;
    }
}
