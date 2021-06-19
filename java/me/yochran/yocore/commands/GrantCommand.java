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

public class GrantCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public GrantCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.grant")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.InvalidPlayer")));
            return true;
        }

        plugin.grant_player.remove(((Player) sender).getUniqueId());
        plugin.grant_rank.remove(((Player) sender).getUniqueId());
        plugin.grant_reason.remove(((Player) sender).getUniqueId());
        plugin.grant_duration.remove(((Player) sender).getUniqueId());

        openGrantGUI((Player) sender, target);
        plugin.grant_player.put(((Player) sender).getUniqueId(), target.getUniqueId());

        return true;
    }

    public void openGrantGUI(Player player, OfflinePlayer target) {
        Inventory inventory = Bukkit.createInventory(player, 54, Utils.translate("&aSelect a rank."));

        for (String rank : plugin.getConfig().getConfigurationSection("Ranks").getKeys(false)) {
            ItemStack item = Utils.getMaterialFromConfig(plugin.getConfig().getString("Ranks." + rank + ".GrantItem"));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(Utils.translate(plugin.getConfig().getString("Ranks." + rank + ".Display")));

            List<String> itemLore = new ArrayList<>();
            String permission;
            if (player.hasPermission(plugin.getConfig().getString("Ranks." + rank + ".GrantPermission"))) {
                permission = "&aYou can grant this rank.";
            } else {
                permission = "&cYou do not have permission to grant this rank.";
            }

            for (String line : plugin.getConfig().getStringList("Grant.Rank.Lore")) {
                itemLore.add(Utils.translate(line
                        .replace("%target%", playerManagement.getPlayerColor(target))
                        .replace("%rank%", plugin.getConfig().getString("Ranks." + rank + ".Display"))
                        .replace("%permission%", permission)));
            }

            itemMeta.setLore(itemLore);
            item.setItemMeta(itemMeta);
            inventory.setItem(plugin.getConfig().getInt("Ranks." + rank + ".Priority") - 1, item);
        }

        player.openInventory(inventory);
    }

    public void openDurationGUI(Player player, OfflinePlayer target) {
        Inventory inventory = Bukkit.createInventory(player, 36, Utils.translate("&aSelect a duration."));

        for (String cItem : plugin.getConfig().getConfigurationSection("Grant.Duration.Items").getKeys(false)) {
            ItemStack item = Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.Duration.Items." + cItem + ".Item"));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(Utils.translate(plugin.getConfig().getString("Grant.Duration.Items." + cItem + ".Name")));

            List<String> itemLore = new ArrayList<>();
            for (String line : plugin.getConfig().getStringList("Grant.Duration.Lore")) {
                itemLore.add(Utils.translate(line
                        .replace("%duration%", plugin.getConfig().getString("Grant.Duration.Items." + cItem + ".Name"))
                        .replace("%target%", playerManagement.getPlayerColor(target))));
            }

            itemMeta.setLore(itemLore);
            item.setItemMeta(itemMeta);
            inventory.setItem(plugin.getConfig().getInt("Grant.Duration.Items." + cItem + ".Slot"), item);
        }

        player.openInventory(inventory);
    }

    public void openReasonGUI(Player player, OfflinePlayer target) {
        Inventory inventory = Bukkit.createInventory(player,  27, Utils.translate("&aSelect a reason."));

        for (String cItem : plugin.getConfig().getConfigurationSection("Grant.Reason.Items").getKeys(false)) {
            ItemStack item = Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.Reason.Items." + cItem + ".Item"));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(Utils.translate(plugin.getConfig().getString("Grant.Reason.Items." + cItem + ".Name")));

            List<String> itemLore = new ArrayList<>();
            for (String line : plugin.getConfig().getStringList("Grant.Reason.Lore")) {
                itemLore.add(Utils.translate(line
                        .replace("%reason%", plugin.getConfig().getString("Grant.Reason.Items." + cItem + ".Name"))
                        .replace("%target%", playerManagement.getPlayerColor(target))));
            }

            itemMeta.setLore(itemLore);
            item.setItemMeta(itemMeta);
            inventory.setItem(plugin.getConfig().getInt("Grant.Reason.Items." + cItem + ".Slot"), item);
        }

        player.openInventory(inventory);
    }

    public void openConfirmGUI(Player player, OfflinePlayer target, String rank, String duration, String reason) {
        Inventory inventory = Bukkit.createInventory(player,  54, Utils.translate("&aConfirm the grant."));

        ItemStack yesItem = XMaterial.GREEN_TERRACOTTA.parseItem();
        ItemStack noItem = XMaterial.RED_TERRACOTTA.parseItem();

        ItemMeta yesItemMeta = yesItem.getItemMeta();
        ItemMeta noItemMeta = noItem.getItemMeta();

        yesItemMeta.setDisplayName(Utils.translate("&2&lConfirm Grant"));
        noItemMeta.setDisplayName(Utils.translate("&c&lCancel Grant"));

        List<String> lore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("Grant.Confirm.Lore")) {
            lore.add(Utils.translate(line
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%rank%", rank)
                    .replace("%duration%", duration)
                    .replace("%reason%", reason)));
        }

        yesItemMeta.setLore(lore);
        noItemMeta.setLore(lore);
        yesItem.setItemMeta(yesItemMeta);
        noItem.setItemMeta(noItemMeta);

        for (int i = 10; i < 13; i++) { inventory.setItem(i, yesItem); }
        for (int i = 19; i < 22; i++) { inventory.setItem(i, yesItem); }
        for (int i = 28; i < 31; i++) { inventory.setItem(i, yesItem); }
        for (int i = 14; i < 17; i++) { inventory.setItem(i, noItem); }
        for (int i = 23; i < 26; i++) { inventory.setItem(i, noItem); }
        for (int i = 32; i < 35; i++) { inventory.setItem(i, noItem); }

        player.openInventory(inventory);
    }
}