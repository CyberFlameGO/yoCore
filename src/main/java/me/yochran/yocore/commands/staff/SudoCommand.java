package me.yochran.yocore.commands.staff;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand implements CommandExecutor {

    private final yoCore plugin;

    public SudoCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Sudo.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.sudo")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Sudo.NoPermission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Sudo.IncorrectUsage")));
            return true;
        }

        if (!args[0].equalsIgnoreCase("*")) {
            Player target = Bukkit.getPlayer(args[0]);
            yoPlayer yoTarget = new yoPlayer(target);

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
                        .replace("%target%", yoTarget.getDisplayName())
                        .replace("%command%", toRun)));

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.SudoCommand")
                                .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())
                                .replace("%target%", yoTarget.getDisplayName())
                                .replace("%command%", toRun)));
                }
            } else {
                target.chat(toRun);

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Sudo.ExecutorMessageMessage")
                        .replace("%target%", yoTarget.getDisplayName())
                        .replace("%message%", toRun)));

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.SudoMessage")
                                .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())
                                .replace("%target%", yoTarget.getDisplayName())
                                .replace("%message%", toRun)));
                }
            }
        } else {
            for (Player target : Bukkit.getOnlinePlayers()) {
                yoPlayer yoTarget = new yoPlayer(target);

                String toRun = "";
                for (int i = 1; i < args.length; i++) {
                    if (toRun.equalsIgnoreCase("")) toRun = args[i];
                    else toRun = toRun + " " + args[i];
                }

                if (args[1].startsWith("/")) {
                    target.performCommand(toRun.replaceFirst("/", ""));

                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Sudo.ExecutorMessageCommand")
                            .replace("%target%", yoTarget.getDisplayName())
                            .replace("%command%", toRun)));

                    for (Player staff : Bukkit.getOnlinePlayers()) {
                        if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                            staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.SudoCommand")
                                    .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())
                                    .replace("%target%", yoTarget.getDisplayName())
                                    .replace("%command%", toRun)));
                    }
                } else {
                    target.chat(toRun);

                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Sudo.ExecutorMessageMessage")
                            .replace("%target%", yoTarget.getDisplayName())
                            .replace("%message%", toRun)));

                    for (Player staff : Bukkit.getOnlinePlayers()) {
                        if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                            staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.SudoMessage")
                                    .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())
                                    .replace("%target%", yoTarget.getDisplayName())
                                    .replace("%message%", toRun)));
                    }
                }
            }
        }

        return true;
    }
}
