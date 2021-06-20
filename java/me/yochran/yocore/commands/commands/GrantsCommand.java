package me.yochran.yocore.commands;

import me.yochran.yocore.management.GrantManagement;
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

public class GrantsCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final GrantManagement grantManagement = new GrantManagement();

    public GrantsCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("GrantHistory.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.granthistory")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("GrantHistory.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("GrantHistory.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("GrantHistory.InvalidPlayer")));
            return true;
        }

        plugin.selected_grant_history.remove(((Player) sender).getUniqueId());
        openHistoryGUI((Player) sender, target);
        plugin.selected_grant_history.put(((Player) sender).getUniqueId(), target.getUniqueId());

        return true;
    }

    public void openHistoryGUI(Player player, OfflinePlayer target) {
        Inventory inventory = Bukkit.createInventory(player, 54, Utils.translate(playerManagement.getPlayerColor(target) + "&a's grant history."));

        if (plugin.grantData.config.contains(target.getUniqueId().toString() + ".Grants")) {
            for (String grant : plugin.grantData.config.getConfigurationSection(target.getUniqueId().toString() + ".Grants").getKeys(false)) {
                ItemStack item = XMaterial.BEDROCK.parseItem();
                ItemMeta itemMeta = item.getItemMeta();

                String activePrefix = "&a&l(Active) ";
                String revokedPrefix = "&4&l(Revoked) ";
                String expiredPrefix = "&6&l(Expired) ";
                boolean revokable = false;

                switch (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Status").toLowerCase()) {
                    case "active":
                        revokable = true;
                        item = XMaterial.LIME_WOOL.parseItem();
                        itemMeta.setDisplayName(Utils.translate(activePrefix + Utils.getExpirationDate(plugin.grantData.config.getLong(target.getUniqueId().toString() + ".Grants." + grant + ".Date"))));
                        break;
                    case "revoked":
                        item = XMaterial.RED_WOOL.parseItem();
                        itemMeta.setDisplayName(Utils.translate(revokedPrefix + Utils.getExpirationDate(plugin.grantData.config.getLong(target.getUniqueId().toString() + ".Grants." + grant + ".Date"))));
                        break;
                    case "expired":
                        item = XMaterial.ORANGE_WOOL.parseItem();
                        itemMeta.setDisplayName(Utils.translate(expiredPrefix + Utils.getExpirationDate(plugin.grantData.config.getLong(target.getUniqueId().toString() + ".Grants." + grant + ".Date"))));
                        break;
                }

                String issuedGrant;
                if (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Type").equalsIgnoreCase("RANK")) {
                    if (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Grant").equalsIgnoreCase("(Removed Rank)"))
                        issuedGrant = "&4(Removed Rank)";
                    else issuedGrant = plugin.getConfig().getString("Ranks." + plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Grant").toUpperCase() + ".Display");
                } else {
                    issuedGrant  = plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Grant");
                }
                String executor = playerManagement.getPlayerColor(Bukkit.getOfflinePlayer(UUID.fromString(plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Executor"))));
                String duration;
                if (plugin.grantData.config.get(target.getUniqueId().toString() + ".Grants." + grant + ".Duration").equals("Permanent")) duration = "Permanent";
                else duration = Utils.getExpirationDate(plugin.grantData.config.getLong(target.getUniqueId().toString() + ".Grants." + grant + ".Duration"));
                String reason = plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Reason");
                String previousRank = plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".PreviousRank");
                String previousRankDisplay;
                if (!previousRank.equalsIgnoreCase("N/A")) previousRankDisplay = plugin.getConfig().getString("Ranks." + previousRank.toUpperCase() + ".Display");
                else previousRankDisplay = "&cN/A (Permission Grant)";
                String ID = String.valueOf(plugin.grantData.config.getInt(target.getUniqueId().toString() + ".Grants." + grant + ".ID"));

                List<String> itemLore = new ArrayList<>();
                itemLore.add(Utils.translate("&e&m----------------------------"));
                itemLore.add(Utils.translate("&eTarget: &f" + playerManagement.getPlayerColor(target)));
                itemLore.add(Utils.translate("&eType: &f" + plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Type")));
                itemLore.add(Utils.translate("&eDuration: &f" + duration));
                itemLore.add(Utils.translate("&e&m----------------------------"));
                itemLore.add(Utils.translate("&eIssued Grant: &f" + issuedGrant));
                itemLore.add(Utils.translate("&eIssued By: &f" + executor));
                itemLore.add(Utils.translate("&eIssued Reason: &f" + reason));
                itemLore.add(Utils.translate("&ePrevious Rank: &f" + previousRankDisplay));
                itemLore.add(Utils.translate("&eGrant ID: &f" + ID));
                itemLore.add(Utils.translate("&e&m----------------------------"));
                if (revokable) {
                    itemLore.add(Utils.translate("&aClick to revoke this grant."));
                }

                itemMeta.setLore(itemLore);
                item.setItemMeta(itemMeta);
                inventory.addItem(item);
            }
        }

        player.openInventory(inventory);
    }
}
