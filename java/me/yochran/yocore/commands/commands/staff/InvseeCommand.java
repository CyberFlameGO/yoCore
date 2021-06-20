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
import org.bukkit.inventory.Inventory;

public class InvseeCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public InvseeCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Invsee.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.invsee")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Invsee.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Invsee.IncorrectUsage")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Invsee.InvalidPlayer")));
            return true;
        }

        Inventory inventory = Bukkit.createInventory((Player) sender, 54, Utils.translate("&aInventory Inspect"));
        inventory.setContents(target.getInventory().getContents());

        for (int i = 36; i < 45; i++) {
            inventory.setItem(i, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem());
        }

        inventory.setItem(45, target.getInventory().getHelmet());
        inventory.setItem(46, target.getInventory().getChestplate());
        inventory.setItem(47, target.getInventory().getLeggings());
        inventory.setItem(48, target.getInventory().getBoots());
        inventory.setItem(53, target.getInventory().getItemInHand());

        ((Player) sender).openInventory(inventory);

        return true;
    }
}
