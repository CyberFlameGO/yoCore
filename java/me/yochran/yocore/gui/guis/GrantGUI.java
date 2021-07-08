package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.CustomGUI;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GrantGUI extends CustomGUI {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public GrantGUI(Player player, int size, String title) {
        super(player, size, title);

        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup(Player player, OfflinePlayer target) {
        int loop = -1;
        for (String rank : plugin.getConfig().getConfigurationSection("Ranks").getKeys(false)) {
            loop++;

            ItemBuilder itemBuilder = new ItemBuilder(
                    Utils.getMaterialFromConfig(plugin.getConfig().getString("Ranks." + rank + ".GrantItem")),
                    1,
                    plugin.getConfig().getString("Ranks." + rank + ".Display"),
                    ItemBuilder.formatLore(new String[] {
                            "&e&m----------------------------",
                            "&eID: &f" + plugin.getConfig().getString("Ranks." + rank + ".ID"),
                            "&ePriority: &f" + plugin.getConfig().getInt("Ranks." + rank + ".Priority"),
                            "&ePrefix: &f" + plugin.getConfig().getString("Ranks." + rank + ".Prefix"),
                            "&eDisplay Name: &f" + plugin.getConfig().getString("Ranks." + rank + ".Display"),
                            "&e&m----------------------------",
                            "&eType: &fRank",
                            "&e&m----------------------------"
                    })
            );

            String permission;
            if (player.hasPermission(plugin.getConfig().getString("Ranks." + rank + ".GrantPermission")))
                permission = "&a&lYou can grant this rank.";
            else {
                permission = "&c&lYou cannot grant this rank.";
                itemBuilder.setItem(Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.NoPermissionItem")));
            }

            itemBuilder.getLore().add(Utils.translate(permission));

            gui.setButton(loop, new Button(
                    itemBuilder.getItem(),
                    1,
                    () -> {
                        if (player.hasPermission(plugin.getConfig().getString("Ranks." + rank + ".GrantPermission"))) {
                            GUI.close(gui);

                            plugin.grant_type.put(player.getUniqueId(), "RANK");
                            plugin.grant_grant.put(player.getUniqueId(), plugin.getConfig().getString("Ranks." + rank + ".ID"));

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
            ));
        }

        for (String perm : plugin.getConfig().getConfigurationSection("Grant.Permission.Items").getKeys(false)) {
            loop++;

            ItemStack item = Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Item"));

            String permission;
            if (player.hasPermission(plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Permission"))) { permission = "&a&lYou can grant this permission."; } else {
                permission = "&c&lYou cannot grant this permission.";
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

            gui.setButton(loop, new Button(
                    itemBuilder.getItem(),
                    () -> {
                        if (player.hasPermission(plugin.getConfig().getString("Grant.Permission.Items." + perm + ".Permission"))) {
                            GUI.close(gui);

                            plugin.grant_type.put(player.getUniqueId(), "PERMISSION");
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
            ));
        }
    }
}
