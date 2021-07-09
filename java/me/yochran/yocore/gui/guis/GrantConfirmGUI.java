package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.CustomGUI;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.management.GrantManagement;
import me.yochran.yocore.management.PermissionManagement;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GrantConfirmGUI extends CustomGUI {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public GrantConfirmGUI(Player player, int size, String title) {
        super(player, size, title);

        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup(Player player, OfflinePlayer target, String grant, String duration, String reason) {
        String finalGrant;
        if (plugin.grant_type.get(player.getUniqueId()).equalsIgnoreCase("RANK"))
            finalGrant = plugin.getConfig().getString("Ranks." + grant + ".Display");
        else finalGrant = grant;

        List<String> lore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("Grant.Confirm.Lore")) {
            lore.add(line
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%grant%", finalGrant)
                    .replace("%duration%", duration)
                    .replace("%reason%", reason));
        }

        ItemBuilder yesItem = new ItemBuilder(XMaterial.GREEN_TERRACOTTA.parseItem(), 1, "&2&lConfirm Grant", ItemBuilder.translateLore(lore));
        ItemBuilder noItem = new ItemBuilder(XMaterial.RED_TERRACOTTA.parseItem(), 1, "&4&lCancel Grant", ItemBuilder.translateLore(lore));

        Button yesButton = new Button(
                yesItem.getItem(),
                () -> {
                    GUI.close(gui);

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
                },
                yesItem.getName(),
                yesItem.getLore()
        );
        Button noButton = new Button(
                noItem.getItem(),
                () -> {
                    GUI.close(gui);

                    player.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.Confirm.CancelledGrant")));

                    plugin.grant_player.remove(player.getUniqueId());
                    plugin.grant_type.remove(player.getUniqueId());
                    plugin.grant_grant.remove(player.getUniqueId());
                    plugin.grant_reason.remove(player.getUniqueId());
                    plugin.grant_duration.remove(player.getUniqueId());
                },
                noItem.getName(),
                noItem.getLore()
        );

        for (int i = 10; i < 13; i++) gui.setButton(i, yesButton);
        for (int i = 19; i < 22; i++) gui.setButton(i, yesButton);
        for (int i = 28; i < 31; i++) gui.setButton(i, yesButton);
        for (int i = 14; i < 17; i++) gui.setButton(i, noButton);
        for (int i = 23; i < 26; i++) gui.setButton(i, noButton);
        for (int i = 32; i < 35; i++) gui.setButton(i, noButton);
    }
}
