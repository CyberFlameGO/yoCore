package me.yochran.yocore.commands.staff;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class FreezeCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public FreezeCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.freeze")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Freeze.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Freeze.IncorrectUsage")));
            return true;
        }

        if (plugin.frozen_cooldown.contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Freeze.OnCooldown")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Freeze.InvalidPlayer")));
            return true;
        }

        plugin.frozen_cooldown.add(((Player) sender).getUniqueId());
        if (!plugin.frozen_players.contains(target.getUniqueId())) {
            plugin.frozen_players.add(target.getUniqueId());

            List<Double> coordinates = new ArrayList<>();
            coordinates.add(target.getLocation().getX());
            coordinates.add(target.getLocation().getZ());

            plugin.frozen_coordinates.put(target.getUniqueId(), coordinates);

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Freeze.ExecutorMessageOn")
                    .replace("%target%", playerManagement.getPlayerColor(target))));
            target.sendMessage(Utils.translate(plugin.getConfig().getString("Freeze.TargetMessageOn")));

            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.FreezeOn")
                            .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                            .replace("%target%", playerManagement.getPlayerColor(target))));
            }
        } else {
            plugin.frozen_players.remove(target.getUniqueId());
            plugin.frozen_coordinates.remove(target.getUniqueId());

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Freeze.ExecutorMessageOff")
                    .replace("%target%", playerManagement.getPlayerColor(target))));
            target.sendMessage(Utils.translate(plugin.getConfig().getString("Freeze.TargetMessageOff")));

            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.FreezeOff")
                            .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                            .replace("%target%", playerManagement.getPlayerColor(target))));
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() { plugin.frozen_cooldown.remove(((Player) sender).getUniqueId()); }
        }.runTaskLater(plugin, 20);

        return true;
    }
}
