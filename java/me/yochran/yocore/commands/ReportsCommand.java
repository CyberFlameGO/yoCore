package me.yochran.yocore.commands;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportsCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public ReportsCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Reports.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.reporthistory")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Reports.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Reports.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Reports.InvalidPlayer")));
            return true;
        }

        openReportsGUI((Player) sender, target);

        return true;
    }

    public void openReportsGUI(Player player, OfflinePlayer target) {
        Inventory inventory = Bukkit.createInventory(player, 54, Utils.translate(playerManagement.getPlayerColor(target) + "&a's report history."));

        if (plugin.playerData.config.contains(target.getUniqueId().toString() + ".Report")) {
            for (String report : plugin.playerData.config.getConfigurationSection(target.getUniqueId().toString() + ".Report").getKeys(false)) {
                ItemStack item = XMaterial.LIME_WOOL.parseItem();
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(Utils.translate("&a&l(Active) " + Utils.getExpirationDate(plugin.playerData.config.getLong(target.getUniqueId().toString() + ".Report." + report  + ".Date"))));

                List<String> itemLore = new ArrayList<>();
                itemLore.add(Utils.translate("&7&m----------------------------"));
                itemLore.add(Utils.translate("&eExecutor: &f" + playerManagement.getPlayerColor(Bukkit.getOfflinePlayer(UUID.fromString(plugin.playerData.config.getString(target.getUniqueId().toString() + ".Report." + report + ".Executor"))))));
                itemLore.add(Utils.translate("&eReason: &f" + plugin.playerData.config.getString(target.getUniqueId().toString() + ".Report." + report + ".Reason")));
                itemLore.add(Utils.translate("&7&m----------------------------"));

                itemMeta.setLore(itemLore);
                item.setItemMeta(itemMeta);

                inventory.addItem(item);
            }
        }

        player.openInventory(inventory);
    }
}
