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

public class UnmuteCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

    public UnmuteCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.mute")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unmute.NoPermission")));
            return true;
        }

        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unmute.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unmute.InvalidPlayer")));
            return true;
        }

        if (!plugin.muted_players.containsKey(target.getUniqueId())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unmute.TargetIsNotMuted")));
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

        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Mute." + punishmentManagement.getInfractionAmount(target, "Mute") + ".Status", "Revoked");
        plugin.punishmentData.config.set("MutedPlayers." + target.getUniqueId().toString(), null);
        plugin.punishmentData.saveData();
        plugin.muted_players.remove(target.getUniqueId());

        if (silent) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Unmute.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))));
        } else {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unmute.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%reason%", "N/A")));
        }

        if (target.isOnline()) { Bukkit.getPlayer(target.getName()).sendMessage(Utils.translate(plugin.getConfig().getString("Unmute.TargetMessage"))); }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (silent) {
                if (players.hasPermission("yocore.silent")) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Unmute.BroadcastMessage")
                            .replace("%executor%", executorName)
                            .replace("%target%", playerManagement.getPlayerColor(target))));
                }
            } else {
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Unmute.BroadcastMessage")
                        .replace("%executor%", executorName)
                        .replace("%target%", playerManagement.getPlayerColor(target))));
            }
        }

        return true;
    }
}