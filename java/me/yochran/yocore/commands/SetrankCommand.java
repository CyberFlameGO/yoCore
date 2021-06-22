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
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Map;

public class SetrankCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PermissionManagement permissionManagement = new PermissionManagement();

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
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SetRank.InvalidPlayer")));
            return true;
        }

        if (!plugin.ranks.contains(args[1].toUpperCase())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SetRank.InvalidRank")));
            return true;
        }

        plugin.playerData.config.set(target.getUniqueId().toString() + ".Rank", args[1].toUpperCase());
        plugin.playerData.saveData();

        if (target.isOnline()) permissionManagement.refreshPlayer(Bukkit.getPlayer(target.getUniqueId()));

        if (target.isOnline())
            Bukkit.getPlayer(target.getUniqueId()).sendMessage(Utils.translate(plugin.getConfig().getString("SetRank.TargetMessage")
                    .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))));

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("SetRank.ExecutorMessage")
                .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))
                .replace("%target%", playerManagement.getPlayerColor(target))));

        return true;
    }
}
