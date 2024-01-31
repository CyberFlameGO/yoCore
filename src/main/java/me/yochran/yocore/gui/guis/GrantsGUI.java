package me.yochran.yocore.gui.guis;

import me.yochran.yocore.grants.Grant;
import me.yochran.yocore.grants.GrantType;
import me.yochran.yocore.gui.*;
import me.yochran.yocore.management.PermissionManagement;
import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GrantsGUI extends CustomGUI implements PagedGUI {

    private final yoCore plugin;

    public GrantsGUI(Player player, int size, String title) {
        super(player, size, title);

        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void setupPagedGUI(Map<Integer, Button> entry, int page) {
        for (Map.Entry<Integer, Button> button : entry.entrySet()) {
            int[] data = Utils.getHistorySlotData(button.getKey());
            if (page == data[0])
                gui.setButton(data[1] + 9, button.getValue());
        }
    }

    public void setup(Player player, OfflinePlayer target, int page) {
        yoPlayer yoTarget = new yoPlayer(target);

        String activePrefix = "&a&l(Active) ";
        String revokedPrefix = "&4&l(Revoked) ";
        String expiredPrefix = "&6&l(Expired) ";

        Map<Integer, Button> buttons = new HashMap<>();
        Set<Integer> pages = new HashSet<>();

        int loop = -1;
        if (plugin.grantData.config.contains(target.getUniqueId().toString() + ".Grants")) {
            for (Map.Entry<Integer, Grant> grant : Grant.getGrants(yoTarget).entrySet()) {
                loop++;

                String issuedGrant;
                if (grant.getValue().getType() == GrantType.RANK) {
                    if (grant.getValue().getGrant().equalsIgnoreCase("(Removed Rank)"))
                        issuedGrant = "&4(Removed Rank)";
                    else issuedGrant = plugin.getConfig().getString("Ranks." + grant.getValue().getGrant().toUpperCase() + ".Display");
                } else issuedGrant = grant.getValue().getGrant();

                String executor = yoPlayer.getYoPlayer(grant.getValue().getExecutor()).getDisplayName();

                String duration;
                if (grant.getValue().getDuration() instanceof String) duration = "Permanent";
                else duration = Utils.getExpirationDate((long) grant.getValue().getDuration());

                String reason = grant.getValue().getReason();

                String previousRank = grant.getValue().getPreviousRank();
                String previousRankDisplay;
                if (!previousRank.equalsIgnoreCase("N/A"))
                    previousRankDisplay = Rank.getRank(previousRank).getDisplay();
                else previousRankDisplay = "&cN/A (Permission Grant)";

                String ID = String.valueOf(grant.getValue().getID());

                String revokePermission;
                if (previousRank.equalsIgnoreCase("N/A")) revokePermission = issuedGrant;
                else revokePermission = Rank.getRank(previousRank).getGrantPermission();

                ItemBuilder itemBuilder = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&4&lNULL", ItemBuilder.formatLore(new String[] {
                        "&3&m----------------------------",
                        "&bTarget: &3" + yoTarget.getDisplayName(),
                        "&bType: &3" + GrantType.convertToString(grant.getValue().getType()),
                        "&bDuration: &3" + duration,
                        "&b ",
                        "&bIssued Grant: &3" + issuedGrant,
                        "&bIssued By: &3" + executor,
                        "&bIssued Reason: &3" + reason,
                        "&bPrevious Rank: &3" + previousRankDisplay,
                        "&bGrant ID: &3" + ID,
                        "&3&m----------------------------"
                }));

                switch (grant.getValue().getStatus().toLowerCase()) {
                    case "active":
                        itemBuilder.setItem(XMaterial.LIME_WOOL.parseItem());
                        itemBuilder.setName(activePrefix + Utils.getExpirationDate(grant.getValue().getDate()));

                        if (player.hasPermission(revokePermission))
                            itemBuilder.getLore().add(Utils.translate("&aClick to revoke this grant."));
                        else itemBuilder.getLore().add(Utils.translate("&cYou cannot remove this grant."));

                        break;
                    case "revoked":
                        itemBuilder.setItem(XMaterial.RED_WOOL.parseItem());
                        itemBuilder.setName(revokedPrefix + Utils.getExpirationDate(grant.getValue().getDate()));
                        break;
                    case "expired":
                        itemBuilder.setItem(XMaterial.ORANGE_WOOL.parseItem());
                        itemBuilder.setName(expiredPrefix + Utils.getExpirationDate(grant.getValue().getDate()));
                        break;
                }

                Button button = new Button(
                        itemBuilder.getItem(),
                        itemBuilder.getName(),
                        itemBuilder.getLore()
                );

                if (grant.getValue().getStatus().equalsIgnoreCase("Active")
                        && itemBuilder.getLore().contains(Utils.translate("&aClick to revoke this grant."))) {
                    button.setAction(() -> {
                        GUI.close(gui);

                        player.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.RevokedGrant")));
                        grant.getValue().revoke();
                    });
                }

                buttons.put(loop, button);
            }

            for (Map.Entry<Integer, Button> entry : buttons.entrySet()) pages.add((entry.getKey() / 9) + 1);

            Toolbar toolbar = new Toolbar(getGui(), "Grants", page, new ArrayList<>(pages), () -> new BukkitRunnable() {
                @Override
                public void run() {
                    GrantsGUI grantsGUI = new GrantsGUI(player, 18, yoTarget.getDisplayName() + "&a's grant history.");
                    grantsGUI.setup(player, target, Toolbar.getNewPage().get());
                    GUI.open(grantsGUI.getGui());
                }
            }.runTaskLater(plugin, 1));

            toolbar.create(target, null, false);
            setupPagedGUI(buttons, page);
        }
    }
}
