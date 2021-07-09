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

public class DetailedPunishmentHistoryGUI extends CustomGUI {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public DetailedPunishmentHistoryGUI(Player player, int size, String title) {
        super(player, size, title);
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup(String type, Player player, OfflinePlayer target, int page) {
        String activePrefix = "&a&l(Active) ";
        String revokedPrefix = "&4&l(Revoked) ";
        String expiredPrefix = "&6&l(Expired) ";

        gui.setFiller(new int[] { 9,10,11,12,13,14,15,16,17 });

        Map<Integer, Button> buttons = new HashMap<>();
        Set<Integer> pages = new HashSet<>();

        if (plugin.punishmentData.config.contains(target.getUniqueId().toString() + "." + type)) {
            int loop = -1;
            for (String punishment : plugin.punishmentData.config.getConfigurationSection(target.getUniqueId().toString() + "." + type).getKeys(false)) {
                loop++;
                String executor;
                if (plugin.punishmentData.config.getString(target.getUniqueId().toString() + "." + type + "." + punishment + ".Executor").equalsIgnoreCase("CONSOLE"))
                    executor = "&c&lConsole";
                else
                    executor = playerManagement.getPlayerColor(Bukkit.getOfflinePlayer(UUID.fromString(plugin.punishmentData.config.getString(target.getUniqueId().toString() + "." + type + "." + punishment + ".Executor"))));
                String duration;
                if (plugin.punishmentData.config.get(target.getUniqueId().toString() + "." + type + "." + punishment + ".Duration").equals("Permanent"))
                    duration = "Permanent";
                else
                    duration = Utils.getExpirationDate(plugin.punishmentData.config.getLong(target.getUniqueId().toString() + "." + type + "." + punishment + ".Duration"));
                String reason = plugin.punishmentData.config.getString(target.getUniqueId().toString() + "." + type + "." + punishment + ".Reason");
                String silent = String.valueOf(plugin.punishmentData.config.getBoolean(target.getUniqueId().toString() + "." + type + "." + punishment + ".Silent"));

                ItemBuilder itemBuilder = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&4&lNULL", ItemBuilder.formatLore(new String[]{
                        "&e&m----------------------------",
                        "&eTarget: &f" + playerManagement.getPlayerColor(target),
                        "&eDuration: &f" + duration,
                        "&e&m----------------------------",
                        "&eIssued By: &f" + executor,
                        "&eIssued Reason: &f" + reason,
                        "&eIssued Silently: &f" + silent,
                        "&e&m----------------------------"
                }));

                switch (plugin.punishmentData.config.getString(target.getUniqueId().toString() + "." + type + "." + punishment + ".Status").toLowerCase()) {
                    case "active":
                        itemBuilder.setItem(XMaterial.LIME_WOOL.parseItem());
                        itemBuilder.setName(activePrefix + Utils.getExpirationDate(plugin.punishmentData.config.getLong(target.getUniqueId().toString() + "." + type + "." + punishment + ".Date")));
                        break;
                    case "revoked":
                        itemBuilder.setItem(XMaterial.RED_WOOL.parseItem());
                        itemBuilder.setName(revokedPrefix + Utils.getExpirationDate(plugin.punishmentData.config.getLong(target.getUniqueId().toString() + "." + type + "." + punishment + ".Date")));
                        break;
                    case "expired":
                        itemBuilder.setItem(XMaterial.ORANGE_WOOL.parseItem());
                        itemBuilder.setName(expiredPrefix + Utils.getExpirationDate(plugin.punishmentData.config.getLong(target.getUniqueId().toString() + "." + type + "." + punishment + ".Date")));
                        break;
                }

                if (plugin.punishmentData.config.getString(target.getUniqueId().toString() + "." + type + "." + punishment + ".Status").equalsIgnoreCase("Active")
                        && player.hasPermission("yocore.un" + type.toLowerCase())
                        && !type.equalsIgnoreCase("Warn"))
                    itemBuilder.getLore().add(Utils.translate("&aClick to revoke this punishment."));

                Button button = new Button(
                        itemBuilder.getItem(),
                        () -> {
                            if (!type.equalsIgnoreCase("Warn") && player.hasPermission("yocore.un" + type.toLowerCase())) {
                                GUI.close(gui);
                                player.performCommand("un" + type.toLowerCase() + " " + target.getName() + " -s");
                            }
                        },
                        itemBuilder.getName(),
                        itemBuilder.getLore()
                );

                buttons.put(loop, button);
            }

            for (Map.Entry<Integer, Button> entry : buttons.entrySet())
                pages.add((entry.getKey() / 9) + 1);

            List<Integer> totalPages = new ArrayList<>(pages);
            setupToolbar(getGui(), page, totalPages, target, type);

            for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
                for (Map.Entry<Integer, Integer> info : Utils.getPageAndSlot(entry.getKey()).entrySet()) {
                    if (info.getKey() == page)
                        gui.setButton(info.getValue(), entry.getValue());
                }
            }
        }
    }

    public void setupToolbar(GUI gui, int page, List<Integer> newPages, OfflinePlayer target, String type) {
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
                            findPageGUI.setup("Punishments", type, target, Collections.max(newPages));
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
                DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 18, playerManagement.getPlayerColor(target) + "&a's " + type.toLowerCase() + "s.");
                detailedPunishmentHistoryGUI.setup(type, getGui().getPlayer(), target, newPage.get());
                GUI.open(detailedPunishmentHistoryGUI.getGui());
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
