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

        if (event.getMessage().startsWith("# ") && event.getPlayer().hasPermission("yocore.chats.staff")) {
            event.setCancelled(true);
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.chats.staff"))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffChat.Format")
                            .replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))
                            .replace("%message%", event.getMessage().replaceFirst("# ", ""))));
            }
        }

        if (event.getMessage().startsWith("@ ") && event.getPlayer().hasPermission("yocore.chats.admin")) {
            event.setCancelled(true);
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("yocore.chats.admin"))
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("AdminChat.Format")
                            .replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))
                            .replace("%message%", event.getMessage().replaceFirst("@ ", ""))));
            }
        }

        if (event.getMessage().startsWith("! ") && event.getPlayer().hasPermission("yocore.chats.management")) {
            event.setCancelled(true);
            for (Player staff : Bukkit.getOnlinePlayers())
                if (staff.hasPermission("yocore.chats.management")) {
                    staff.sendMessage(Utils.translate(plugin.getConfig().getString("ManagementChat.Format")
                            .replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))
                            .replace("%message%", event.getMessage().replaceFirst("! ", ""))));
            }
        }

        String format = plugin.getConfig().getString("ChatFormat")
                .replace("%player_prefix%", playerManagement.getPlayerPrefix(event.getPlayer()))
                .replace("%player_color%", playerManagement.getPlayerColor(event.getPlayer()))
                .replace("%message%", ChatColor.stripColor(event.getMessage()));

        event.setFormat(Utils.translate(format));
    }
}
