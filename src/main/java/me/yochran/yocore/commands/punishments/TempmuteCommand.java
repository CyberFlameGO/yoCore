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

public class TempmuteCommand implements CommandExecutor {

    private final yoCore plugin;

    public TempmuteCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.mute")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.NoPermission")));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Temporary.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.InvalidPlayer")));
            return true;
        }

        if (plugin.muted_players.containsKey(target.getUniqueId())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.TargetIsMuted")));
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
            executorName = yoPlayer.getYoPlayer((Player) sender).getDisplayName();
        }

        boolean silent = false;
        if (reason.contains("-s")) {
            reason = reason.replace("-s ", "");
            silent = true;
        }

        Punishment punishment = new Punishment(PunishmentType.MUTE, yoTarget, executor, durationMS, silent, reason);
        punishment.create();
        punishment.execute();

        if (silent) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Mute.Temporary.ExecutorMessage")
                    .replace("%target%", yoTarget.getDisplayName())
                    .replace("%reason%", reason)
                    .replace("%duration%", durationStr)));
        } else {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Temporary.ExecutorMessage")
                    .replace("%target%", yoTarget.getDisplayName())
                    .replace("%reason%", reason)
                    .replace("%duration%", durationStr)));
        }

        if (target.isOnline()) {
            Bukkit.getPlayer(target.getName()).sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Temporary.TargetMessage")
                    .replace("%reason%", reason)
                    .replace("%duration%", durationStr)));
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (silent) {
                if (players.hasPermission("yocore.silent")) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Mute.Temporary.BroadcastMessage")
                            .replace("%executor%", executorName)
                            .replace("%target%", yoTarget.getDisplayName())));
                }
            } else {
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Temporary.BroadcastMessage")
                        .replace("%executor%", executorName)
                        .replace("%target%", yoTarget.getDisplayName())));
            }
        }

        return true;
    }
}
