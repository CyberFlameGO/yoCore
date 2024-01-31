package me.yochran.yocore.commands.staff;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class TeleportCommands implements CommandExecutor {

    private final yoCore plugin;

    public TeleportCommands() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.MustBePlayer")));
            return true;
        }

        Player target = null;
        yoPlayer yoTarget = null;

        switch (command.getName().toLowerCase()) {
            case "teleport":
                if (!sender.hasPermission("yocore.teleport")) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.NoPermission")));
                    return true;
                }

                if (args.length < 1 || args.length > 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.IncorrectUsage")));
                    return true;
                }

                if (args.length == 1) {
                    target = Bukkit.getPlayer(args[0]);
                    yoTarget = new yoPlayer(target);

                    if (target == null) {
                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.InvalidPlayer")));
                        return true;
                    }

                    ((Player) sender).teleport(target.getLocation());

                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.Teleport")
                            .replace("%target%", yoTarget.getDisplayName())));

                    for (Player staff : Bukkit.getOnlinePlayers()) {
                        if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                            staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.Teleport")
                                    .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())
                                    .replace("%target%", yoTarget.getDisplayName())));
                    }

                } else {
                    Player target1 = Bukkit.getPlayer(args[0]);
                    Player target2 = Bukkit.getPlayer(args[1]);

                    yoTarget = new yoPlayer(target1);
                    yoPlayer yoTarget2 = new yoPlayer(target2);

                    if (target1 == null || target2 == null) {
                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.InvalidPlayer")));
                        return true;
                    }

                    target1.teleport(target2.getLocation());

                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportTwo")
                            .replace("%target1%", yoTarget.getDisplayName())
                            .replace("%target2%", yoTarget2.getDisplayName())));

                    for (Player staff : Bukkit.getOnlinePlayers()) {
                        if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                            staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.TeleportTwo")
                                    .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())
                                    .replace("%target1%", yoTarget.getDisplayName())
                                    .replace("%target2%", yoTarget2.getDisplayName())));
                    }

                }

                break;
            case "teleporthere":
                if (!sender.hasPermission("yocore.teleport")) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.NoPermission")));
                    return true;
                }

                if (args.length < 1 || args.length > 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.IncorrectUsage")));
                    return true;
                }

                target = Bukkit.getPlayer(args[0]);
                yoTarget = new yoPlayer(target);

                if (target == null) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.InvalidPlayer")));
                    return true;
                }

                target.teleport(((Player) sender).getLocation());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportHere")
                        .replace("%target%", yoTarget.getDisplayName())));

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.TeleportHere")
                                .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())
                                .replace("%target%", yoTarget.getDisplayName())));
                }

                break;
            case "teleportall":
                if (!sender.hasPermission("yocore.teleport.all") && !sender.hasPermission("yocore.teleport")) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.NoPermission")));
                    return true;
                }

                for (Player players : Bukkit.getOnlinePlayers())
                    players.teleport(((Player) sender).getLocation());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportAll")));

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.TeleportAll")
                                .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())));
                }

                break;
            case "teleporta":
                if (!sender.hasPermission("yocore.teleportrequest")) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.NoPermission")));
                    return true;
                }

                if (args.length < 1 || args.length > 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.IncorrectUsage")));
                    return true;
                }

                if (plugin.tpa.containsKey(((Player) sender).getUniqueId())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequestOutgoingRequest")));
                    return true;
                }

                target = Bukkit.getPlayer(args[0]);
                yoTarget = new yoPlayer(target);

                if (target == null) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.InvalidPlayer")));
                    return true;
                }

                plugin.tpa.remove(target.getUniqueId());
                plugin.tpa.remove(((Player) sender).getUniqueId());

                plugin.tpa_coords.remove(target.getUniqueId());
                plugin.tpa_coords.remove(((Player) sender).getUniqueId());

                plugin.tpa.put(((Player) sender).getUniqueId(), target.getUniqueId());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequest")
                        .replace("%target%", yoTarget.getDisplayName())));
                target.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequestTarget")
                        .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())));

                break;
            case "teleportaccept":
                if (!sender.hasPermission("yocore.teleportrequest")) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.NoPermission")));
                    return true;
                }

                if (!plugin.tpa.containsValue(((Player) sender).getUniqueId())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequestNoRequest")));
                    return true;
                }

                for (Map.Entry<UUID, UUID> map : plugin.tpa.entrySet()) {
                    if (map.getValue() == ((Player) sender).getUniqueId())
                        target = Bukkit.getPlayer(map.getKey());
                }

                if (target == null) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.InvalidPlayer")));
                    return true;
                }

                yoTarget = new yoPlayer(target);

                plugin.tpa_coords.put(target.getUniqueId(), target.getLocation());
                plugin.tpa_timer.put(target.getUniqueId(), 5);

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequestAccept")
                        .replace("%player%", yoTarget.getDisplayName())));
                target.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequestStarted")));

                break;
            case "teleportdeny":
                if (!sender.hasPermission("yocore.teleportrequest")) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.NoPermission")));
                    return true;
                }

                if (!plugin.tpa.containsValue(((Player) sender).getUniqueId())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequestNoRequest")));
                    return true;
                }

                for (Map.Entry<UUID, UUID> map : plugin.tpa.entrySet()) {
                    if (map.getValue() == ((Player) sender).getUniqueId()) {
                        target = Bukkit.getPlayer(map.getKey());
                        yoTarget = new yoPlayer(target);
                    }
                }

                if (target == null) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.InvalidPlayer")));
                    return true;
                }

                plugin.tpa.remove(target.getUniqueId());
                plugin.tpa_timer.remove(target.getUniqueId());
                plugin.tpa_coords.remove(target.getUniqueId());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequestDeny")
                        .replace("%player%", yoTarget.getDisplayName())));
                target.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequestDenyTarget")
                        .replace("%target%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())));

                break;
            case "teleportcancel":
                if (!sender.hasPermission("yocore.teleportrequest")) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.NoPermission")));
                    return true;
                }

                if (!plugin.tpa.containsKey(((Player) sender).getUniqueId())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequestNoRequest")));
                    return true;
                }

                plugin.tpa.remove(((Player) sender).getUniqueId());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequestCancel")));

                break;
        }

        return true;
    }
}
