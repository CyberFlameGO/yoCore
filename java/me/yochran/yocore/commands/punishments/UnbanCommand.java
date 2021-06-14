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

public class UnbanCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

    public UnbanCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.unban")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unban.NoPermission")));
            return true;
        }

        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unban.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unban.InvalidPlayer")));
            return true;
        }

        if (!plugin.banned_players.containsKey(target.getUniqueId())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unban.TargetIsNotBanned")));
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

        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Ban." + punishmentManagement.getInfractionAmount(target, "Ban") + ".Status", "Revoked");
        plugin.punishmentData.config.set("BannedPlayers." + target.getUniqueId().toString(), null);
        plugin.punishmentData.saveData();
        plugin.banned_players.remove(target.getUniqueId());

        if (silent) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Unban.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))));
        } else {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unban.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%reason%", "N/A")));
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (silent) {
                if (players.hasPermission("yocore.silent")) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Unban.BroadcastMessage")
                            .replace("%executor%", executorName)
                            .replace("%target%", playerManagement.getPlayerColor(target))));
                }
            } else {
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Unban.BroadcastMessage")
                        .replace("%executor%", executorName)
                        .replace("%target%", playerManagement.getPlayerColor(target))));
            }
        }

        return true;
    }
}
