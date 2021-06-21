package me.yochran.yocore.commands;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagCommand implements CommandExecutor {

    private final yoCore plugin;
    public PlayerManagement playerManagement = new PlayerManagement();

    public TagCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.MustBePlayer")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.IncorrectUsage")));
            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {
            plugin.tag.remove(((Player) sender).getUniqueId());
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.FormatOff")));
        } else openTagsGUI((Player) sender);

        return true;
    }

    public void openTagsGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, Utils.translate("&aChat tags."));

        for (String tag : plugin.getConfig().getConfigurationSection("Tags").getKeys(false)) {
            ItemStack item = Utils.getMaterialFromConfig(plugin.getConfig().getString("TagCommand.TagItem"));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(Utils.translate(plugin.getConfig().getString("Tags." + tag + ".Display")));

            List<String> lore = new ArrayList<>();
            lore.add(Utils.translate("&7&m--------------------"));
            lore.add(Utils.translate("&eTag: &f" + plugin.getConfig().getString("Tags." + tag + ".ID")));
            lore.add(Utils.translate("&ePrefix: &f" + plugin.getConfig().getString("Tags." + tag + ".Prefix")));
            lore.add(Utils.translate("&eDisplay: &f" + plugin.getConfig().getString("Tags." + tag + ".Display")));
            lore.add(Utils.translate("&7&m--------------------"));
            if (player.hasPermission(plugin.getConfig().getString("Tags." + tag + ".Permission")))
                lore.add(Utils.translate("&aClick to select this tag."));
            else lore.add(Utils.translate("&cYou cannot use this tag."));

            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);

            inventory.addItem(item);
        }

        player.openInventory(inventory);
    }
}
