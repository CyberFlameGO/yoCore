package me.yochran.yocore.commands.punishments;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.PunishmentManagement;
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

public class ReTempbanCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

    public ReTempbanCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.ban")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ban.NoPermission")));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ban.Temporary.ReBanIncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ban.InvalidPlayer")));
            return true;
        }

        if (!plugin.banned_players.containsKey(target.getUniqueId())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ban.TargetNotBanned")));
            return true;
        }

        long durationMS = Utils.getDurationMS(args[1]);
        String durationStr = Utils.getDurationString(args[1]);

        String reason = "";
        for (int i = 2; i < args.length; i++) {
            reason = reason + args[i] + " ";
        }

        String executor;
        String executorName;
        if (!(sender instanceof Player)) {
            executor = "CONSOLE";
            executorName = "&c&lConsole";
        } else {
            executor = ((Player) sender).getUniqueId().toString();
            executorName = playerManagement.getPlayerColor((Player) sender);
        }

        boolean silent = false;
        if (reason.contains("-s")) {
            reason = reason.replace("-s ", "");
            silent = true;
        }

        for (Map.Entry<Integer, Punishment> entry : Punishment.getPunishments(target).entrySet()) {
            if (entry.getValue().getType() == PunishmentType.BAN && entry.getValue().getStatus().equalsIgnoreCase("Active")) {
                Punishment.redo(entry.getValue(), target, executor, durationMS, silent, reason);
                entry.getValue().reexecute();
            }
        }

        if (silent) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Ban.Temporary.ReBanExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%reason%", reason)
                    .replace("%duration%", durationStr)));
        } else {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ban.Temporary.ReBanExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%reason%", reason)
                    .replace("%duration%", durationStr)));
        }

        if (target.isOnline()) {
            Bukkit.getPlayer(target.getName()).kickPlayer(Utils.translate(plugin.getConfig().getString("Ban.Temporary.TargetMessage")
                    .replace("%reason%", reason)
                    .replace("%expiration%", durationStr)));
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (silent) {
                if (players.hasPermission("yocore.silent")) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Ban.Temporary.BroadcastMessage")
                            .replace("%executor%", executorName)
                            .replace("%target%", playerManagement.getPlayerColor(target))));
                }
            } else {
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Ban.Temporary.BroadcastMessage")
                        .replace("%executor%", executorName)
                        .replace("%target%", playerManagement.getPlayerColor(target))));
            }
        }

        return true;
    }
}
