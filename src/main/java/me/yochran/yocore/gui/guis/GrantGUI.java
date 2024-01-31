package me.yochran.yocore.gui.guis;

import me.yochran.yocore.grants.GrantType;
import me.yochran.yocore.gui.*;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GrantGUI extends CustomGUI implements PagedGUI {

    private final yoCore plugin;

    public GrantGUI(Player player, int size, String title) {
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
        int loop = -1;

        Map<Integer, Button> buttons = new HashMap<>();
        Set<Integer> pages = new HashSet<>();

        for (Map.Entry<String, Rank> rank : Rank.getRanks().entrySet()) {
            loop++;

            ItemStack item = rank.getValue().getGrantItem();

            String permission;
            if (player.hasPermission(rank.getValue().getGrantPermission())) permission = "&aYou can grant this rank.";
            else {
                permission = "&cYou cannot grant this rank.";
                item = Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.NoPermissionItem"));
            }

            ItemBuilder itemBuilder = new ItemBuilder(
                    item,
                    1,
                    rank.getValue().getDisplay(),
                    ItemBuilder.formatLore(new String[] {
                            "&3&m----------------------------",
                            "&bID: &3" + rank.getValue().getID(),
                            "&bPrefix: &3" + rank.getValue().getPrefix(),
                            "&bDisplay Name: &3" + rank.getValue().getDisplay(),
                            "&bColor: &3" + rank.getValue().getColor() + "Example",
                            "&bTab Index: &3" + rank.getValue().getTabIndex(),
                            "&bType: &3Rank",
                            "&b ",
                            permission,
                            "&3&m----------------------------",
                    })
            );

            Button button = new Button(
                    itemBuilder.getItem(),
                    1,
                    () -> {
                        if (player.hasPermission(rank.getValue().getGrantPermission())) {
                            GUI.close(gui);

                            plugin.grant_type.put(player.getUniqueId(), GrantType.RANK);
                            plugin.grant_grant.put(player.getUniqueId(), rank.getValue().getID());

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    GrantDurationGUI grantDurationGUI = new GrantDurationGUI(player, 36, "&aSelect a duration.");
                                    grantDurationGUI.setup(player, target);
                                    GUI.open(grantDurationGUI.getGui());
                                }
                            }.runTaskLater(plugin, 1);
                        }
                    },
                    itemBuilder.getName(),
                    itemBuilder.getLore()
            );

            buttons.put(loop, button);
        }

        for (String perm : plugin.getConfig().getConfigurationSection("Grant.Permission.Items").getKeys(false)) {
            loop++;

            ItemStack item = Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Item"));

            String permission;
            if (player.hasPermission(plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Permission"))) { permission = "&aYou can grant this permission."; } else {
                permission = "&cYou cannot grant this permission.";
                item = Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.NoPermissionItem"));
            }

            List<String> itemLore = new ArrayList<>();
            for (String line : plugin.getConfig().getStringList("Grant.Permission.Lore")) {
                itemLore.add(line
                        .replace("%permission%", plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Permission"))
                        .replace("%has_permission%", permission));
            }

            ItemBuilder itemBuilder = new ItemBuilder(
                    item,
                    1,
                    plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Name"),
                    ItemBuilder.translateLore(itemLore)
            );

            Button button = new Button(
                    itemBuilder.getItem(),
                    () -> {
                        if (player.hasPermission(plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Permission"))) {
                            GUI.close(gui);

                            plugin.grant_type.put(player.getUniqueId(), GrantType.PERMISSION);
                            plugin.grant_grant.put(player.getUniqueId(), plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Permission"));

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    GrantDurationGUI grantDurationGUI = new GrantDurationGUI(player, 36, "&aSelect a duration.");
                                    grantDurationGUI.setup(player, target);
                                    GUI.open(grantDurationGUI.getGui());
                                }
                            }.runTaskLater(plugin, 1);
                        }
                    },
                    itemBuilder.getName(),
                    itemBuilder.getLore()
            );

            buttons.put(loop, button);
        }

        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) pages.add((entry.getKey() / 9) + 1);

        Toolbar toolbar = new Toolbar(getGui(), "Grant", page, new ArrayList<>(pages), () -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    GrantGUI grantGUI = new GrantGUI(player, 18, "&aSelect a grant.");
                    grantGUI.setup(player, target, Toolbar.getNewPage().get());
                    GUI.open(grantGUI.getGui());
                }
            }.runTaskLater(plugin, 1);
        });

        toolbar.create(target, null, false);
        setupPagedGUI(buttons, page);
    }
}
