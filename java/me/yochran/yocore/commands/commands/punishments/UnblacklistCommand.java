package me.yochran.yocore.commands.punishments;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnblacklistCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

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
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unblacklist.InvalidPlayer")));
            return true;
        }

        String targetIP = plugin.playerData.config.getString(target.getUniqueId().toString() + ".IP");

        if (!plugin.punishmentData.config.contains("BlacklistedPlayers." + target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unblacklist.TargetIsNotBlacklisted")));
            return true;
        }

        String executorName;
        if (!(sender instanceof Player)) {
            executorName = "&c&lConsole";
        } else {
            executorName = playerManagement.getPlayerColor((Player) sender);
        }

        boolean silent = false;
        if (args.length == 2 && args[1].equalsIgnoreCase("-s")) {
            silent = true;
        }

        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Blacklist." + punishmentManagement.getInfractionAmount(target, "Blacklist") + ".Status", "Revoked");
        plugin.punishmentData.config.set("BlacklistedPlayers." + target.getUniqueId().toString(), null);
        plugin.punishmentData.saveData();
        plugin.blacklisted_ips.remove(targetIP);

        if (silent) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Unblacklist.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))));
        } else {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unblacklist.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%reason%", "N/A")));
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (silent) {
                if (players.hasPermission("yocore.silent")) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Unblacklist.BroadcastMessage")
                            .replace("%executor%", executorName)
                            .replace("%target%", playerManagement.getPlayerColor(target))));
                }
            } else {
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Unblacklist.BroadcastMessage")
                        .replace("%executor%", executorName)
                        .replace("%target%", playerManagement.getPlayerColor(target))));
            }
        }

        return true;
    }
}
