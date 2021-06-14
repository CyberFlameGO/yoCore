package me.yochran.yocore.commands.punishments;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
        Inventory inventory = Bukkit.createInventory(player, 36, Utils.translate("&aSelect Punishment Type."));

        ItemStack warn = XMaterial.YELLOW_WOOL.parseItem();
        ItemMeta warnMeta = warn.getItemMeta();
        warnMeta.setDisplayName(Utils.translate(playerManagement.getPlayerColor(target) + "&e's warns."));
        warn.setItemMeta(warnMeta);

        inventory.setItem(10, warn);

        for (int i = 1; i < plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".WarnsAmount"); i++) {
            if (i <= 64) {
                inventory.addItem(warn);
            }
        }

        ItemStack mute = XMaterial.ORANGE_WOOL.parseItem();
        ItemMeta muteMeta = mute.getItemMeta();
        muteMeta.setDisplayName(Utils.translate(playerManagement.getPlayerColor(target) + "&6's mutes."));
        mute.setItemMeta(muteMeta);

        inventory.setItem(12, mute);

        for (int i = 1; i < plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".MutesAmount"); i++) {
            if (i <= 64) {
                inventory.addItem(mute);
            }
        }

        ItemStack kick = XMaterial.RED_WOOL.parseItem();
        ItemMeta kickMeta = kick.getItemMeta();
        kickMeta.setDisplayName(Utils.translate(playerManagement.getPlayerColor(target) + "&c's kicks."));
        kick.setItemMeta(kickMeta);

        inventory.setItem(14, kick);

        for (int i = 1; i < plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".KicksAmount"); i++) {
            if (i <= 64) {
                inventory.addItem(kick);
            }
        }

        ItemStack ban = XMaterial.RED_WOOL.parseItem();
        ItemMeta banMeta = ban.getItemMeta();
        banMeta.setDisplayName(Utils.translate(playerManagement.getPlayerColor(target) + "&c's bans."));
        ban.setItemMeta(banMeta);

        inventory.setItem(16, ban);

        for (int i = 1; i < plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".BansAmount"); i++) {
            if (i <= 64) {
                inventory.addItem(ban);
            }
        }

        ItemStack blacklist = XMaterial.BEDROCK.parseItem();
        ItemMeta blacklistMeta = blacklist.getItemMeta();
        blacklistMeta.setDisplayName(Utils.translate(playerManagement.getPlayerColor(target) + "&4's blacklists."));
        blacklist.setItemMeta(blacklistMeta);

        inventory.setItem(22, blacklist);

        for (int i = 1; i < plugin.punishmentData.config.getInt(target.getUniqueId().toString() + ".BlacklistsAmount"); i++) {
            if (i <= 64) {
                inventory.addItem(blacklist);
            }
        }

        player.openInventory(inventory);
    }

    public void openPunishmentType(String type, Player player, OfflinePlayer target) {
        Inventory inventory = Bukkit.createInventory(player, 54, Utils.translate(playerManagement.getPlayerColor(target) + "&a's " + type + "&as"));

        String activePrefix = "&a&l(Active) ";
        String revokedPrefix = "&4&l(Revoked) ";
        String expiredPrefix = "&6&l(Expired) ";

        if (plugin.punishmentData.config.contains(target.getUniqueId().toString() + "." + type)) {
            for (String punishment : plugin.punishmentData.config.getConfigurationSection(target.getUniqueId().toString() + "." + type).getKeys(false)) {
                ItemStack item = XMaterial.BEDROCK.parseItem();
                ItemMeta itemMeta = item.getItemMeta();

                switch (plugin.punishmentData.config.getString(target.getUniqueId().toString() + "." + type + "." + punishment + ".Status").toLowerCase()) {
                    case "active":
                        item =XMaterial.LIME_WOOL.parseItem();
                        itemMeta.setDisplayName(Utils.translate(activePrefix + Utils.getExpirationDate(plugin.punishmentData.config.getLong(target.getUniqueId().toString() + "." + type + "." + punishment + ".Date"))));
                        break;
                    case "revoked":
                        item = XMaterial.RED_WOOL.parseItem();
                        itemMeta.setDisplayName(Utils.translate(revokedPrefix + Utils.getExpirationDate(plugin.punishmentData.config.getLong(target.getUniqueId().toString() + "." + type + "." + punishment + ".Date"))));
                        break;
                    case "expired":
                        item = XMaterial.ORANGE_WOOL.parseItem();
                        itemMeta.setDisplayName(Utils.translate(expiredPrefix + Utils.getExpirationDate(plugin.punishmentData.config.getLong(target.getUniqueId().toString() + "." + type + "." + punishment + ".Date"))));
                        break;
                }

                String executor = playerManagement.getPlayerColor(Bukkit.getOfflinePlayer(UUID.fromString(plugin.punishmentData.config.getString(target.getUniqueId().toString() + "." + type + "." + punishment + ".Executor"))));
                String duration;
                if (plugin.punishmentData.config.get(target.getUniqueId().toString() + "." + type + "." + punishment + ".Duration").equals("Permanent")) {
                    duration = "Permanent";
                } else {
                    duration = Utils.getExpirationDate(plugin.punishmentData.config.getLong(target.getUniqueId().toString() + "." + type + "." + punishment + ".Duration"));
                }
                String reason = plugin.punishmentData.config.getString(target.getUniqueId().toString() + "." + type + "." + punishment + ".Reason");
                String silent = String.valueOf(plugin.punishmentData.config.getBoolean(target.getUniqueId().toString() + "." + type + "." + punishment + ".Silent"));

                List<String> itemLore = new ArrayList<>();
                itemLore.add(Utils.translate("&7&m----------------------------"));
                itemLore.add(Utils.translate("&eExecutor: &f" + executor));
                itemLore.add(Utils.translate("&eReason: &f" + reason));
                itemLore.add(Utils.translate("&eDuration: &f" + duration));
                itemLore.add(Utils.translate("&eSilent: &f" + silent));
                itemLore.add(Utils.translate("&7&m----------------------------"));

                itemMeta.setLore(itemLore);
                item.setItemMeta(itemMeta);
                inventory.addItem(item);
            }
        }

        player.openInventory(inventory);
    }
}
