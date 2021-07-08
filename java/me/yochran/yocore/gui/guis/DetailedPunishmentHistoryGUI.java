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
                else executor = playerManagement.getPlayerColor(Bukkit.getOfflinePlayer(UUID.fromString(plugin.punishmentData.config.getString(target.getUniqueId().toString() + "." + type + "." + punishment + ".Executor"))));
                String duration;
                if (plugin.punishmentData.config.get(target.getUniqueId().toString() + "." + type + "." + punishment + ".Duration").equals("Permanent"))
                    duration = "Permanent";
                else duration = Utils.getExpirationDate(plugin.punishmentData.config.getLong(target.getUniqueId().toString() + "." + type + "." + punishment + ".Duration"));
                String reason = plugin.punishmentData.config.getString(target.getUniqueId().toString() + "." + type + "." + punishment + ".Reason");
                String silent = String.valueOf(plugin.punishmentData.config.getBoolean(target.getUniqueId().toString() + "." + type + "." + punishment + ".Silent"));

                ItemBuilder itemBuilder = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&4&lNULL", ItemBuilder.formatLore(new String[] {
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
                                    DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 18,
                                            playerManagement.getPlayerColor(target) + "&a's " + type.toLowerCase() + "s.");
                                    detailedPunishmentHistoryGUI.setup(type, player, target, newPage.get());
                                    GUI.open(detailedPunishmentHistoryGUI.getGui());
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
                                    DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 18,
                                            playerManagement.getPlayerColor(target) + "&a's " + type.toLowerCase() + "s.");
                                    detailedPunishmentHistoryGUI.setup(type, player, target, newPage.get());
                                    GUI.open(detailedPunishmentHistoryGUI.getGui());
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
