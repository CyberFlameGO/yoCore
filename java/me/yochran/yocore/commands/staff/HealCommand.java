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

public class HealCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public HealCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Heal.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.heal")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Heal.NoPermission")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Heal.IncorrectUsage")));
            return true;
        }

        if (args.length == 0) {
            ((Player) sender).setHealth(20);
            ((Player) sender).setFireTicks(0);
            ((Player) sender).setFoodLevel(20);
            for (PotionEffect effect : ((Player) sender).getActivePotionEffects()) {
                PotionEffectType effectType = effect.getType();
                ((Player) sender).removePotionEffect(effectType);
            }

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Heal.TargetMessage")));

            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.HealSelf")
                            .replace("%player%", playerManagement.getPlayerColor((Player) sender))));
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Heal.InvalidPlayer")));
                return true;
            }

            target.setHealth(20);
            target.setFireTicks(0);
            target.setFoodLevel(20);
            for (PotionEffect effect : target.getActivePotionEffects()) {
                PotionEffectType effectType = effect.getType();
                target.removePotionEffect(effectType);
            }

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Heal.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))));
            target.sendMessage(Utils.translate(plugin.getConfig().getString("Heal.TargetMessage")));

            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.staffalerts") && plugin.staff_alerts.contains(staff.getUniqueId()))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.HealOther")
                            .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                            .replace("%target%", playerManagement.getPlayerColor(target))));
            }
        }

        return true;
    }
}
