package me.yochran.yocore.listeners;

import me.yochran.yocore.management.*;
import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.punishments.Punishment;
import me.yochran.yocore.punishments.PunishmentType;
import me.yochran.yocore.server.Server;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerLogListener implements Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();
    private final GrantManagement grantManagement = new GrantManagement();
    private final EconomyManagement economyManagement = new EconomyManagement();
    private final StatsManagement statsManagement = new StatsManagement();
    private final PermissionManagement permissionManagement = new PermissionManagement();

    public PlayerLogListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");

        yoPlayer yoPlayer = new yoPlayer(event.getPlayer());

        if (!plugin.playerData.config.contains(event.getPlayer().getUniqueId().toString())) playerManagement.setupPlayer(event.getPlayer());
        if (!plugin.punishmentData.config.contains(event.getPlayer().getUniqueId().toString())) punishmentManagement.setupPlayer(event.getPlayer());
        if (!plugin.grantData.config.contains(event.getPlayer().getUniqueId().toString())) grantManagement.setupPlayer(event.getPlayer());
        if (!economyManagement.isInitialized(event.getPlayer())) economyManagement.setupPlayer(event.getPlayer());
        if (!statsManagement.isInitialized(event.getPlayer())) statsManagement.setupPlayer(event.getPlayer());

        permissionManagement.setupPlayer(event.getPlayer());

        if (plugin.playerData.config.contains(event.getPlayer().getUniqueId().toString())
                && !plugin.playerData.config.getString(event.getPlayer().getUniqueId().toString() + ".Name").equalsIgnoreCase(event.getPlayer().getName())) {
            plugin.playerData.config.set(event.getPlayer().getUniqueId().toString() + ".Name", event.getPlayer().getName());
            plugin.playerData.saveData();
        }

        if (plugin.punishmentData.config.contains(event.getPlayer().getUniqueId().toString())
                && !plugin.punishmentData.config.getString(event.getPlayer().getUniqueId().toString() + ".Name").equalsIgnoreCase(event.getPlayer().getName())) {
            plugin.punishmentData.config.set(event.getPlayer().getUniqueId().toString() + ".Name", event.getPlayer().getName());
            plugin.punishmentData.saveData();
        }

        yoPlayer.setIP(event.getPlayer().getAddress().getAddress().getHostAddress());

        if (!yoPlayer.getAllIPs().contains(event.getPlayer().getAddress().getAddress().getHostAddress())) {
            List<String> ips = new ArrayList<>(yoPlayer.getAllIPs());
            ips.add(event.getPlayer().getAddress().getAddress().getHostAddress());

            yoPlayer.setAllIPs(ips);
        }

        if (plugin.blacklisted_players.containsKey(event.getPlayer().getUniqueId())) {
            for (Map.Entry<Integer, Punishment> entry : Punishment.getPunishments(yoPlayer).entrySet()) {
                if (entry.getValue().getType() == PunishmentType.BLACKLIST && entry.getValue().getStatus().equalsIgnoreCase("Active"))
                    event.getPlayer().kickPlayer(Utils.translate(plugin.getConfig().getString("Blacklist.TargetJoinScreen")
                            .replace("%reason%", entry.getValue().getReason())));
            }

            return;
        }

        if (plugin.banned_players.containsKey(event.getPlayer().getUniqueId())) {
            Punishment punishment = null;

            for (Map.Entry<Integer, Punishment> entry : Punishment.getPunishments(yoPlayer).entrySet()) {
                if (entry.getValue().getType() == PunishmentType.BAN && entry.getValue().getStatus().equalsIgnoreCase("Active"))
                    punishment = entry.getValue();
            }

            if (punishment == null)
                return;

            if (plugin.banned_players.get(event.getPlayer().getUniqueId())) {
                event.getPlayer().kickPlayer(Utils.translate(plugin.getConfig().getString("Ban.Temporary.TargetJoinScreen")
                        .replace("%reason%", punishment.getReason())
                        .replace("%expiration%", Utils.getExpirationDate((long) punishment.getDuration()))));
            } else {
                event.getPlayer().kickPlayer(Utils.translate(plugin.getConfig().getString("Ban.Permanent.TargetJoinScreen")
                        .replace("%reason%", punishment.getReason())));
            }

            return;
        }

        if (plugin.vanish_logged.contains(event.getPlayer().getUniqueId())) {
            plugin.vanish_logged.remove(event.getPlayer().getUniqueId());
            plugin.vanished_players.add(event.getPlayer().getUniqueId());

            for (Player players : Bukkit.getOnlinePlayers())
                event.getPlayer().hidePlayer(players);
        }

        Server server = Server.getServer(event.getPlayer());

        if (plugin.getConfig().getBoolean("Servers.Hub.HubEveryJoin")
                && !server.getName().equalsIgnoreCase(plugin.getConfig().getString("Servers.Hub.Server").toUpperCase())) {
            playerManagement.sendToSpawn(Server.getServer(plugin.getConfig().getString("Servers.Hub.Server")), event.getPlayer());

            if (plugin.getConfig().getBoolean("Servers.WorldSeparation")) {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (!server.getName().equalsIgnoreCase(Server.getServer(players).getName()))
                        players.hidePlayer(event.getPlayer());
                }
            }
        }

        if (plugin.getConfig().getBoolean("JoinMessage.Staff.Enabled")) {
            if (event.getPlayer().hasPermission("yocore.chats.staff")) {
                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.chats.staff"))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("JoinMessage.Staff.Message")
                                .replace("%player%", yoPlayer.getDisplayName())
                                .replace("%server%", server.getName())
                                .replace("%world%", event.getPlayer().getWorld().getName())));
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");

        yoPlayer yoPlayer = new yoPlayer(event.getPlayer());

        if (!plugin.playerData.config.contains(event.getPlayer().getUniqueId().toString()))
            playerManagement.setupPlayer(event.getPlayer());

        Server server = Server.getServer(event.getPlayer());

        if (plugin.last_location.get(event.getPlayer().getUniqueId()) == null) {
            Map<Server, Location> location = new HashMap<>();
            location.put(server, event.getPlayer().getLocation());
            plugin.last_location.put(event.getPlayer().getUniqueId(), location);
        }

        plugin.last_location.get(event.getPlayer().getUniqueId()).put(server, event.getPlayer().getLocation());

        if (plugin.getConfig().getBoolean("Servers.Hub.HubEveryJoin"))
            playerManagement.sendToSpawn(Server.getServer(plugin.getConfig().getString("Servers.Hub.Server")), event.getPlayer());

        if (plugin.getConfig().getBoolean("QuitMessage.Staff.Enabled")) {
            if (event.getPlayer().hasPermission("yocore.chats.staff")) {
                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.chats.staff")) {
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("QuitMessage.Staff.Message")
                                .replace("%player%", yoPlayer.getDisplayName())
                                .replace("%server%", server.getName())
                                .replace("%world%", event.getPlayer().getWorld().getName())));
                    }
                }
            }
        }

        plugin.player_permissions.remove(event.getPlayer().getUniqueId());

        if (plugin.vanished_players.contains(event.getPlayer().getUniqueId())) {
            plugin.vanished_players.remove(event.getPlayer().getUniqueId());
            plugin.vanish_logged.add(event.getPlayer().getUniqueId());
        }

        if (plugin.blacklisted_players.containsKey(event.getPlayer().getUniqueId())
                || plugin.banned_players.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
    }
}
