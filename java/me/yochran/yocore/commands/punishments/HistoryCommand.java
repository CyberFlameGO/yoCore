package me.yochran.yocore.commands.punishments;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HistoryCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

    public HistoryCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("History.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.history")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("History.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("History.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("History.InvalidPlayer")));
            return true;
        }

        openHistoryGUI((Player) sender, target);
        plugin.selected_history.put(((Player) sender).getUniqueId(), target.getUniqueId());

        return true;
    }

    public void openHistoryGUI(Player player, OfflinePlayer target) {
        GUI gui = new GUI(player, 36, "&aSelect Punishment Type.");

        gui.setFiller(36);

        gui.setButton(10, new Button(
                XMaterial.YELLOW_WOOL.parseItem(),
                plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".WarnsAmount"),
                () -> {
                    gui.close();
                    openPunishmentType("Warn", player, target);
                },
                playerManagement.getPlayerColor(target) + "&e's warns."
        ));
        gui.setButton(12, new Button(
                XMaterial.ORANGE_WOOL.parseItem(),
                plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".MutesAmount"),
                () -> {
                    gui.close();
                    openPunishmentType("Mute", player, target);
                },
                playerManagement.getPlayerColor(target) + "&6's mutes."
        ));
        gui.setButton(14, new Button(
                XMaterial.RED_WOOL.parseItem(),
                plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".KicksAmount"),
                () -> {
                    gui.close();
                    openPunishmentType("Kick", player, target);
                },
                playerManagement.getPlayerColor(target) + "&c's kicks."
        ));
        gui.setButton(16, new Button(
                XMaterial.RED_WOOL.parseItem(),
                plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".BansAmount"),
                () -> {
                    gui.close();
                    openPunishmentType("Ban", player, target);
                },
                playerManagement.getPlayerColor(target) + "&c's bans."
        ));
        gui.setButton(22, new Button(
                XMaterial.REDSTONE_BLOCK.parseItem(),
                plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".BlacklistsAmount"),
                () -> {
                    gui.close();
                    openPunishmentType("Blacklist", player, target);
                },
                playerManagement.getPlayerColor(target) + "&4's blacklists."
        ));

        gui.open();
    }

    public void openPunishmentType(String type, Player player, OfflinePlayer target) {
        GUI gui = new GUI(player, 54, playerManagement.getPlayerColor(target) + "&a's " + type + "&as");

        String activePrefix = "&a&l(Active) ";
        String revokedPrefix = "&4&l(Revoked) ";
        String expiredPrefix = "&6&l(Expired) ";

        if (plugin.punishmentData.config.contains(target.getUniqueId().toString() + "." + type)) {
            int loop = -1;
            for (String punishment : plugin.punishmentData.config.getConfigurationSection(target.getUniqueId().toString() + "." + type).getKeys(false)) {
                loop++;
                Button item = new Button(XMaterial.BEDROCK.parseItem(), "&4&lNULL", new ArrayList<>());

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

                switch (plugin.punishmentData.config.getString(target.getUniqueId().toString() + "." + type + "." + punishment + ".Status").toLowerCase()) {
                    case "active":
                        item.setItem(XMaterial.LIME_WOOL.parseItem());
                        item.setName(activePrefix + Utils.getExpirationDate(plugin.punishmentData.config.getLong(target.getUniqueId().toString() + "." + type + "." + punishment + ".Date")));
                        if (!type.equalsIgnoreCase("Warn") && player.hasPermission("yocore.un" + type.toLowerCase()))
                            item.setAction(() -> {
                                gui.close();
                                player.performCommand("un" + type.toLowerCase() + " " + target.getName() + " -s");
                            });
                        break;
                    case "revoked":
                        item.setItem(XMaterial.RED_WOOL.parseItem());
                        item.setName(revokedPrefix + Utils.getExpirationDate(plugin.punishmentData.config.getLong(target.getUniqueId().toString() + "." + type + "." + punishment + ".Date")));
                        break;
                    case "expired":
                        item.setItem(XMaterial.ORANGE_WOOL.parseItem());
                        item.setName(expiredPrefix + Utils.getExpirationDate(plugin.punishmentData.config.getLong(target.getUniqueId().toString() + "." + type + "." + punishment + ".Date")));
                        break;
                }

                List<String> itemLore = new ArrayList<>();
                itemLore.add(Utils.translate("&e&m----------------------------"));
                itemLore.add(Utils.translate("&eTarget: &f" + playerManagement.getPlayerColor(target)));
                itemLore.add(Utils.translate("&eDuration: &f" + duration));
                itemLore.add(Utils.translate("&e&m----------------------------"));
                itemLore.add(Utils.translate("&eIssued By: &f" + executor));
                itemLore.add(Utils.translate("&eIssued Reason: &f" + reason));
                itemLore.add(Utils.translate("&eIssued Silently: &f" + silent));
                itemLore.add(Utils.translate("&e&m----------------------------"));
                if (plugin.punishmentData.config.getString(target.getUniqueId().toString() + "." + type + "." + punishment + ".Status").equalsIgnoreCase("Active")
                        && player.hasPermission("yocore.un" + type.toLowerCase())
                        && !type.equalsIgnoreCase("Warn"))
                    itemLore.add(Utils.translate("&aClick to revoke this punishment."));

                item.setLore(itemLore);

                gui.setButton(loop, item);
            }
        }

        gui.open();
    }
}
