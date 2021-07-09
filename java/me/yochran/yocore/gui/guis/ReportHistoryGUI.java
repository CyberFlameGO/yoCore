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

            List<Integer> totalPages = new ArrayList<>(pages);
            setupToolbar(getGui(), page, totalPages, target);

            for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
                for (Map.Entry<Integer, Integer> info : Utils.getPageAndSlot(entry.getKey()).entrySet()) {
                    if (info.getKey() == page)
                        gui.setButton(info.getValue(), entry.getValue());
                }
            }
        }
    }

    public void setupToolbar(GUI gui, int page, List<Integer> newPages, OfflinePlayer target) {
        ItemBuilder firstPage = new ItemBuilder(XMaterial.GRAY_DYE.parseItem(), 1, "&c&lYou are on the first page.", new ArrayList<>());
        ItemBuilder lastPage = new ItemBuilder(XMaterial.GRAY_DYE.parseItem(), 1, "&c&lYou are on the last page.", new ArrayList<>());
        ItemBuilder previousPage = new ItemBuilder(XMaterial.GRAY_DYE.parseItem(), 1, "&c&lNo previous page available", new ArrayList<>());
        ItemBuilder nextPage = new ItemBuilder(XMaterial.GRAY_DYE.parseItem(), 1, "&c&lNo next page available", new ArrayList<>());
        ItemBuilder findPage = new ItemBuilder(XMaterial.OAK_SIGN.parseItem(), 1, "&a&lFind a page.", new ArrayList<>());

        gui.setButton(9, new Button(firstPage.getItem(), firstPage.getName(), firstPage.getLore()));
        gui.setButton(10, new Button(previousPage.getItem(), previousPage.getName(), previousPage.getLore()));
        gui.setButton(15, new Button(
                findPage.getItem(),
                () -> {
                    GUI.close(gui);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            FindPageGUI findPageGUI = new FindPageGUI(getGui().getPlayer(), 9, "&aFind a page.");
                            findPageGUI.setup("Reports", null, target, Collections.max(newPages));
                            GUI.open(findPageGUI.getGui());
                        }
                    }.runTaskLater(plugin, 1);
                },
                findPage.getName(),
                findPage.getLore()
        ));
        gui.setButton(16, new Button(nextPage.getItem(), nextPage.getName(), nextPage.getLore()));
        gui.setButton(17, new Button(lastPage.getItem(), lastPage.getName(), lastPage.getLore()));

        AtomicInteger newPage = new AtomicInteger();

        Runnable reopen = () -> new BukkitRunnable() {
            @Override
            public void run() {
                ReportHistoryGUI reportHistoryGUI = new ReportHistoryGUI(player, 18, playerManagement.getPlayerColor(target) + "&a's report history.");
                reportHistoryGUI.setup(target, newPage.get());
                GUI.open(reportHistoryGUI.getGui());
            }
        }.runTaskLater(plugin, 1);

        if (page != 1) {
            firstPage.setItem(XMaterial.MAGENTA_DYE.parseItem());
            firstPage.setName("&a&lFirst page.");
            gui.setButton(9, new Button(firstPage.getItem(), () -> {
                GUI.close(gui);
                newPage.set(1);
                reopen.run();
            }, firstPage.getName(), firstPage.getLore()));
        }
        if (Collections.min(newPages) == 1 && page > Collections.min(newPages)) {
            previousPage.setItem(XMaterial.LIME_DYE.parseItem());
            previousPage.setName("&a&lPrevious page");
            gui.setButton(10, new Button(previousPage.getItem(), () -> {
                GUI.close(gui);
                newPage.set(page - 1);
                reopen.run();
            }, previousPage.getName(), previousPage.getLore()));
        }
        if (Collections.max(newPages) > 1 && page < Collections.max(newPages)) {
            nextPage.setItem(XMaterial.LIME_DYE.parseItem());
            nextPage.setName("&a&lNext page");
            gui.setButton(16, new Button(nextPage.getItem(), () -> {
                GUI.close(gui);
                newPage.set(page + 1);
                reopen.run();
            }, nextPage.getName(), nextPage.getLore()));
        }
        if (page < Collections.max(newPages)) {
            firstPage.setItem(XMaterial.MAGENTA_DYE.parseItem());
            firstPage.setName("&a&lLast page.");
            gui.setButton(17, new Button(firstPage.getItem(), () -> {
                GUI.close(gui);
                newPage.set(Collections.max(newPages));
                reopen.run();
            }, firstPage.getName(), firstPage.getLore()));
        }
    }
}
