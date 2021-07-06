package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.management.PermissionManagement;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrantsGUI {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public GrantsGUI() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void openHistoryGUI(Player player, OfflinePlayer target) {
        GUI gui = new GUI(player, 54, playerManagement.getPlayerColor(target) + "&a's grant history.");

        String activePrefix = "&a&l(Active) ";
        String revokedPrefix = "&4&l(Revoked) ";
        String expiredPrefix = "&6&l(Expired) ";

        int loop = -1;
        if (plugin.grantData.config.contains(target.getUniqueId().toString() + ".Grants")) {
            for (String grant : plugin.grantData.config.getConfigurationSection(target.getUniqueId().toString() + ".Grants").getKeys(false)) {
                loop++;

                String issuedGrant;
                if (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Type").equalsIgnoreCase("RANK")) {
                    if (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Grant").equalsIgnoreCase("(Removed Rank)"))
                        issuedGrant = "&4(Removed Rank)";
                    else issuedGrant = plugin.getConfig().getString("Ranks." + plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Grant").toUpperCase() + ".Display");
                } else issuedGrant  = plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Grant");
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

                String revokePermission;
                if (previousRank.equalsIgnoreCase("N/A")) revokePermission = issuedGrant;
                else revokePermission = plugin.getConfig().getString("Ranks." + previousRank + ".GrantPermission");

                Button item = new Button(XMaterial.BEDROCK.parseItem(),"&4&lNULL");
                boolean revokable = false;

                switch (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Status").toLowerCase()) {
                    case "active":
                        revokable = true;
                        item.setItem(XMaterial.LIME_WOOL.parseItem());
                        item.setName(activePrefix + Utils.getExpirationDate(plugin.grantData.config.getLong(target.getUniqueId().toString() + ".Grants." + grant + ".Date")));
                        break;
                    case "revoked":
                        item.setItem(XMaterial.RED_WOOL.parseItem());
                        item.setName(revokedPrefix + Utils.getExpirationDate(plugin.grantData.config.getLong(target.getUniqueId().toString() + ".Grants." + grant + ".Date")));
                        break;
                    case "expired":
                        item.setItem(XMaterial.ORANGE_WOOL.parseItem());
                        item.setName(expiredPrefix + Utils.getExpirationDate(plugin.grantData.config.getLong(target.getUniqueId().toString() + ".Grants." + grant + ".Date")));
                        break;
                }

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
                    if (player.hasPermission(revokePermission))
                        itemLore.add(Utils.translate("&aClick to revoke this grant."));
                    else itemLore.add(Utils.translate("&cYou cannot remove this grant."));
                }
                if (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Status").equalsIgnoreCase("Active")
                        && itemLore.contains(Utils.translate("&aClick to revoke this grant."))) {
                    System.out.println("debug");
                    item.setAction(() -> {
                        gui.close();

                        player.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.RevokedGrant")));

                        int id = plugin.grantData.config.getInt(target.getUniqueId().toString() + ".Grants." + grant + ".ID");

                        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + id + ".Status", "Revoked");
                        plugin.grantData.saveData();

                        if (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + id + ".Type").equalsIgnoreCase("RANK"))
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + target.getName() + " " + plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + id + ".PreviousRank"));
                        else
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "user " + target.getName() + " remove " + plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + id + ".Grant"));

                        if (target.isOnline())
                            new PermissionManagement().setupPlayer(Bukkit.getPlayer(target.getUniqueId()));

                        new BukkitRunnable() {
                            @Override
                            public void run() { openHistoryGUI(player, target); }
                        }.runTaskLater(plugin, 1);
                    });
                }

                item.setLore(itemLore);
                gui.setButton(loop, item);
            }
        }

        gui.open();
    }
}
