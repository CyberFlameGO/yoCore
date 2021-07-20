package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.CustomGUI;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.punishments.PunishmentType;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class PunishmentHistoryGUI extends CustomGUI {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

    public PunishmentHistoryGUI(Player player, int size, String title) {
        super(player, size, title);
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup(Player player, OfflinePlayer target) {
        gui.setFiller(36);

        ItemBuilder warns = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&c&lPlayer has no warns.", new ArrayList<>());
        ItemBuilder mutes = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&c&lPlayer has no mutes.", new ArrayList<>());
        ItemBuilder kicks = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&c&lPlayer has no kicks.", new ArrayList<>());
        ItemBuilder bans = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&c&lPlayer has no bans.", new ArrayList<>());
        ItemBuilder blacklists = new ItemBuilder(XMaterial.BEDROCK.parseItem(), 1, "&c&lPlayer has no blacklists.", new ArrayList<>());

        Button warnButton = new Button(warns.getItem(), warns.getName(), warns.getLore());
        Button muteButton = new Button(mutes.getItem(), mutes.getName(), mutes.getLore());
        Button kickButton = new Button(kicks.getItem(), kicks.getName(), kicks.getLore());
        Button banButton = new Button(bans.getItem(), bans.getName(), bans.getLore());
        Button blacklistButton = new Button(blacklists.getItem(), blacklists.getName(), blacklists.getLore());

        if (plugin.punishmentData.config.contains(target.getUniqueId().toString() + ".Warn")) {
            warnButton.setItem(XMaterial.YELLOW_WOOL.parseItem());
            warnButton.setName(playerManagement.getPlayerColor(target) + "&6's warns.");
            warnButton.setAmount(punishmentManagement.getAmount(target, PunishmentType.WARN));
            warnButton.setAction(() -> {
                GUI.close(gui);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 27, playerManagement.getPlayerColor(target) + "&a's warns.");
                        detailedPunishmentHistoryGUI.setup(PunishmentType.WARN, player, target, 1);
                        GUI.open(detailedPunishmentHistoryGUI.getGui());
                    }
                }.runTaskLater(plugin, 1);
            });
        }
        if (plugin.punishmentData.config.contains(target.getUniqueId().toString() + ".Mute")) {
            muteButton.setItem(XMaterial.ORANGE_WOOL.parseItem());
            muteButton.setName(playerManagement.getPlayerColor(target) + "&6's mutes.");
            muteButton.setAmount(punishmentManagement.getAmount(target, PunishmentType.MUTE));
            muteButton.setAction(() -> {
                GUI.close(gui);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 27, playerManagement.getPlayerColor(target) + "&a's mutes.");
                        detailedPunishmentHistoryGUI.setup(PunishmentType.MUTE, player, target, 1);
                        GUI.open(detailedPunishmentHistoryGUI.getGui());
                    }
                }.runTaskLater(plugin, 1);
            });
        }
        if (plugin.punishmentData.config.contains(target.getUniqueId().toString() + ".Kick")) {
            kickButton.setItem(XMaterial.RED_WOOL.parseItem());
            kickButton.setName(playerManagement.getPlayerColor(target) + "&6's kicks.");
            kickButton.setAmount(punishmentManagement.getAmount(target, PunishmentType.KICK));
            kickButton.setAction(() -> {
                GUI.close(gui);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 27, playerManagement.getPlayerColor(target) + "&a's kicks.");
                        detailedPunishmentHistoryGUI.setup(PunishmentType.KICK, player, target, 1);
                        GUI.open(detailedPunishmentHistoryGUI.getGui());
                    }
                }.runTaskLater(plugin, 1);
            });
        }
        if (plugin.punishmentData.config.contains(target.getUniqueId().toString() + ".Ban")) {
            banButton.setItem(XMaterial.RED_WOOL.parseItem());
            banButton.setName(playerManagement.getPlayerColor(target) + "&6's bans.");
            banButton.setAmount(punishmentManagement.getAmount(target, PunishmentType.BAN));
            banButton.setAction(() -> {
                GUI.close(gui);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 27, playerManagement.getPlayerColor(target) + "&a's bans.");
                        detailedPunishmentHistoryGUI.setup(PunishmentType.BAN, player, target, 1);
                        GUI.open(detailedPunishmentHistoryGUI.getGui());
                    }
                }.runTaskLater(plugin, 1);
            });
        }
        if (plugin.punishmentData.config.contains(target.getUniqueId().toString() + ".Blacklist")) {
            blacklistButton.setItem(XMaterial.REDSTONE_BLOCK.parseItem());
            blacklistButton.setName(playerManagement.getPlayerColor(target) + "&6's blacklists.");
            blacklistButton.setAmount(punishmentManagement.getAmount(target, PunishmentType.BLACKLIST));
            blacklistButton.setAction(() -> {
                GUI.close(gui);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 27, playerManagement.getPlayerColor(target) + "&a's blacklists.");
                        detailedPunishmentHistoryGUI.setup(PunishmentType.BLACKLIST, player, target, 1);
                        GUI.open(detailedPunishmentHistoryGUI.getGui());
                    }
                }.runTaskLater(plugin, 1);
            });
        }

        gui.setButton(10, warnButton);
        gui.setButton(12, muteButton);
        gui.setButton(14, kickButton);
        gui.setButton(16, banButton);
        gui.setButton(22, blacklistButton);
    }
}
