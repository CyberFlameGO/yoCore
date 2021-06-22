package me.yochran.yocore.commands.staff;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public SudoCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Sudo.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.speed")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Sudo.NoPermission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Sudo.IncorrectUsage")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Sudo.InvalidPlayer")));
            return true;
        }

        String toRun = "";
        for (int i = 1; i < args.length; i++) {
            if (toRun.equalsIgnoreCase("")) toRun = args[i];
            else toRun = toRun + " " + args[i];
        }

        if (args[1].startsWith("/")) {
            target.performCommand(toRun.replaceFirst("/", ""));

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Sudo.ExecutorMessageCommand")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%command%", toRun)));

            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.SudoCommand")
                            .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                            .replace("%target%", playerManagement.getPlayerColor(target))
                            .replace("%command%", toRun)));
            }
        } else {
            target.chat(toRun);

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Sudo.ExecutorMessageMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%message%", toRun)));

            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.SudoMessage")
                            .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                            .replace("%target%", playerManagement.getPlayerColor(target))
                            .replace("%message%", toRun)));
            }
        }

        return true;
    }
}
