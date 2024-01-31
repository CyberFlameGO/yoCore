package me.yochran.yocore.gui;

import me.yochran.yocore.gui.guis.FindPageGUI;
import me.yochran.yocore.gui.guis.PunishmentHistoryGUI;
import me.yochran.yocore.punishments.PunishmentType;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Toolbar {

    private final yoCore plugin;

    private GUI gui;
    private int page;
    private List<Integer> totalPages;
    private Runnable reopen;
    private String type;
    private static AtomicInteger newPage;

    public Toolbar(GUI gui, String type, int page, List<Integer> totalPages, Runnable reopen) {
        plugin = yoCore.getPlugin(yoCore.class);

        this.gui = gui;
        this.page = page;
        this.totalPages = totalPages;
        this.reopen = reopen;
        this.type = type;
        newPage = new AtomicInteger(1);
    }

    public GUI getGui() { return gui; }
    public List<Integer> getTotalPages() { return totalPages; }
    public Runnable getReopen() { return reopen; }
    public String getType() { return type; }
    public static AtomicInteger getNewPage() { return newPage; }

    public void create(OfflinePlayer target, PunishmentType type, boolean bottomRow) {
        gui.setFiller(new int[] { 0,1,2,3,4,5,6,7,8 });
        ItemBuilder firstPage = new ItemBuilder(XMaterial.GRAY_DYE.parseItem(), 1, "&c&lYou are on the first page.", new ArrayList<>());
        ItemBuilder lastPage = new ItemBuilder(XMaterial.GRAY_DYE.parseItem(), 1, "&c&lYou are on the last page.", new ArrayList<>());
        ItemBuilder previousPage = new ItemBuilder(XMaterial.GRAY_DYE.parseItem(), 1, "&c&lNo previous page available", new ArrayList<>());
        ItemBuilder nextPage = new ItemBuilder(XMaterial.GRAY_DYE.parseItem(), 1, "&c&lNo next page available", new ArrayList<>());
        ItemBuilder findPage = new ItemBuilder(XMaterial.OAK_SIGN.parseItem(), 1, "&a&lFind a page.", new ArrayList<>());
        gui.setButton(0, new Button(firstPage.getItem(), firstPage.getName(), firstPage.getLore()));
        gui.setButton(1, new Button(previousPage.getItem(), previousPage.getName(), previousPage.getLore()));
        gui.setButton(4, new Button(
                findPage.getItem(),
                () -> {
                    GUI.close(gui);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            FindPageGUI findPageGUI = new FindPageGUI(getGui().getPlayer(), 9, "&aFind a page.");
                            findPageGUI.setup(getType(), type, target, Collections.max(getTotalPages()));
                            GUI.open(findPageGUI.getGui());
                        }
                    }.runTaskLater(plugin, 1);
                },
                findPage.getName(),
                findPage.getLore()
        ));
        gui.setButton(7, new Button(nextPage.getItem(), nextPage.getName(), nextPage.getLore()));
        gui.setButton(8, new Button(lastPage.getItem(), lastPage.getName(), lastPage.getLore()));

        Runnable reopen = getReopen();

        if (page != 1) {
            firstPage.setItem(XMaterial.MAGENTA_DYE.parseItem());
            firstPage.setName("&a&lFirst page.");
            gui.setButton(0, new Button(firstPage.getItem(), () -> {
                GUI.close(gui);
                newPage.set(1);
                reopen.run();
            }, firstPage.getName(), firstPage.getLore()));
        }
        if (Collections.min(getTotalPages()) == 1 && page > Collections.min(getTotalPages())) {
            previousPage.setItem(XMaterial.LIME_DYE.parseItem());
            previousPage.setName("&a&lPrevious page");
            gui.setButton(1, new Button(previousPage.getItem(), () -> {
                GUI.close(gui);
                newPage.set(page - 1);
                reopen.run();
            }, previousPage.getName(), previousPage.getLore()));
        }
        if (Collections.max(getTotalPages()) > 1 && page < Collections.max(getTotalPages())) {
            nextPage.setItem(XMaterial.LIME_DYE.parseItem());
            nextPage.setName("&a&lNext page");
            gui.setButton(7, new Button(nextPage.getItem(), () -> {
                GUI.close(gui);
                newPage.set(page + 1);
                reopen.run();
            }, nextPage.getName(), nextPage.getLore()));
        }
        if (page < Collections.max(getTotalPages())) {
            firstPage.setItem(XMaterial.MAGENTA_DYE.parseItem());
            firstPage.setName("&a&lLast page.");
            gui.setButton(8, new Button(firstPage.getItem(), () -> {
                GUI.close(gui);
                newPage.set(Collections.max(getTotalPages()));
                reopen.run();
            }, firstPage.getName(), firstPage.getLore()));
        }

        if (bottomRow) {
            gui.setFiller(new int[] { 18,19,20,21,22,23,24,25,26 });

            ItemBuilder back = new ItemBuilder(XMaterial.ARROW.parseItem(), 1, "&6&lGo back to main menu.", new ArrayList<>());
            ItemBuilder exit = new ItemBuilder(XMaterial.BARRIER.parseItem(), 1, "&4&lExit", new ArrayList<>());

            gui.setButton(18, new Button(back.getItem(), () -> {
                GUI.close(gui);

                if (getType().equalsIgnoreCase("Punishments")) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            PunishmentHistoryGUI punishmentHistoryGUI = new PunishmentHistoryGUI(getGui().getPlayer(), 36, "&aSelect punishment type.");
                            punishmentHistoryGUI.setup(getGui().getPlayer(), target);
                            GUI.open(punishmentHistoryGUI.getGui());
                        }
                    }.runTaskLater(plugin, 1);
                }
            }, back.getName(), back.getLore()));
            gui.setButton(26, new Button(exit.getItem(), () -> GUI.close(gui), exit.getName(), exit.getLore()));
        }
    }
}
