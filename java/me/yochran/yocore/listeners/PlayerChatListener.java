package me.yochran.yocore.listeners;

import me.yochran.yocore.chats.ChatType;
import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.punishments.Punishment;
import me.yochran.yocore.punishments.PunishmentType;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.server.Server;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

public class PlayerChatListener implements Listener {

    private final yoCore plugin = yoCore.getInstance();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        yoPlayer yoPlayer = new yoPlayer(event.getPlayer());

        if (ChatType.hasToggleOn(event.getPlayer())) {
            ChatType type = ChatType.getToggle(event.getPlayer());

            if (type != null) {
                event.setCancelled(true);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (ChatType.hasPermission(player, type))
                        ChatType.sendMessage(event.getPlayer(), player, type, event.getMessage());
                }

                return;
            }
        }

        String prefix = event.getMessage().split(" ")[0];
        ChatType type = ChatType.getChatFromPrefix(prefix);
        if (type != null) {
            event.setCancelled(true);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (ChatType.hasPermission(player, type)) {
                    if (prefix.equalsIgnoreCase("$"))
                        ChatType.sendMessage(event.getPlayer(), player, type, event.getMessage().replaceFirst("\\$ ", ""));
                    else
                        ChatType.sendMessage(event.getPlayer(), player, type, event.getMessage().replaceFirst(prefix + " ", ""));
                }
            }

            return;
        }

        if (plugin.muted_players.containsKey(event.getPlayer().getUniqueId())) {
            Punishment punishment = null;

            for (Map.Entry<Integer, Punishment> entry : Punishment.getPunishments(yoPlayer).entrySet()) {
                if (entry.getValue().getType() == PunishmentType.MUTE && entry.getValue().getStatus().equalsIgnoreCase("Active"))
                    punishment = entry.getValue();
            }

            if (punishment == null)
                return;

            if (plugin.muted_players.get(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Temporary.TargetAttemptToSpeak")
                        .replace("%reason%", punishment.getReason())
                        .replace("%expiration%", Utils.getExpirationDate((long) punishment.getDuration()))));
            } else {
                event.getPlayer().sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Permanent.TargetAttemptToSpeak")
                        .replace("%reason%", punishment.getReason())));

            }

            event.setCancelled(true);
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
                case "dark red":
                    message = "&4" + message;
                    break;
                case "light red":
                    message = "&c" + message;
                    break;
                case "orange":
                    message = "&6" + message;
                    break;
                case "yellow":
                    message = "&e" + message;
                    break;
                case "lime":
                    message = "&a" + message;
                    break;
                case "green":
                    message = "&2" + message;
                    break;
                case "aqua":
                    message = "&b" + message;
                    break;
                case "blue":
                    message = "&9" + message;
                    break;
                case "dark blue":
                    message = "&1" + message;
                    break;
                case "purple":
                    message = "&5" + message;
                    break;
                case "pink":
                    message = "&d" + message;
                    break;
                case "white":
                    message = "&r" + message;
                    break;
                case "bold":
                    message = "&l" + message;
                    break;
                case "italics":
                    message = "&o" + message;
                    break;
            }
        }

        String tag = "";
        if (plugin.tag.containsKey(event.getPlayer().getUniqueId()))
            tag = plugin.getConfig().getString("Tags." + plugin.tag.get(event.getPlayer().getUniqueId()) + ".Prefix");

        int playTime;
        try {
            playTime = (event.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 3600;
        } catch (NoSuchFieldError ignored) {
            playTime = -1;
        }

        Rank rank = yoPlayer.getRank();
        if (yoPlayer.isRankDisguised())
            rank = yoPlayer.getRankDisguise();

        String displayName = rank.getColor() + event.getPlayer().getName();
        if (yoPlayer.isNicked()) displayName = rank.getColor() + yoPlayer.getDisplayNickname();

        String format = plugin.getConfig().getString("ChatFormat")
                .replace("%player_prefix%", rank.getPrefix())
                .replace("%player_color%", rank.getColor())
                .replace("%player%", displayName)
                .replace("%message%", message)
                .replace("%player_playtime%", String.valueOf(playTime))
                .replace("%player_tag%", tag);

        event.setFormat(Utils.translate(format));

        Server server = Server.getServer(event.getPlayer());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.chat_toggled.contains(player.getUniqueId())) event.getRecipients().remove(player);
            if (server != Server.getServer(player) && plugin.getConfig().getBoolean("Servers.ChatSeparation")) event.getRecipients().remove(player);
        }
    }
}
