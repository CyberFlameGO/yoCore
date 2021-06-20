package me.yochran.yocore.commands.staff;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EnderChestCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public EnderChestCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("EnderChest.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.echest")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("EnderChest.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("EnderChest.IncorrectUsage")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("EnderChest.InvalidPlayer")));
            return true;
        }

        Inventory inventory = Bukkit.createInventory((Player) sender, 27, Utils.translate("&aEnder Chest."));

        inventory.setContents(target.getEnderChest().getContents());

        ((Player) sender).openInventory(inventory);

        return true;
    }
}
