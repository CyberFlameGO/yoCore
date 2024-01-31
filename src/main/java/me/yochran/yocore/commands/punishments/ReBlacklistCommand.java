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

public class ReBlacklistCommand implements CommandExecutor {

    private final yoCore plugin;

    public ReBlacklistCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.blacklist")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Blacklist.NoPermission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Blacklist.ReBlacklistIncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Blacklist.InvalidPlayer")));
            return true;
        }

        if (!plugin.blacklisted_players.containsKey(target.getUniqueId())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Blacklist.TargetNotBlacklisted")));
            return true;
        }

        String reason = "";
        for (int i = 1; i < args.length; i++) {
            reason = reason + args[i] + " ";
        }

        String executor;
        String executorName;
        if (!(sender instanceof Player)) {
            executor = "CONSOLE";
            executorName = "&c&lConsole";
        } else {
            executor = ((Player) sender).getUniqueId().toString();
            executorName = yoPlayer.getYoPlayer((Player) sender).getDisplayName();
        }

        boolean silent = false;
        if (reason.contains("-s")) {
            reason = reason.replace("-s ", "");
            silent = true;
        }

        for (Map.Entry<Integer, Punishment> entry : Punishment.getPunishments(yoTarget).entrySet()) {
            if (entry.getValue().getType() == PunishmentType.BLACKLIST && entry.getValue().getStatus().equalsIgnoreCase("Active")) {
                Punishment.redo(entry.getValue(), yoTarget, executor, "Permanent", silent, reason);
                entry.getValue().reexecute();
            }
        }

        if (silent) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Blacklist.ReBlacklistExecutorMessage")
                    .replace("%target%", yoTarget.getDisplayName())
                    .replace("%reason%", reason)));
        } else {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Blacklist.ReBlacklistExecutorMessage")
                    .replace("%target%", yoTarget.getDisplayName())
                    .replace("%reason%", reason)));
        }

        if (target.isOnline()) {
            Bukkit.getPlayer(target.getName()).kickPlayer(Utils.translate(plugin.getConfig().getString("Blacklist.TargetMessage")
                    .replace("%reason%", reason)));
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            yoPlayer yoPlayer = new yoPlayer(players);

            if (players.getAddress().getAddress().getHostAddress().equals(yoTarget.getIP())
                    || (yoTarget.getAllIPs().contains(yoPlayer.getIP())
                    || yoPlayer.getAllIPs().contains(yoTarget.getIP()))) {
                players.kickPlayer(Utils.translate(plugin.getConfig().getString("Blacklist.TargetMessage")
                        .replace("%reason%", reason)));
            }
        }

        for (String players : plugin.punishmentData.config.getConfigurationSection("BlacklistedPlayers").getKeys(false)) {
            if (!UUID.fromString(players).equals(target.getUniqueId())
                    && plugin.punishmentData.config.getString("BlacklistedPlayers." + players + ".Reason").contains("(Linked to " + target.getName() + ")")) {
                yoPlayer yoPlayer = new yoPlayer(UUID.fromString(players));

                for (Map.Entry<Integer, Punishment> entry : Punishment.getPunishments(yoPlayer).entrySet()) {
                    if (entry.getValue().getType() == PunishmentType.BLACKLIST && entry.getValue().getStatus().equalsIgnoreCase("Active")) {
                        Punishment.redo(entry.getValue(), yoPlayer, executor, "Permanent", silent, reason + " (Linked to " + target.getName() + ")");
                        entry.getValue().reexecute();
                    }
                }
            }
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (silent) {
                if (players.hasPermission("yocore.silent")) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Blacklist.BroadcastMessage")
                            .replace("%executor%", executorName)
                            .replace("%target%", yoTarget.getDisplayName())));
                }
            } else {
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Blacklist.BroadcastMessage")
                        .replace("%executor%", executorName)
                        .replace("%target%", yoTarget.getDisplayName())));
            }
        }

        return true;
    }
}
