package me.yochran.yocore.gui.guis;

import me.yochran.yocore.grants.Grant;
import me.yochran.yocore.grants.GrantType;
import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.CustomGUI;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.management.GrantManagement;
import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GrantConfirmGUI extends CustomGUI {

    private final yoCore plugin;
    private final GrantManagement grantManagement = new GrantManagement();

    public GrantConfirmGUI(Player player, int size, String title) {
        super(player, size, title);

        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup(Player player, OfflinePlayer target, String grant, String duration, String reason) {
        yoPlayer yoTarget = new yoPlayer(target);

        String display;
        if (plugin.grant_type.get(player.getUniqueId()) == GrantType.RANK) display = Rank.getRank(grant).getDisplay();
        else display = grant;

        List<String> lore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("Grant.Confirm.Lore")) {
            lore.add(line
                    .replace("%target%", yoTarget.getDisplayName())
                    .replace("%grant%", display)
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
                            .replace("%target%", yoTarget.getDisplayName())
                            .replace("%grant%", display)
                            .replace("%duration%", duration)
                            .replace("%reason%", reason)));

                    Object dur = duration;
                    if (!duration.equalsIgnoreCase("Permanent")) dur = grantManagement.getGrantDuration(duration);

                    Grant grantToCreate = new Grant(plugin.grant_type.get(player.getUniqueId()), grant, yoTarget, player, dur, reason, yoTarget.getRank().getID());
                    grantToCreate.create();
                    grantToCreate.grant(plugin.grant_type.get(player.getUniqueId()), grant);

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
