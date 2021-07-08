package me.yochran.yocore.listeners;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private yoCore plugin;
    private PlayerManagement playerManagement = new PlayerManagement();
    private PunishmentManagement punishmentManagement = new PunishmentManagement();

    public PlayerChatListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().startsWith("# ") && event.getPlayer().hasPermission("yocore.chats.staff")) {
            event.setCancelled(true);
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.chats.staff"))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffChat.Format")
                            .replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))
                            .replace("%message%", event.getMessage().replaceFirst("# ", ""))
                            .replace("%server%", plugin.getConfig().getString("ServerName"))
                            .replace("%world%", event.getPlayer().getWorld().getName())));
            }
            return;
        }

        if (event.getMessage().startsWith("@ ") && event.getPlayer().hasPermission("yocore.chats.admin")) {
            event.setCancelled(true);
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.chats.admin"))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.Format")
                            .replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))
                            .replace("%message%", event.getMessage().replaceFirst("@ ", ""))
                            .replace("%server%", plugin.getConfig().getString("ServerName"))
                            .replace("%world%", event.getPlayer().getWorld().getName())));
            }
            return;
        }

        if (event.getMessage().startsWith("! ") && event.getPlayer().hasPermission("yocore.chats.management")) {
            event.setCancelled(true);
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.chats.management"))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("ManagementChat.Format")
                            .replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))
                            .replace("%message%", event.getMessage().replaceFirst("! ", ""))
                            .replace("%server%", plugin.getConfig().getString("ServerName"))
                            .replace("%world%", event.getPlayer().getWorld().getName())));
            }
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

        if (plugin.chat_color.containsKey(event.getPlayer().getUniqueId())) {
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

        if (!event.getPlayer().hasPermission("yocore.chatcolor.bypass") && !event.getPlayer().hasPermission("yocore.chatcolor")) {
            plugin.chat_color.remove(event.getPlayer().getUniqueId());
            message = message.replace("&", "");
        }

        String tag = "";
        if (plugin.tag.containsKey(event.getPlayer().getUniqueId()))
            tag = plugin.getConfig().getString("Tags." + plugin.tag.get(event.getPlayer().getUniqueId()) + ".Prefix");

        String format = plugin.getConfig().getString("ChatFormat")
                    .replace("%player_prefix%", playerManagement.getPlayerPrefix(event.getPlayer()))
                    .replace("%player_color%", playerManagement.getPlayerColor(event.getPlayer()))
                    .replace("%message%", message)
                    .replace("%player_tag%", tag);

        event.setFormat(Utils.translate(format));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.chat_toggled.contains(player.getUniqueId()))
                event.getRecipients().remove(player);
            if (event.getPlayer().getWorld() != player.getWorld() && plugin.getConfig().getBoolean("Servers.ChatSeparation"))
                event.getRecipients().remove(player);
        }
    }
}
