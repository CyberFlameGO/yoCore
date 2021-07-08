package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.CustomGUI;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PunishmentHistoryGUI extends CustomGUI {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public PunishmentHistoryGUI(Player player, int size, String title) {
        super(player, size, title);
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup(Player player, OfflinePlayer target) {
        gui.setFiller(36);

        gui.setButton(10, new Button(
                XMaterial.YELLOW_WOOL.parseItem(),
                plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".WarnsAmount"),
                () -> {
                    GUI.close(gui);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 18, playerManagement.getPlayerColor(target) + "&a's warns.");
                            detailedPunishmentHistoryGUI.setup("Warn", player, target, 1);
                            GUI.open(detailedPunishmentHistoryGUI.getGui());
                        }
                    }.runTaskLater(plugin, 1);
                },
                playerManagement.getPlayerColor(target) + "&e's warns."
        ));
        gui.setButton(12, new Button(
                XMaterial.ORANGE_WOOL.parseItem(),
                plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".MutesAmount"),
                () -> {
                    GUI.close(gui);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 18, playerManagement.getPlayerColor(target) + "&a's mutes.");
                            detailedPunishmentHistoryGUI.setup("Mute", player, target, 1);
                            GUI.open(detailedPunishmentHistoryGUI.getGui());
                        }
                    }.runTaskLater(plugin, 1);
                },
                playerManagement.getPlayerColor(target) + "&6's mutes."
        ));
        gui.setButton(14, new Button(
                XMaterial.RED_WOOL.parseItem(),
                plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".KicksAmount"),
                () -> {
                    GUI.close(gui);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 18, playerManagement.getPlayerColor(target) + "&a's kicks.");
                            detailedPunishmentHistoryGUI.setup("Kick", player, target, 1);
                            GUI.open(detailedPunishmentHistoryGUI.getGui());
                        }
                    }.runTaskLater(plugin, 1);
                },
                playerManagement.getPlayerColor(target) + "&c's kicks."
        ));
        gui.setButton(16, new Button(
                XMaterial.RED_WOOL.parseItem(),
                plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".BansAmount"),
                () -> {
                    GUI.close(gui);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 18, playerManagement.getPlayerColor(target) + "&a's bans.");
                            detailedPunishmentHistoryGUI.setup("Ban", player, target, 1);
                            GUI.open(detailedPunishmentHistoryGUI.getGui());
                        }
                    }.runTaskLater(plugin, 1);
                },
                playerManagement.getPlayerColor(target) + "&c's bans."
        ));
        gui.setButton(22, new Button(
                XMaterial.REDSTONE_BLOCK.parseItem(),
                plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".BlacklistsAmount"),
                () -> {
                    GUI.close(gui);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(player, 18, playerManagement.getPlayerColor(target) + "&a's blacklists.");
                            detailedPunishmentHistoryGUI.setup("Blacklist", player, target, 1);
                            GUI.open(detailedPunishmentHistoryGUI.getGui());
                        }
                    }.runTaskLater(plugin, 1);
                },
                playerManagement.getPlayerColor(target) + "&4's blacklists."
        ));
    }
}
