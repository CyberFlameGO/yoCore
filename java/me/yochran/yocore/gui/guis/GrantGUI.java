package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.management.GrantManagement;
import me.yochran.yocore.management.PermissionManagement;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GrantGUI {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public GrantGUI() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void openGrantGUI(Player player, OfflinePlayer target) {
        GUI gui = new GUI(player, 54, "&aSelect a grant.");

        int loop = -1;
        for (String rank : plugin.getConfig().getConfigurationSection("Ranks").getKeys(false)) {
            loop++;
            Button item = new Button(Utils.getMaterialFromConfig(plugin.getConfig().getString("Ranks." + rank + ".GrantItem")), () -> {
                if (player.hasPermission(plugin.getConfig().getString("Ranks." + rank + ".GrantPermission"))) {
                    gui.close();

                    plugin.grant_type.put(player.getUniqueId(), "RANK");
                    plugin.grant_grant.put(player.getUniqueId(), plugin.getConfig().getString("Ranks." + rank + ".ID"));

                    new BukkitRunnable() {
                        @Override
                        public void run() { openDurationGUI(player, target); }
                    }.runTaskLater(plugin, 1);
                }
            }, plugin.getConfig().getString("Ranks." + rank + ".Display"));

            List<String> itemLore = new ArrayList<>();
            String permission;
            if (player.hasPermission(plugin.getConfig().getString("Ranks." + rank + ".GrantPermission"))) { permission = "&a&lYou can grant this rank."; } else {
                permission = "&c&lYou cannot grant this rank.";
                item.setItem(Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.NoPermissionItem")));
            }

            itemLore.add(Utils.translate("&e&m----------------------------"));
            itemLore.add(Utils.translate("&eID: &f" + plugin.getConfig().getString("Ranks." + rank + ".ID")));
            itemLore.add(Utils.translate("&ePriority: &f" + plugin.getConfig().getInt("Ranks." + rank + ".Priority")));
            itemLore.add(Utils.translate("&ePrefix: &f" + plugin.getConfig().getString("Ranks." + rank + ".Prefix")));
            itemLore.add(Utils.translate("&eDisplay Name: &f" + plugin.getConfig().getString("Ranks." + rank + ".Display")));
            itemLore.add(Utils.translate("&e&m----------------------------"));
            itemLore.add(Utils.translate("&eType: &fRank"));
            itemLore.add(Utils.translate("&e&m----------------------------"));
            itemLore.add(Utils.translate(permission));

            item.setLore(itemLore);
            gui.setButton(loop, item);
        }

        for (String perm : plugin.getConfig().getConfigurationSection("Grant.Permission.Items").getKeys(false)) {
            loop++;
            Button item = new Button(Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Item")), () -> {
                if (player.hasPermission(plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Permission"))) {
                    gui.close();

                    plugin.grant_type.put(player.getUniqueId(), "PERMISSION");
                    plugin.grant_grant.put(player.getUniqueId(), plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Permission"));

                    new BukkitRunnable() {
                        @Override
                        public void run() { openDurationGUI(player, target); }
                    }.runTaskLater(plugin, 1);
                }
            }, plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Name"));

            List<String> itemLore = new ArrayList<>();
            String permission;
            if (player.hasPermission(plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Permission"))) { permission = "&a&lYou can grant this permission."; } else {
                permission = "&c&lYou cannot grant this permission.";
                item.setItem(Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.NoPermissionItem")));
            }

            for (String line : plugin.getConfig().getStringList("Grant.Permission.Lore")) {
                itemLore.add(Utils.translate(line
                        .replace("%permission%", plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Permission"))
                        .replace("%has_permission%", permission)));
            }

            item.setLore(itemLore);
            gui.setButton(loop, item);
        }

        gui.open();
    }

    public void openDurationGUI(Player player, OfflinePlayer target) {
        GUI gui = new GUI(player, 36, "&aSelect a duration.");

        for (String cItem : plugin.getConfig().getConfigurationSection("Grant.Duration.Items").getKeys(false)) {
            List<String> itemLore = new ArrayList<>();
            for (String line : plugin.getConfig().getStringList("Grant.Duration.Lore")) {
                itemLore.add(Utils.translate(line
                        .replace("%duration%", plugin.getConfig().getString("Grant.Duration.Items." + cItem + ".Name"))
                        .replace("%target%", playerManagement.getPlayerColor(target))));
            }

            Button item = new Button(Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.Duration.Items." + cItem + ".Item")), plugin.getConfig().getString("Grant.Duration.Items." + cItem + ".Name"), itemLore);

            item.setAction(() -> {
                gui.close();

                plugin.grant_duration.put(player.getUniqueId(), plugin.getConfig().getString("Grant.Duration.Items." + cItem + ".ID"));

                new BukkitRunnable() {
                    @Override
                    public void run() { openReasonGUI(player, target); }
                }.runTaskLater(plugin, 1);
            });

            gui.setButton(plugin.getConfig().getInt("Grant.Duration.Items." + cItem + ".Slot"), item);
        }

        gui.open();
    }

    public void openReasonGUI(Player player, OfflinePlayer target) {
        GUI gui = new GUI(player, 27, "&aSelect a reason.");

        for (String cItem : plugin.getConfig().getConfigurationSection("Grant.Reason.Items").getKeys(false)) {
            List<String> itemLore = new ArrayList<>();
            for (String line : plugin.getConfig().getStringList("Grant.Reason.Lore")) {
                itemLore.add(Utils.translate(line
                        .replace("%duration%", plugin.getConfig().getString("Grant.Reason.Items." + cItem + ".Name"))
                        .replace("%target%", playerManagement.getPlayerColor(target))));
            }

            Button item = new Button(Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.Reason.Items." + cItem + ".Item")), plugin.getConfig().getString("Grant.Reason.Items." + cItem + ".Name"), itemLore);

            item.setAction(() -> {
                gui.close();

                if (!plugin.getConfig().getString("Grant.Reason.Items." + cItem + ".ID").equalsIgnoreCase(Utils.translate(plugin.getConfig().getString("Grant.Reason.Items.Custom.ID")))) {
                    plugin.grant_reason.put(player.getUniqueId(), plugin.getConfig().getString("Grant.Reason.Items." + cItem + ".ID"));

                    new BukkitRunnable() {
                        @Override
                        public void run() { openConfirmGUI(player, target, plugin.grant_grant.get(player.getUniqueId()), plugin.grant_duration.get(player.getUniqueId()), plugin.grant_reason.get(player.getUniqueId())); }
                    }.runTaskLater(plugin, 1);

                } else {
                    plugin.grant_custom_reason.add(player.getUniqueId());
                    player.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.Reason.CustomReasonChatMessage")));
                }
            });

            gui.setButton(plugin.getConfig().getInt("Grant.Reason.Items." + cItem + ".Slot"), item);
        }

        gui.open();
    }

    public void openConfirmGUI(Player player, OfflinePlayer target, String grant, String duration, String reason) {
        GUI gui = new GUI(player, 45, "&aConfirm the grant.");

        String finalGrant;
        if (plugin.grant_type.get(player.getUniqueId()).equalsIgnoreCase("RANK"))
            finalGrant = plugin.getConfig().getString("Ranks." + grant + ".Display");
        else finalGrant = grant;

        List<String> lore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("Grant.Confirm.Lore")) {
            lore.add(Utils.translate(line
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%grant%", finalGrant)
                    .replace("%duration%", duration)
                    .replace("%reason%", reason)));
        }

        Button yesButton = new Button(XMaterial.GREEN_TERRACOTTA.parseItem(), () -> {
            gui.close();

            player.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.Confirm.ConfirmedGrant")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%grant%", finalGrant)
                    .replace("%duration%", plugin.grant_duration.get(player.getUniqueId()))
                    .replace("%reason%", plugin.grant_reason.get(player.getUniqueId()))));

            String previousRank;
            String type = plugin.grant_type.get(player.getUniqueId());

            if (plugin.grant_type.get(player.getUniqueId()).equalsIgnoreCase("RANK")) {
                previousRank = plugin.playerData.config.getString(target.getUniqueId().toString() + ".Rank");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + target.getName() + " " + plugin.grant_grant.get(player.getUniqueId()));
            } else {
                previousRank = "N/A";
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "user " + target.getName() + " add " + plugin.grant_grant.get(player.getUniqueId()));
            }

            if (target.isOnline()) new PermissionManagement().setupPlayer(Bukkit.getPlayer(target.getUniqueId()));

            if (plugin.grant_duration.get(player.getUniqueId()).equalsIgnoreCase("Permanent")) new GrantManagement().addGrant(target, player.getUniqueId().toString(), type, plugin.grant_grant.get(player.getUniqueId()), "Permanent", System.currentTimeMillis(), reason, previousRank);
            else new GrantManagement().addGrant(target, player.getUniqueId().toString(), type, plugin.grant_grant.get(player.getUniqueId()), new GrantManagement().getGrantDuration(plugin.grant_duration.get(player.getUniqueId())), System.currentTimeMillis(), reason, previousRank);

            plugin.grant_player.remove(player.getUniqueId());
            plugin.grant_type.remove(player.getUniqueId());
            plugin.grant_grant.remove(player.getUniqueId());
            plugin.grant_reason.remove(player.getUniqueId());
            plugin.grant_duration.remove(player.getUniqueId());
        },"&2&lConfirm Grant", lore);

        Button noButton = new Button(XMaterial.RED_TERRACOTTA.parseItem(), () -> {
            gui.close();

            player.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.CancelledGrant")));

            plugin.grant_player.remove(player.getUniqueId());
            plugin.grant_type.remove(player.getUniqueId());
            plugin.grant_grant.remove(player.getUniqueId());
            plugin.grant_reason.remove(player.getUniqueId());
            plugin.grant_duration.remove(player.getUniqueId());
        }, "&c&lCancel Grant", lore);

        for (int i = 10; i < 13; i++) gui.setButton(i, yesButton);
        for (int i = 19; i < 22; i++) gui.setButton(i, yesButton);
        for (int i = 28; i < 31; i++) gui.setButton(i, yesButton);
        for (int i = 14; i < 17; i++) gui.setButton(i, noButton);
        for (int i = 23; i < 26; i++) gui.setButton(i, noButton);
        for (int i = 32; i < 35; i++) gui.setButton(i, noButton);

        gui.open();
    }
}
