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

import java.util.UUID;

public class BlacklistCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

    public BlacklistCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.blacklist")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Blacklist.NoPermission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Blacklist.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Blacklist.InvalidPlayer")));
            return true;
        }

        String targetIP = plugin.playerData.config.getString(target.getUniqueId().toString() + ".IP");

        if (plugin.blacklisted_players.containsKey(target.getUniqueId())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Blacklist.TargetIsBlacklisted")));
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
            executorName = playerManagement.getPlayerColor((Player) sender);
        }

        boolean silent = false;
        if (reason.contains("-s")) {
            reason = reason.replace("-s ", "");
            silent = true;
        }

        punishmentManagement.addInfraction("Blacklist", target, executor, reason, System.currentTimeMillis(), "Permanent", silent);
        punishmentManagement.addBlacklist(target, reason);

        if (silent) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Blacklist.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%reason%", reason)));
        } else {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Blacklist.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%reason%", reason)));
        }

        if (target.isOnline()) {
            Bukkit.getPlayer(target.getName()).kickPlayer(Utils.translate(plugin.getConfig().getString("Blacklist.TargetMessage")
                    .replace("%reason%", reason)));
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players.getAddress().getAddress().getHostAddress().equals(targetIP)
                    || (plugin.playerData.config.getStringList(target.getUniqueId().toString() + ".TotalIPs").contains(plugin.playerData.config.getString(players.getUniqueId().toString() + ".IP"))
                    || plugin.playerData.config.getStringList(players.getUniqueId().toString() + ".TotalIPs").contains(plugin.playerData.config.getString(target.getUniqueId().toString() + ".IP")))) {
                players.kickPlayer(Utils.translate(plugin.getConfig().getString("Blacklist.TargetMessage")
                        .replace("%reason%", reason)));
            }
        }

        for (String players : plugin.playerData.config.getKeys(false)) {
            if (plugin.playerData.config.getString(players + ".IP").equalsIgnoreCase(targetIP)
                    || (plugin.playerData.config.getStringList(target.getUniqueId().toString() + ".TotalIPs").contains(plugin.playerData.config.getString(players + ".IP"))
                    || plugin.playerData.config.getStringList(players + ".TotalIPs").contains(plugin.playerData.config.getString(target.getUniqueId().toString() + ".IP")))) {
                if (!plugin.playerData.config.getString(players + ".Name").equalsIgnoreCase(target.getName())) {
                    punishmentManagement.addInfraction("Blacklist", Bukkit.getOfflinePlayer(UUID.fromString(players)), executor, reason + "(Linked to " + target.getName() + ")", System.currentTimeMillis(), "Permanent", silent);
                    punishmentManagement.addBlacklist(Bukkit.getOfflinePlayer(UUID.fromString(players)), reason + "(Linked to " + target.getName() + ")");
                }
            }
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (silent) {
                if (players.hasPermission("yocore.silent")) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Blacklist.BroadcastMessage")
                            .replace("%executor%", executorName)
                            .replace("%target%", playerManagement.getPlayerColor(target))));
                }
            } else {
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Blacklist.BroadcastMessage")
                        .replace("%executor%", executorName)
                        .replace("%target%", playerManagement.getPlayerColor(target))));
            }
        }

        return true;
    }
}
