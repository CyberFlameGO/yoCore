package me.yochran.yocore.commands.punishments;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.punishments.Punishment;
import me.yochran.yocore.punishments.PunishmentType;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarnCommand implements CommandExecutor {

    private final yoCore plugin;

    public WarnCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.warn")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Warn.NoPermission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Warn.IncorrectUsage")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (target == null) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Warn.InvalidPlayer")));
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

        Punishment punishment = new Punishment(PunishmentType.WARN, yoTarget, executor, "Permanent", silent, reason);
        punishment.create();

        if (silent) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Warn.ExecutorMessage")
                    .replace("%target%", yoTarget.getDisplayName())
                    .replace("%reason%", reason)));
        } else {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Warn.ExecutorMessage")
                    .replace("%target%", yoTarget.getDisplayName())
                            .replace("%reason%", reason)));
        }

        target.sendMessage(Utils.translate(plugin.getConfig().getString("Warn.TargetMessage")
                .replace("%reason%", reason)));

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (silent) {
                if (players.hasPermission("yocore.silent")) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Warn.BroadcastMessage")
                        .replace("%executor%", executorName)
                        .replace("%target%", yoTarget.getDisplayName())));
                }
            } else {
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Warn.BroadcastMessage")
                    .replace("%executor%", executorName)
                    .replace("%target%", yoTarget.getDisplayName())));
            }
        }

        return true;
    }
}
