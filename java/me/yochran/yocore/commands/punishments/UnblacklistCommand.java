package me.yochran.yocore.commands.punishments;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.punishments.Punishment;
import me.yochran.yocore.punishments.PunishmentType;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class UnblacklistCommand implements CommandExecutor {

    private final yoCore plugin;

    public UnblacklistCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.unblacklist")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unblacklist.NoPermission")));
            return true;
        }

        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unblacklist.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unblacklist.InvalidPlayer")));
            return true;
        }

        if (!plugin.blacklisted_players.containsKey(target.getUniqueId())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unblacklist.TargetIsNotBlacklisted")));
            return true;
        }

        String executorName;
        if (!(sender instanceof Player)) {
            executorName = "&c&lConsole";
        } else {
            executorName = yoPlayer.getYoPlayer((Player) sender).getDisplayName();
        }

        boolean silent = false;
        if (args.length == 2 && args[1].equalsIgnoreCase("-s")) {
            silent = true;
        }

        for (String players : plugin.playerData.config.getKeys(false)) {
            if (plugin.blacklisted_players.containsKey(UUID.fromString(players))) {
                yoPlayer yoPlayer = new yoPlayer(UUID.fromString(players));

                if (yoPlayer.getIP().equalsIgnoreCase(yoTarget.getIP())
                        || (yoTarget.getAllIPs().contains(yoPlayer.getIP())
                        || yoPlayer.getAllIPs().contains(yoTarget.getIP()))) {
                    for (Map.Entry<Integer, Punishment> entry : Punishment.getPunishments(yoPlayer).entrySet()) {
                        if (entry.getValue().getType() == PunishmentType.BLACKLIST && entry.getValue().getStatus().equalsIgnoreCase("Active"))
                            entry.getValue().revoke();
                    }
                }
            }
        }

        if (silent) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Unblacklist.ExecutorMessage")
                    .replace("%target%", yoTarget.getDisplayName())));
        } else {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unblacklist.ExecutorMessage")
                    .replace("%target%", yoTarget.getDisplayName())
                    .replace("%reason%", "N/A")));
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (silent) {
                if (players.hasPermission("yocore.silent")) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Unblacklist.BroadcastMessage")
                            .replace("%executor%", executorName)
                            .replace("%target%", yoTarget.getDisplayName())));
                }
            } else {
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Unblacklist.BroadcastMessage")
                        .replace("%executor%", executorName)
                        .replace("%target%", yoTarget.getDisplayName())));
            }
        }

        return true;
    }
}
