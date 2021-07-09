package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.CustomGUI;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.management.PermissionManagement;
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

public class GrantsGUI extends CustomGUI {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public GrantsGUI(Player player, int size, String title) {
        super(player, size, title);

        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup(Player player, OfflinePlayer target, int page) {
        String activePrefix = "&a&l(Active) ";
        String revokedPrefix = "&4&l(Revoked) ";
        String expiredPrefix = "&6&l(Expired) ";

        gui.setFiller(new int[] { 9,10,11,12,13,14,15,16,17 });

        Map<Integer, Button> buttons = new HashMap<>();
        Set<Integer> pages = new HashSet<>();

        int loop = -1;
        if (plugin.grantData.config.contains(target.getUniqueId().toString() + ".Grants")) {
            for (String grant : plugin.grantData.config.getConfigurationSection(target.getUniqueId().toString() + ".Grants").getKeys(false)) {
                loop++;

                String issuedGrant;
                if (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Type").equalsIgnoreCase("RANK")) {
                    if (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Grant").equalsIgnoreCase("(Removed Rank)"))
                        issuedGrant = "&4(Removed Rank)";
                    else issuedGrant = plugin.getConfig().getString("Ranks." + plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Grant").toUpperCase() + ".Display");
                } else issuedGrant  = plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Grant");
                String executor = playerManagement.getPlayerColor(Bukkit.getOfflinePlayer(UUID.fromString(plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Executor"))));
                String duration;
                if (plugin.grantData.config.get(target.getUniqueId().toString() + ".Grants." + grant + ".Duration").equals("Permanent")) duration = "Permanent";
                else duration = Utils.getExpirationDate(plugin.grantData.config.getLong(target.getUniqueId().toString() + ".Grants." + grant + ".Duration"));
                String reason = plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Reason");
                String previousRank = plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".PreviousRank");
                String previousRankDisplay;
                if (!previousRank.equalsIgnoreCase("N/A")) previousRankDisplay = plugin.getConfig().getString("Ranks." + previousRank.toUpperCase() + ".Display");
                else previousRankDisplay = "&cN/A (Permission Grant)";
                String ID = String.valueOf(plugin.grantData.config.getInt(target.getUniqueId().toString() + ".Grants." + grant + ".ID"));

                String revokePermission;
                if (previousRank.equalsIgnoreCase("N/A")) revokePermission = issuedGrant;
                else revokePermission = plugin.getConfig().getString("Ranks." + previousRank + ".GrantPermission");

                ItemBuilder itemBuilder = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&4&lNULL", ItemBuilder.formatLore(new String[] {
                        "&e&m----------------------------",
                        "&eTarget: &f" + playerManagement.getPlayerColor(target),
                        "&eType: &f" + plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Type"),
                        "&eDuration: &f" + duration,
                        "&e&m----------------------------",
                        "&eIssued Grant: &f" + issuedGrant,
                        "&eIssued By: &f" + executor,
                        "&eIssued Reason: &f" + reason,
                        "&ePrevious Rank: &f" + previousRankDisplay,
                        "&eGrant ID: &f" + ID,
                        "&e&m----------------------------"
                }));

                boolean revokable = false;

                switch (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Status").toLowerCase()) {
                    case "active":
                        revokable = true;
                        itemBuilder.setItem(XMaterial.LIME_WOOL.parseItem());
                        itemBuilder.setName(activePrefix + Utils.getExpirationDate(plugin.grantData.config.getLong(target.getUniqueId().toString() + ".Grants." + grant + ".Date")));
                        break;
                    case "revoked":
                        itemBuilder.setItem(XMaterial.RED_WOOL.parseItem());
                        itemBuilder.setName(revokedPrefix + Utils.getExpirationDate(plugin.grantData.config.getLong(target.getUniqueId().toString() + ".Grants." + grant + ".Date")));
                        break;
                    case "expired":
                        itemBuilder.setItem(XMaterial.ORANGE_WOOL.parseItem());
                        itemBuilder.setName(expiredPrefix + Utils.getExpirationDate(plugin.grantData.config.getLong(target.getUniqueId().toString() + ".Grants." + grant + ".Date")));
                        break;
                }

                if (revokable) {
                    if (player.hasPermission(revokePermission))
                        itemBuilder.getLore().add(Utils.translate("&aClick to revoke this grant."));
                    else itemBuilder.getLore().add(Utils.translate("&cYou cannot remove this grant."));
                }

                Button button = new Button(
                        itemBuilder.getItem(),
                        itemBuilder.getName(),
                        itemBuilder.getLore()
                );

                if (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + grant + ".Status").equalsIgnoreCase("Active")
                        && itemBuilder.getLore().contains(Utils.translate("&aClick to revoke this grant."))) {
                    button.setAction(() -> {
                        GUI.close(gui);

                        player.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.RevokedGrant")));

                        int id = plugin.grantData.config.getInt(target.getUniqueId().toString() + ".Grants." + grant + ".ID");

                        plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + id + ".Status", "Revoked");
                        plugin.grantData.saveData();

                        if (plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + id + ".Type").equalsIgnoreCase("RANK"))
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + target.getName() + " " + plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + id + ".PreviousRank"));
                        else
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "user " + target.getName() + " remove " + plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + id + ".Grant"));

                        if (target.isOnline())
                            new PermissionManagement().setupPlayer(Bukkit.getPlayer(target.getUniqueId()));
                    });
                }

                buttons.put(loop, button);
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
                            findPageGUI.setup("Grants", null, target, Collections.max(newPages));
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
                GrantsGUI grantsGUI = new GrantsGUI(player, 18, playerManagement.getPlayerColor(target) + "&a's grant history.");
                grantsGUI.setup(getGui().getPlayer(), target, newPage.get());
                GUI.open(grantsGUI.getGui());
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
