package me.yochran.yocore.commands.staff;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommands implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public TeleportCommands() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.teleport")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.NoPermission")));
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "teleport":
                if (args.length < 1 || args.length > 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.IncorrectUsage")));
                    return true;
                }

                if (args.length == 1) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.InvalidPlayer")));
                        return true;
                    }

                    ((Player) sender).teleport(target.getLocation());

                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.Teleport")
                            .replace("%target%", playerManagement.getPlayerColor(target))));

                    for (Player staff : Bukkit.getOnlinePlayers()) {
                        if (staff.hasPermission("yocore.chats.staff") && plugin.staff_alerts.contains(staff.getUniqueId()))
                            staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.Teleport")
                                    .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                                    .replace("%target%", playerManagement.getPlayerColor(target))));
                    }

                } else {
                    Player target1 = Bukkit.getPlayer(args[0]);
                    Player target2 = Bukkit.getPlayer(args[1]);
                    if (target1 == null || target2 == null) {
                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.InvalidPlayer")));
                        return true;
                    }

                    target1.teleport(target2.getLocation());

                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportTwo")
                            .replace("%target1%", playerManagement.getPlayerColor(target1))
                            .replace("%target2%", playerManagement.getPlayerColor(target2))));

                    for (Player staff : Bukkit.getOnlinePlayers()) {
                        if (staff.hasPermission("yocore.chats.staff") && plugin.staff_alerts.contains(staff.getUniqueId()))
                            staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.TeleportTwo")
                                    .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                                    .replace("%target1%", playerManagement.getPlayerColor(target1))
                                    .replace("%target2%", playerManagement.getPlayerColor(target2))));
                    }

                }

                break;
            case "teleporthere":
                if (args.length != 1) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.IncorrectUsageHere")));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.InvalidPlayer")));
                    return true;
                }

                target.teleport(((Player) sender).getLocation());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportHere")
                        .replace("%target%", playerManagement.getPlayerColor(target))));

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.chats.staff") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.TeleportHere")
                                .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                                .replace("%target%", playerManagement.getPlayerColor(target))));
                }

                break;
            case "teleportall":
                if (!sender.hasPermission("yocore.teleport.all")) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.NoPermission")));
                    return true;
                }

                for (Player players : Bukkit.getOnlinePlayers())
                    players.teleport(((Player) sender).getLocation());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportAll")));

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.chats.staff") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.TeleportAll")
                                .replace("%player%", playerManagement.getPlayerColor((Player) sender))));
                }

                break;
        }

        return true;
    }
}
