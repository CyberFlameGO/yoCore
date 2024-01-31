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

public class UnmuteCommand implements CommandExecutor {

    private final yoCore plugin;

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
        yoPlayer yoTarget = new yoPlayer(target);

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
            executorName = yoPlayer.getYoPlayer((Player) sender).getDisplayName();
        }

        boolean silent = false;
        if (args.length == 2 && args[1].equalsIgnoreCase("-s")) {
            silent = true;
        }

        for (Map.Entry<Integer, Punishment> punishment : Punishment.getPunishments(yoTarget).entrySet()) {
            if (punishment.getValue().getType() == PunishmentType.MUTE && punishment.getValue().getStatus().equalsIgnoreCase("Active"))
                punishment.getValue().revoke();
        }

        if (silent) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Unmute.ExecutorMessage")
                    .replace("%target%", yoTarget.getDisplayName())));
        } else {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unmute.ExecutorMessage")
                    .replace("%target%", yoTarget.getDisplayName())
                    .replace("%reason%", "N/A")));
        }

        if (target.isOnline()) { Bukkit.getPlayer(target.getName()).sendMessage(Utils.translate(plugin.getConfig().getString("Unmute.TargetMessage"))); }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (silent) {
                if (players.hasPermission("yocore.silent")) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Unmute.BroadcastMessage")
                            .replace("%executor%", executorName)
                            .replace("%target%", yoTarget.getDisplayName())));
                }
            } else {
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Unmute.BroadcastMessage")
                        .replace("%executor%", executorName)
                        .replace("%target%", yoTarget.getDisplayName())));
            }
        }

        return true;
    }
}
