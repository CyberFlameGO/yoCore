package me.yochran.yocore.commands.staff;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleStaffAlertsCommand implements CommandExecutor {

    private final yoCore plugin;

    public ToggleStaffAlertsCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.staffalerts")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.NoPermission")));
            return true;
        }

        if (!plugin.staff_alerts.contains(((Player) sender).getUniqueId())) {
            plugin.staff_alerts.add(((Player) sender).getUniqueId());
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.ToggleOnMessage")));
        } else {
            plugin.staff_alerts.remove(((Player) sender).getUniqueId());
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.ToggleOffMessage")));
        }

        return true;
    }
}
