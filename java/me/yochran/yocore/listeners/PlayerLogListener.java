package me.yochran.yocore.listeners;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLogListener implements Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

    public PlayerLogListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.blacklisted_ips.containsKey(event.getPlayer().getAddress().getAddress().getHostAddress())) {
            event.getPlayer().kickPlayer(Utils.translate(plugin.getConfig().getString("Blacklist.TargetJoinScreen")
                    .replace("%reason%", plugin.blacklisted_ips.get(event.getPlayer().getAddress().getAddress().getHostAddress()))));
        }

        if (plugin.banned_players.containsKey(event.getPlayer().getUniqueId())) {
            if (plugin.banned_players.get(event.getPlayer().getUniqueId())) {
                event.getPlayer().kickPlayer(Utils.translate(plugin.getConfig().getString("Ban.Temporary.TargetJoinScreen")
                        .replace("%reason%", plugin.punishmentData.config.getString(event.getPlayer().getUniqueId().toString() + ".Ban." + punishmentManagement.getInfractionAmount(event.getPlayer(), "Ban") + ".Reason"))
                        .replace("%expiration%", Utils.getExpirationDate(plugin.punishmentData.config.getLong(event.getPlayer().getUniqueId().toString() + ".Ban." + punishmentManagement.getInfractionAmount(event.getPlayer(), "Ban") + ".Duration")))));
            } else {
                event.getPlayer().kickPlayer(Utils.translate(plugin.getConfig().getString("Ban.Permanent.TargetJoinScreen")
                        .replace("%reason%", plugin.punishmentData.config.getString(event.getPlayer().getUniqueId().toString() + ".Ban." + punishmentManagement.getInfractionAmount(event.getPlayer(), "Ban") + ".Reason"))));
            }
        }

        if (!plugin.playerData.config.contains(event.getPlayer().getUniqueId().toString())) {
            playerManagement.setupPlayer(event.getPlayer());
            punishmentManagement.setupPlayer(event.getPlayer());
        }

        if (!playerManagement.checkIP(event.getPlayer(), event.getPlayer().getAddress().getAddress().getHostAddress())) {
            plugin.playerData.config.set(event.getPlayer().getUniqueId().toString() + ".IP", event.getPlayer().getAddress().getAddress().getHostAddress());
            plugin.playerData.saveData();
        }

        if (plugin.getConfig().getBoolean("JoinMessage.Enabled"))
            event.setJoinMessage(Utils.translate(plugin.getConfig().getString("JoinMessage.Message").replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!plugin.playerData.config.contains(event.getPlayer().getUniqueId().toString()))
            playerManagement.setupPlayer(event.getPlayer());

        if (plugin.getConfig().getBoolean("QuitMessage.Enabled"))
            event.setQuitMessage(Utils.translate(plugin.getConfig().getString("QuitMessage.Message").replace("%player%", playerManagement.getPlayerColor(event.getPlayer()))));
    }
}
