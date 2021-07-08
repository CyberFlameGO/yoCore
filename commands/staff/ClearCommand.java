package me.yochran.yocore.commands.staff;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public ClearCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Clear.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.clear")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Clear.NoPermission")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Clear.IncorrectUsage")));
            return true;
        }

        if (args.length == 0) {
            ((Player) sender).getInventory().clear();
            ((Player) sender).getInventory().setHelmet(XMaterial.AIR.parseItem());
            ((Player) sender).getInventory().setChestplate(XMaterial.AIR.parseItem());
            ((Player) sender).getInventory().setLeggings(XMaterial.AIR.parseItem());
            ((Player) sender).getInventory().setBoots(XMaterial.AIR.parseItem());
            ((Player) sender).updateInventory();

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Clear.TargetMessage")));
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Clear.InvalidPlayer")));
                return true;
            }

            target.getInventory().clear();
            target.getInventory().setHelmet(XMaterial.AIR.parseItem());
            target.getInventory().setChestplate(XMaterial.AIR.parseItem());
            target.getInventory().setLeggings(XMaterial.AIR.parseItem());
            target.getInventory().setBoots(XMaterial.AIR.parseItem());
            target.updateInventory();

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Clear.ExecutorMessage")
                    .replace("%target%", playerManagement.getPlayerColor(target))));
            target.sendMessage(Utils.translate(plugin.getConfig().getString("Clear.TargetMessage")));
        }

        return true;
    }
}
