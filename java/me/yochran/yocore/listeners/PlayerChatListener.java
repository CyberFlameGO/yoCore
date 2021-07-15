package me.yochran.yocore.listeners;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.management.ServerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();
    private final ServerManagement serverManagement = new ServerManagement();

    public PlayerChatListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (plugin.bchat_toggle.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            buildChat(event.getPlayer(), event.getMessage());
            return;
        }
        if (plugin.schat_toggle.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            staffChat(event.getPlayer(), event.getMessage());
            return;
        }
        if (plugin.achat_toggle.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            adminChat(event.getPlayer(), event.getMessage());
            return;
        }
        if (plugin.mchat_toggle.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            managementChat(event.getPlayer(), event.getMessage());
            return;
        }

        if (event.getMessage().startsWith("$ ") && event.getPlayer().hasPermission("yocore.chats.build")) {
            event.setCancelled(true);
            buildChat(event.getPlayer(), event.getMessage().replaceFirst("\\$ ", ""));
            return;
        } else if (event.getMessage().startsWith("# ") && event.getPlayer().hasPermission("yocore.chats.staff")) {
            event.setCancelled(true);
            staffChat(event.getPlayer(), event.getMessage().replaceFirst("# ", ""));
            return;
        } else if (event.getMessage().startsWith("@ ") && event.getPlayer().hasPermission("yocore.chats.admin")) {
            event.setCancelled(true);
            adminChat(event.getPlayer(), event.getMessage().replaceFirst("@ ", ""));
            return;
        } else if (event.getMessage().startsWith("! ") && event.getPlayer().hasPermission("yocore.chats.management")) {
            event.setCancelled(true);
            managementChat(event.getPlayer(), event.getMessage().replaceFirst("! ", ""));
            return;
        }

        if (plugin.muted_players.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);

            if (plugin.muted_players.get(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Temporary.TargetAttemptToSpeak")
                        .replace("%reason%", plugin.punishmentData.config.getString(event.getPlayer().getUniqueId().toString() + ".Mute." + punishmentManagement.getInfractionAmount(event.getPlayer(), "Mute") + ".Reason"))
                        .replace("%expiration%", Utils.getExpirationDate(plugin.punishmentData.config.getLong(event.getPlayer().getUniqueId().toString() + ".Mute." + punishmentManagement.getInfractionAmount(event.getPlayer(), "Mute") + ".Duration")))));
            } else {
                event.getPlayer().sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Permanent.TargetAttemptToSpeak")
                        .replace("%reason%", plugin.punishmentData.config.getString(event.getPlayer().getUniqueId().toString() + ".Mute." + punishmentManagement.getInfractionAmount(event.getPlayer(), "Mute") + ".Reason"))));
            }
        }

        if (plugin.chat_muted && !event.getPlayer().hasPermission("yocore.mutechat.bypass")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Utils.translate(plugin.getConfig().getString("MuteChat.AttemptToSpeak")));
        }

        if (plugin.chat_toggled.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Utils.translate(plugin.getConfig().getString("Settings.GlobalChatAttempt")));
        }

        String message = event.getMessage().replaceAll("%", "%%");

        if (!event.getPlayer().hasPermission("yocore.chatcolor"))
            plugin.chat_color.remove(event.getPlayer().getUniqueId());

        if (!event.getPlayer().hasPermission("yocore.chatcolor.bypass"))
            message = message.replace("&", "");

        if (plugin.chat_color.containsKey(event.getPlayer().getUniqueId()) && event.getPlayer().hasPermission("yocore.chatcolor")) {
            switch (ChatColor.stripColor(plugin.chat_color.get(event.getPlayer().getUniqueId())).toLowerCase()) {
                case "dark red": message = "&4" + message; break;
                case "light red": message = "&c" + message; break;
                case "orange": message = "&6" + message; break;
                case "yellow": message = "&e" + message; break;
                case "lime": message = "&a" + message; break;
                case "green": message = "&2" + message; break;
                case "aqua": message = "&b" + message; break;
                case "blue": message = "&9" + message; break;
                case "dark blue": message = "&1" + message; break;
                case "purple": message = "&5" + message; break;
                case "pink": message = "&d" + message; break;
                case "white": message = "&r" + message; break;
                case "bold": message = "&l" + message; break;
                case "italics": message = "&o" + message; break;
            }
        }

        String tag = "";
        if (plugin.tag.containsKey(event.getPlayer().getUniqueId()))
            tag = plugin.getConfig().getString("Tags." + plugin.tag.get(event.getPlayer().getUniqueId()) + ".Prefix");

        int playTime;
        try {
            playTime = (event.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 3600;
        } catch (NoSuchFieldError ignored) { playTime = -1; }

        String format = plugin.getConfig().getString("ChatFormat")
                .replace("%player_prefix%", playerManagement.getPlayerPrefix(event.getPlayer()))
                .replace("%player_color%", playerManagement.getPlayerColor(event.getPlayer()))
                .replace("%message%", message)
                .replace("%player_playtime%", String.valueOf(playTime))
                .replace("%player_tag%", tag);

        event.setFormat(Utils.translate(format));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.chat_toggled.contains(player.getUniqueId()))
                event.getRecipients().remove(player);
            if (!serverManagement.getServer(event.getPlayer()).equalsIgnoreCase(serverManagement.getServer(player)) && plugin.getConfig().getBoolean("Servers.ChatSeparation"))
                event.getRecipients().remove(player);
        }
    }

    public void buildChat(Player player, String message) {
        for (Player builders : Bukkit.getOnlinePlayers()) {
            if (builders.hasPermission("yocore.chats.build")) {
                builders.sendMessage(Utils.translate(plugin.getConfig().getString("BuildChat.Format")
                        .replace("%player%", playerManagement.getPlayerColor(player))
                        .replace("%message%", message)
                        .replace("%server%", serverManagement.getName(serverManagement.getServer(player)))
                        .replace("%world%", player.getWorld().getName())));
            }
        }
    }

    public void staffChat(Player player, String message) {
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("yocore.chats.staff")) {
                staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffChat.Format")
                        .replace("%player%", playerManagement.getPlayerColor(player))
                        .replace("%message%", message)
                        .replace("%server%", serverManagement.getName(serverManagement.getServer(player)))
                        .replace("%world%", player.getWorld().getName())));
            }
        }
    }

    public void adminChat(Player player, String message) {
        for (Player admins : Bukkit.getOnlinePlayers()) {
            if (admins.hasPermission("yocore.chats.admin")) {
                admins.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.Format")
                        .replace("%player%", playerManagement.getPlayerColor(player))
                        .replace("%message%", message)
                        .replace("%server%", serverManagement.getName(serverManagement.getServer(player)))
                        .replace("%world%", player.getWorld().getName())));
            }
        }
    }

    public void managementChat(Player player, String message) {
        for (Player managers : Bukkit.getOnlinePlayers()) {
            if (managers.hasPermission("yocore.chats.management")) {
                managers.sendMessage(Utils.translate(plugin.getConfig().getString("ManagementChat.Format")
                        .replace("%player%", playerManagement.getPlayerColor(player))
                        .replace("%message%", message)
                        .replace("%server%", serverManagement.getName(serverManagement.getServer(player)))
                        .replace("%world%", player.getWorld().getName())));
            }
        }
    }
}
