package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.CustomGUI;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ReportHistoryGUI extends CustomGUI {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public ReportHistoryGUI(Player player, int size, String title) {
        super(player, size, title);
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup(OfflinePlayer target, int page) {
        gui.setFiller(new int[] { 9,10,11,12,13,14,15,16,17 });

        Map<Integer, Button> buttons = new HashMap<>();
        Set<Integer> pages = new HashSet<>();

        if (plugin.playerData.config.contains(target.getUniqueId().toString() + ".Report")) {
            int loop = -1;
            for (String report : plugin.playerData.config.getConfigurationSection(target.getUniqueId().toString() + ".Report").getKeys(false)) {
                loop++;
                ItemBuilder itemBuilder = new ItemBuilder(
                        XMaterial.LIME_WOOL.parseItem(),
                        1,
                        "&a&l(Active) " + Utils.getExpirationDate(plugin.playerData.config.getLong(target.getUniqueId().toString() + ".Report." + report  + ".Date")),
                        ItemBuilder.formatLore(new String[] {
                                "&e&m----------------------------",
                                "&eTarget: &f" + playerManagement.getPlayerColor(target),
                                "&e&m----------------------------",
                                "&eIssued By: &f" + playerManagement.getPlayerColor(Bukkit.getOfflinePlayer(UUID.fromString(plugin.playerData.config.getString(target.getUniqueId().toString() + ".Report." + report + ".Executor")))),
                                "&eIssued Reason: &f" + plugin.playerData.config.getString(target.getUniqueId().toString() + ".Report." + report + ".Reason"),
                                "&e&m----------------------------"
                        })
                );

                buttons.put(loop, new Button(
                        itemBuilder.getItem(),
                        itemBuilder.getName(),
                        itemBuilder.getLore()
                ));
            }

            for (Map.Entry<Integer, Button> entry : buttons.entrySet())
                pages.add((entry.getKey() / 9) + 1);
            List<Integer> newPages = new ArrayList<>(pages);

            ItemBuilder nextPage = new ItemBuilder(XMaterial.GRAY_DYE.parseItem(), 1, "&c&lNo next page available", new ArrayList<>());
            ItemBuilder previousPage = new ItemBuilder(XMaterial.GRAY_DYE.parseItem(), 1, "&c&lNo previous page available", new ArrayList<>());

            gui.setButton(17, new Button(nextPage.getItem(), nextPage.getName(), nextPage.getLore()));
            gui.setButton(9, new Button(previousPage.getItem(), previousPage.getName(), previousPage.getLore()));

            AtomicInteger newPage = new AtomicInteger();

            if (Collections.max(newPages) > 1 && page < Collections.max(newPages)) {
                nextPage.setItem(XMaterial.LIME_DYE.parseItem());
                nextPage.setName("&a&lNext page");

                gui.setButton(17, new Button(nextPage.getItem(), () -> {
                    GUI.close(gui);
                    newPage.set(page + 1);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ReportHistoryGUI reportHistoryGUI = new ReportHistoryGUI(player, 18, playerManagement.getPlayerColor(target) + "&a's report history.");
                            reportHistoryGUI.setup(target, newPage.get());
                            GUI.open(reportHistoryGUI.getGui());
                        }
                    }.runTaskLater(plugin, 1);
                }, nextPage.getName(), nextPage.getLore()));
            }

            if (Collections.min(newPages) == 1 && page > Collections.min(newPages)) {
                previousPage.setItem(XMaterial.LIME_DYE.parseItem());
                previousPage.setName("&a&lPrevious page");

                gui.setButton(9, new Button(previousPage.getItem(), () -> {
                    GUI.close(gui);
                    newPage.set(page + -1);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ReportHistoryGUI reportHistoryGUI = new ReportHistoryGUI(player, 18, playerManagement.getPlayerColor(target) + "&a's report history.");
                            reportHistoryGUI.setup(target, newPage.get());
                            GUI.open(reportHistoryGUI.getGui());
                        }
                    }.runTaskLater(plugin, 1);
                }, previousPage.getName(), previousPage.getLore()));
            }

            for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
                for (Map.Entry<Integer, Integer> info : Utils.getPageAndSlot(entry.getKey()).entrySet()) {
                    if (info.getKey() == page)
                        gui.setButton(info.getValue(), entry.getValue());
                }
            }
        }
    }
}
