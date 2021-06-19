package me.yochran.yocore.listeners;

import me.yochran.yocore.commands.GrantCommand;
import me.yochran.yocore.commands.punishments.HistoryCommand;
import me.yochran.yocore.management.GrantManagement;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class GUIClickListener implements Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final GrantManagement grantManagement = new GrantManagement();
    private final GrantCommand grantCommand = new GrantCommand();
    private final HistoryCommand historyCommand = new HistoryCommand();

    public GUIClickListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == XMaterial.AIR.parseMaterial())
            return;

        if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aSelect Punishment Type."))) {
            event.setCancelled(true);
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.selected_history.get(event.getWhoClicked().getUniqueId()));

            if (event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("warns")) {
                event.getWhoClicked().closeInventory();
                historyCommand.openPunishmentType("Warn", (Player) event.getWhoClicked(), target);
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("mutes")) {
                event.getWhoClicked().closeInventory();
                historyCommand.openPunishmentType("Mute", (Player) event.getWhoClicked(), target);
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("kicks")) {
                event.getWhoClicked().closeInventory();
                historyCommand.openPunishmentType("Kick", (Player) event.getWhoClicked(), target);
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("bans")) {
                event.getWhoClicked().closeInventory();
                historyCommand.openPunishmentType("Ban", (Player) event.getWhoClicked(), target);
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("blacklists")) {
                event.getWhoClicked().closeInventory();
                historyCommand.openPunishmentType("Blacklist", (Player) event.getWhoClicked(), target);
            }
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aSelect a rank."))) {
            if (event.getWhoClicked().hasPermission(plugin.getConfig().getString("Ranks." + ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().toUpperCase()) + ".GrantPermission"))) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.grant_player.get(event.getWhoClicked().getUniqueId()));
                plugin.grant_rank.put(event.getWhoClicked().getUniqueId(), ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

                event.getWhoClicked().closeInventory();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        grantCommand.openDurationGUI((Player) event.getWhoClicked(), target);
                    }
                }.runTaskLater(plugin, 5);
            } else {
                event.setCancelled(true);
            }
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aSelect a duration."))) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.grant_player.get(event.getWhoClicked().getUniqueId()));
            plugin.grant_duration.put(event.getWhoClicked().getUniqueId(), ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

            event.getWhoClicked().closeInventory();
            new BukkitRunnable() {
                @Override
                public void run() {
                    grantCommand.openReasonGUI((Player) event.getWhoClicked(), target);
                }
            }.runTaskLater(plugin, 5);
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aSelect a reason."))) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.grant_player.get(event.getWhoClicked().getUniqueId()));

            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate(plugin.getConfig().getString("Grant.Reason.Items.Custom.Name")))) {
                event.getWhoClicked().closeInventory();
                plugin.grant_custom_reason.add(event.getWhoClicked().getUniqueId());
                event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("Grant.Reason.CustomReasonChatMessage")));
                return;
            }

            plugin.grant_reason.put(event.getWhoClicked().getUniqueId(), ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

            event.getWhoClicked().closeInventory();
            new BukkitRunnable() {
                @Override
                public void run() {
                    grantCommand.openConfirmGUI((Player) event.getWhoClicked(), target, plugin.grant_rank.get(event.getWhoClicked().getUniqueId()), plugin.grant_duration.get(event.getWhoClicked().getUniqueId()), plugin.grant_reason.get(event.getWhoClicked().getUniqueId()));
                }
            }.runTaskLater(plugin, 5);
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aConfirm the grant."))) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&2&lConfirm Grant"))) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.grant_player.get(event.getWhoClicked().getUniqueId()));
                String previousRank = plugin.playerData.config.getString(target.getUniqueId().toString() + ".Rank");

                event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("Grant.Confirm.ConfirmedGrant")
                        .replace("%target%", playerManagement.getPlayerColor(target))
                        .replace("%rank%", plugin.grant_rank.get(event.getWhoClicked().getUniqueId()))
                        .replace("%duration%", plugin.grant_duration.get(event.getWhoClicked().getUniqueId()))
                        .replace("%reason%", plugin.grant_reason.get(event.getWhoClicked().getUniqueId()))));

                if (plugin.grant_duration.get(event.getWhoClicked().getUniqueId()).equalsIgnoreCase("Permanent")) {
                    grantManagement.addGrant(target, event.getWhoClicked().getUniqueId().toString(), plugin.grant_rank.get(event.getWhoClicked().getUniqueId()), "Permanent", System.currentTimeMillis(), plugin.grant_reason.get(event.getWhoClicked().getUniqueId()), previousRank);
                } else {
                    grantManagement.addGrant(target, event.getWhoClicked().getUniqueId().toString(), plugin.grant_rank.get(event.getWhoClicked().getUniqueId()), grantManagement.getGrantDuration(plugin.grant_duration.get(event.getWhoClicked().getUniqueId())), System.currentTimeMillis(), plugin.grant_reason.get(event.getWhoClicked().getUniqueId()), previousRank);
                }

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + target.getName() + " " + plugin.grant_rank.get(event.getWhoClicked().getUniqueId()));

                plugin.grant_player.remove(event.getWhoClicked().getUniqueId());
                plugin.grant_rank.remove(event.getWhoClicked().getUniqueId());
                plugin.grant_reason.remove(event.getWhoClicked().getUniqueId());
                plugin.grant_duration.remove(event.getWhoClicked().getUniqueId());

                event.getWhoClicked().closeInventory();

            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', "&c&lCancel Grant"))) {
                event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("Grant.CancelledGrant")));
                event.getWhoClicked().closeInventory();

                plugin.grant_player.remove(event.getWhoClicked().getUniqueId());
                plugin.grant_rank.remove(event.getWhoClicked().getUniqueId());
                plugin.grant_reason.remove(event.getWhoClicked().getUniqueId());
                plugin.grant_duration.remove(event.getWhoClicked().getUniqueId());
            }
        } else if (event.getCurrentItem().getItemMeta().hasLore() && event.getCurrentItem().getItemMeta().getLore().contains(Utils.translate("&aClick to revoke this grant."))) {
            List<String> itemLore = event.getCurrentItem().getItemMeta().getLore();

            OfflinePlayer target = Bukkit.getOfflinePlayer(ChatColor.stripColor(event.getView().getTitle().replace("'s grant history.", "")));

            String idLore = ChatColor.stripColor(itemLore.get(8));
            int id = Integer.parseInt(idLore.replace("Grant ID: ", ""));

            plugin.grantData.config.set(target.getUniqueId().toString() + ".Grants." + id + ".Status", "Revoked");
            plugin.grantData.saveData();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + target.getName() + " " + plugin.grantData.config.getString(target.getUniqueId().toString() + ".Grants." + id + ".PreviousRank"));

            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("Grant.RevokedGrant")));
        } else if (event.getCurrentItem().getItemMeta().hasLore()
                && event.getCurrentItem().getItemMeta().getLore().contains(Utils.translate("&e&m----------------------------"))
                && event.getInventory().getSize() == 54) {
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aSelect a chat color."))) {
            plugin.chat_color.remove(event.getWhoClicked().getUniqueId());

            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("ChatColor.SelectedColor")
                    .replace("%color%", event.getCurrentItem().getItemMeta().getDisplayName())));

            plugin.chat_color.put(event.getWhoClicked().getUniqueId(), ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aPlayer settings."))) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains(Utils.translate("&bPrivate Messages:"))) {
                Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).performCommand("tpm");
                event.getWhoClicked().closeInventory();
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&bToggle Scoreboard"))) {
                Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).performCommand("tsb");
                event.getWhoClicked().closeInventory();
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains(Utils.translate("&bMessage Sounds:"))) {
                if (!plugin.message_sounds_toggled.contains(event.getWhoClicked().getUniqueId())) {
                    event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("Settings.MessageSoundsOff")));
                    plugin.message_sounds_toggled.add(event.getWhoClicked().getUniqueId());
                } else {
                    event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("Settings.MessageSoundsOn")));
                    plugin.message_sounds_toggled.remove(event.getWhoClicked().getUniqueId());
                }
                event.getWhoClicked().closeInventory();
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains(Utils.translate("&bGlobal Chat:"))) {
                if (!plugin.chat_toggled.contains(event.getWhoClicked().getUniqueId())) {
                    event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("Settings.GlobalChatOff")));
                    plugin.chat_toggled.add(event.getWhoClicked().getUniqueId());
                } else {
                    event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("Settings.GlobalChatOn")));
                    plugin.chat_toggled.remove(event.getWhoClicked().getUniqueId());
                }
                event.getWhoClicked().closeInventory();
            }
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aEnder Chest."))) {
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aInventory Inspect")))
            event.setCancelled(true);
    }
}
