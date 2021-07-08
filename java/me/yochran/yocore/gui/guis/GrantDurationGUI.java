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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GrantDurationGUI extends CustomGUI {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public GrantDurationGUI(Player player, int size, String title) {
        super(player, size, title);

        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup(Player player, OfflinePlayer target) {
        for (String cItem : plugin.getConfig().getConfigurationSection("Grant.Duration.Items").getKeys(false)) {
            List<String> itemLore = new ArrayList<>();
            for (String line : plugin.getConfig().getStringList("Grant.Duration.Lore")) {
                itemLore.add(line
                        .replace("%duration%", plugin.getConfig().getString("Grant.Duration.Items." + cItem + ".Name"))
                        .replace("%target%", playerManagement.getPlayerColor(target)));
            }

            ItemBuilder itemBuilder = new ItemBuilder(
                    Utils.getMaterialFromConfig(plugin.getConfig().getString("Grant.Duration.Items." + cItem + ".Item")),
                    1,
                    plugin.getConfig().getString("Grant.Duration.Items." + cItem + ".Name"),
                    ItemBuilder.translateLore(itemLore)
            );

            gui.setButton(plugin.getConfig().getInt("Grant.Duration.Items." + cItem + ".Slot"), new Button(
                    itemBuilder.getItem(),
                    () -> {
                        GUI.close(gui);

                        plugin.grant_duration.put(player.getUniqueId(), plugin.getConfig().getString("Grant.Duration.Items." + cItem + ".ID"));

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                GrantReasonGUI grantReasonGUI = new GrantReasonGUI(player, 27, "&aSelect a reason.");
                                grantReasonGUI.setup(player, target);
                                GUI.open(grantReasonGUI.getGui());
                            }
                        }.runTaskLater(plugin, 1);
                    },
                    itemBuilder.getName(),
                    itemBuilder.getLore()
            ));
        }
    }
}
