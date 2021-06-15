package me.yochran.yocore.commands;

import me.yochran.yocore.management.GrantManagement;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ClearGrantHistoryCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final GrantManagement grantManagement = new GrantManagement();

    public ClearGrantHistoryCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.cleargranthistory")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearGrantHistory.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearGrantHistory.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearGrantHistory.InvalidPlayer")));
            return true;
        }

        grantManagement.clearHistory(target);
        sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearGrantHistory.ExecutorMessage")
                .replace("%target%", playerManagement.getPlayerColor(target))));

        return true;
    }
}
