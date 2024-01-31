package me.yochran.yocore.commands.punishments;

import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ClearHistoryCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

    public ClearHistoryCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.clearhistory")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearHistory.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearHistory.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearHistory.InvalidPlayer")));
            return true;
        }

        punishmentManagement.clearHistory(target);
        sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearHistory.ExecutorMessage")
                .replace("%target%", yoTarget.getDisplayName())));

        return true;
    }
}
