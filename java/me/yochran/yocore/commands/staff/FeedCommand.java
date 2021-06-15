package me.yochran.yocore.commands.staff;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FeedCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public FeedCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Feed.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.feed")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Feed.NoPermission")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Feed.IncorrectUsage")));
            return true;
        }

        if (args.length == 0) {
            ((Player) sender).setFoodLevel(20);

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Feed.TargetMessage")));

            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.FeedSelf")
                            .replace("%player%", playerManagement.getPlayerColor((Player) sender))));
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Feed.InvalidPlayer")));
                return true;
            }

            target.setFoodLevel(20);

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Feed.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))));
            target.sendMessage(Utils.translate(plugin.getConfig().getString("Feed.TargetMessage")));

            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.FeedOther")
                            .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                            .replace("%target%", playerManagement.getPlayerColor(target))));
            }
        }

        return true;
    }
}
