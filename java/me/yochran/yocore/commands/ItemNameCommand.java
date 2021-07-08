package me.yochran.yocore.commands;

import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemNameCommand implements CommandExecutor {

    private final yoCore plugin;

    public ItemNameCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ItemName.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.itemname")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ItemName.NoPermission")));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ItemName.IncorrectUsage")));
            return true;
        }

        if (((Player) sender).getInventory().getItemInHand().getType() == XMaterial.AIR.parseMaterial()) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ItemName.InvalidItem")));
            return true;
        }

        String name = "";
        for (int i = 0; i < args.length; i++) {
            if (name.equalsIgnoreCase("")) name = args[i];
            else name = name + " " + args[i];
        }

        List<String> lore = new ArrayList<>();
        if (((Player) sender).getInventory().getItemInHand().getItemMeta().hasLore())
            lore = ((Player) sender).getInventory().getItemInHand().getItemMeta().getLore();

        ItemBuilder itemBuilder = new ItemBuilder(
                ((Player) sender).getInventory().getItemInHand(),
                ((Player) sender).getInventory().getItemInHand().getAmount(),
                name,
                ItemBuilder.translateLore(lore)
        );

        itemBuilder.give((Player) sender, ((Player) sender).getInventory().getHeldItemSlot());
        ((Player) sender).updateInventory();

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("ItemName.Format")
            .replace("%name%", name)));

        return true;
    }
}
