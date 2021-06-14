package me.yochran.yocore.commands.punishments;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarnCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

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
            executorName = playerManagement.getPlayerColor((Player) sender);
        }

        boolean silent = false;
        if (reason.contains("-s")) {
            reason = reason.replace("-s", "");
            silent = true;
        }

        punishmentManagement.addInfraction("Warn", target, executor, reason, "Permanent", silent);

        if (silent) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Warn.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%reason%", reason)));
        } else {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Warn.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                            .replace("%reason%", reason)));
        }

        target.sendMessage(Utils.translate(plugin.getConfig().getString("Warn.TargetMessage")
                .replace("%reason%", reason)));

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (silent) {
                if (players.hasPermission("yocore.silent")) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("SilentPrefix") + plugin.getConfig().getString("Warn.BroadcastMessage")
                        .replace("%executor%", executorName)
                        .replace("%target%", playerManagement.getPlayerColor(target))));
                }
            } else {
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Warn.BroadcastMessage")
                    .replace("%executor%", executorName)
                    .replace("%target%", playerManagement.getPlayerColor(target))));
            }
        }

        return true;
    }
}
