package me.yochran.yocore.commands;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearReportsCommand implements CommandExecutor {

    private final yoCore plugin;

    public ClearReportsCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearReports.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.clearreports")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearReports.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearReports.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearReports.InvalidPlayer")));
            return true;
        }

        plugin.playerData.config.set(target.getUniqueId().toString() + ".ReportsAmount", 0);
        plugin.playerData.config.set(target.getUniqueId().toString() + ".Report", null);
        plugin.playerData.saveData();

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearReports.Format")
                .replace("%target%", yoTarget.getDisplayName())));

        return true;
    }
}
