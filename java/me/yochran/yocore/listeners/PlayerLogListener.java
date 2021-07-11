package me.yochran.yocore.listeners;

import me.yochran.yocore.management.*;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PlayerLogListener implements Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();
    private final GrantManagement grantManagement = new GrantManagement();
    private final EconomyManagement economyManagement = new EconomyManagement();
    private final StatsManagement statsManagement = new StatsManagement();
    private final PermissionManagement permissionManagement = new PermissionManagement();
    private final ServerManagement serverManagement = new ServerManagement();

    public PlayerLogListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.playerData.config.contains(event.getPlayer().getUniqueId().toString())) playerManagement.setupPlayer(event.getPlayer());
        if (!plugin.punishmentData.config.contains(event.getPlayer().getUniqueId().toString())) punishmentManagement.setupPlayer(event.getPlayer());
        if (!plugin.grantData.config.contains(event.getPlayer().getUniqueId().toString())) grantManagement.setupPlayer(event.getPlayer());
        if (!economyManagement.isInitialized(event.getPlayer())) economyManagement.setupPlayer(event.getPlayer());
        if (!statsManagement.isInitialized(event.getPlayer())) statsManagement.setupPlayer(event.getPlayer());

        permissionManagement.setupPlayer(event.getPlayer());

        if (plugin.playerData.config.contains(event.getPlayer().getUniqueId().toString()) && !plugin.playerData.config.getString(event.getPlayer().getUniqueId().toString() + ".Name").equalsIgnoreCase(event.getPlayer().getName())) {
            plugin.playerData.config.set(event.getPlayer().getUniqueId().toString() + ".Name", event.getPlayer().getName());
            plugin.playerData.saveData();
        }

        if (plugin.punishmentData.config.contains(event.getPlayer().getUniqueId().toString()) && !plugin.punishmentData.config.getString(event.getPlayer().getUniqueId().toString() + ".Name").equalsIgnoreCase(event.getPlayer().getName())) {
            plugin.punishmentData.config.set(event.getPlayer().getUniqueId().toString() + ".Name", event.getPlayer().getName());
            plugin.punishmentData.saveData();
        }

        if (!playerManagement.checkIP(event.getPlayer(), event.getPlayer().getAddress().getAddress().getHostAddress())) {
            plugin.playerData.config.set(event.getPlayer().getUniqueId().toString() + ".IP", event.getPlayer().getAddress().getAddress().getHostAddress());
            plugin.playerData.saveData();
        }

        if (!plugin.playerData.config.getStringList(event.getPlayer().getUniqueId().toString() + ".TotalIPs").contains(event.getPlayer().getAddress().getAddress().getHostAddress())) {
            List<String> ips = new ArrayList<>(plugin.playerData.config.getStringList(event.getPlayer().getUniqueId().toString() + ".TotalIPs"));
            ips.add(event.getPlayer().getAddress().getAddress().getHostAddress());

            plugin.playerData.config.set(event.getPlayer().getUniqueId().toString() + ".TotalIPs", ips);
            plugin.playerData.saveData();
        }

        if (plugin.blacklisted_players.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().kickPlayer(Utils.translate(plugin.getConfig().getString("Blacklist.TargetJoinScreen")
                    .replace("%reason%", plugin.blacklisted_players.get(event.getPlayer().getUniqueId()))));
            event.setJoinMessage("");
            return;
        }

        if (plugin.banned_players.containsKey(event.getPlayer().getUniqueId())) {
            if (plugin.banned_players.get(event.getPlayer().getUniqueId()))
                event.getPlayer().kickPlayer(Utils.translate(plugin.getConfig().getString("Ban.Temporary.TargetJoinScreen")
                        .replace("%reason%", plugin.punishmentData.config.getString(event.getPlayer().getUniqueId().toString() + ".Ban." + punishmentManagement.getInfractionAmount(event.getPlayer(), "Ban") + ".Reason"))
                        .replace("%expiration%", Utils.getExpirationDate(plugin.punishmentData.config.getLong(event.getPlayer().getUniqueId().toString() + ".Ban." + punishmentManagement.getInfractionAmount(event.getPlayer(), "Ban") + ".Duration")))));
            else
                event.getPlayer().kickPlayer(Utils.translate(plugin.getConfig().getString("Ban.Permanent.TargetJoinScreen")
                        .replace("%reason%", plugin.punishmentData.config.getString(event.getPlayer().getUniqueId().toString() + ".Ban." + punishmentManagement.getInfractionAmount(event.getPlayer(), "Ban") + ".Reason"))));
            event.setJoinMessage("");
            return;
        }

        if (plugin.vanish_logged.contains(event.getPlayer().getUniqueId())) {
            plugin.vanish_logged.remove(event.getPlayer().getUniqueId());
            plugin.vanished_players.add(event.getPlayer().getUniqueId());

            for (Player players : Bukkit.getOnlinePlayers())
                event.getPlayer().hidePlayer(players);

            event.setJoinMessage("");
        }

        if (plugin.getConfig().getBoolean("Servers.Hub.HubEveryJoin")) {
            playerManagement.sendToSpawn(plugin.getConfig().getString("Servers.Hub.Server"), event.getPlayer());

            if (plugin.getConfig().getBoolean("Servers.WorldSeparation")) {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (!serverManagement.getServer(event.getPlayer()).equalsIgnoreCase(serverManagement.getServer(players)))
                        players.hidePlayer(event.getPlayer());
                }
            }
        }

        if (plugin.getConfig().getBoolean("JoinMessage.Staff.Enabled")) {
            if (event.getPlayer().hasPermission("yocore.chats.staff")) {
                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.chats.staff"))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("JoinMessage.Staff.Message")
                                .replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))
                                .replace("%server%", serverManagement.getName(serverManagement.getServer(event.getPlayer())))
                                .replace("%world%", event.getPlayer().getWorld().getName())));
                }
            }
        }

        if (plugin.getConfig().getBoolean("JoinMessage.Enabled") && !plugin.vanished_players.contains(event.getPlayer().getUniqueId()))
            event.setJoinMessage(Utils.translate(plugin.getConfig().getString("JoinMessage.Message")
                    .replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!plugin.playerData.config.contains(event.getPlayer().getUniqueId().toString()))
            playerManagement.setupPlayer(event.getPlayer());

        System.out.println(serverManagement.getServer(event.getPlayer()));

        if (plugin.getConfig().getBoolean("QuitMessage.Staff.Enabled")) {
            if (event.getPlayer().hasPermission("yocore.chats.staff")) {
                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.chats.staff")) {
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("QuitMessage.Staff.Message")
                                .replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))
                                .replace("%server%", serverManagement.getName(serverManagement.getServer(event.getPlayer())))
                                .replace("%world%", event.getPlayer().getWorld().getName())));
                    }
                }
            }
        }

        if (plugin.vanished_players.contains(event.getPlayer().getUniqueId())) {
            plugin.vanished_players.remove(event.getPlayer().getUniqueId());
            plugin.vanish_logged.add(event.getPlayer().getUniqueId());
            event.setQuitMessage("");
        }

        if (plugin.blacklisted_players.containsKey(event.getPlayer().getUniqueId())
                || plugin.banned_players.containsKey(event.getPlayer().getUniqueId())) {
            event.setQuitMessage("");
            return;
        }

        if (plugin.getConfig().getBoolean("QuitMessage.Enabled"))
            event.setQuitMessage(Utils.translate(plugin.getConfig().getString("QuitMessage.Message")
                    .replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))));

        plugin.player_permissions.remove(event.getPlayer().getUniqueId());
    }
}
