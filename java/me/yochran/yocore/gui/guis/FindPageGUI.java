package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.CustomGUI;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class FindPageGUI extends CustomGUI {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public FindPageGUI(Player player, int size, String title) {
        super(player, size, title);
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup(String cl, String type, OfflinePlayer target, int pages) {
        ItemBuilder itemBuilder = new ItemBuilder(XMaterial.BOOK.parseItem(), 1, "&4&lNULL", new ArrayList<>());

        for (int i = 0; i < pages; i++) {
            int page = i;
            gui.setButton(i, new Button(
                    itemBuilder.getItem(),
                    () -> {
                        GUI.close(gui);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                switch (cl.toLowerCase()) {
                                    case "punishments":
                                        DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(getGui().getPlayer(), 18, playerManagement.getPlayerColor(target) + "&a's " + type.toLowerCase() + "&as.");
                                        detailedPunishmentHistoryGUI.setup(type, getGui().getPlayer(), target, (page + 1));
                                        GUI.open(detailedPunishmentHistoryGUI.getGui());
                                        break;
                                    case "grants":
                                        GrantsGUI grantsGUI = new GrantsGUI(getGui().getPlayer(), 18, playerManagement.getPlayerColor(target) + "&a's grant history.");
                                        grantsGUI.setup(getGui().getPlayer(), target, (page + 1));
                                        GUI.open(grantsGUI.getGui());
                                        break;
                                    case "reports":
                                        ReportHistoryGUI reportHistoryGUI = new ReportHistoryGUI(getGui().getPlayer(), 18, playerManagement.getPlayerColor(target) + "&a's report history.");
                                        reportHistoryGUI.setup(target, (page + 1));
                                        GUI.open(reportHistoryGUI.getGui());
                                        break;
                                }
                            }
                        }.runTaskLater(plugin, 1);
                    },
                    "&a&lPage " + (i + 1),
                    itemBuilder.getLore()
            ));
        }
    }
}
