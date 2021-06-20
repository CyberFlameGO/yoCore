package me.yochran.yocore.commands.staff;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public VanishCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Vanish.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.vanish")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Vanish.NoPermission")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Vanish.IncorrectUsage")));
            return true;
        }

        if (args.length == 0) {
            if (!plugin.vanished_players.contains(((Player) sender).getUniqueId())) {
                plugin.vanished_players.add(((Player) sender).getUniqueId());
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Vanish.TargetMessageOn")));
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (!players.hasPermission("yocore.vanish"))
                        players.hidePlayer((Player) sender);
                }

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.VanishOnSelf")
                                .replace("%player%", playerManagement.getPlayerColor((Player) sender))));
                }
            } else {
                plugin.vanished_players.remove(((Player) sender).getUniqueId());
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Vanish.TargetMessageOff")));
                for (Player players : Bukkit.getOnlinePlayers())
                    ((Player) sender).showPlayer(players);

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.VanishOffSelf")
                                .replace("%player%", playerManagement.getPlayerColor((Player) sender))));
                }
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Vanish.InvalidPlayer")));
                return true;
            }

            if (!plugin.vanished_players.contains(target.getUniqueId())) {
                plugin.vanished_players.add(target.getUniqueId());
                target.sendMessage(Utils.translate(plugin.getConfig().getString("Vanish.TargetMessageOn")));
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Vanish.ExecutorMessageOn")
                        .replace("%target%", playerManagement.getPlayerColor(target))));
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (!players.hasPermission("yocore.vanish"))
                        players.hidePlayer(target);
                }

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.VanishOnOther")
                                .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                                .replace("%target%", playerManagement.getPlayerColor(target))));
                }
            } else {
                plugin.vanished_players.remove(target.getUniqueId());
                target.sendMessage(Utils.translate(plugin.getConfig().getString("Vanish.TargetMessageOff")));
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Vanish.ExecutorMessageOff")
                        .replace("%target%", playerManagement.getPlayerColor(target))));
                for (Player players : Bukkit.getOnlinePlayers())
                    target.showPlayer(players);

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.VanishOffOther")
                                .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                                .replace("%target%", playerManagement.getPlayerColor(target))));
                }
            }
        }

        return true;
    }
}
