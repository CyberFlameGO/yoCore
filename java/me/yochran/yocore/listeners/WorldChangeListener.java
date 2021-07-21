package me.yochran.yocore.listeners;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.server.Server;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class WorldChangeListener implements Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public WorldChangeListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Server server = Server.getServer(event.getPlayer());
        yoPlayer yoPlayer = new yoPlayer(event.getPlayer());

        if (!server.getWorlds().contains(event.getPlayer().getWorld().getName())) {
            if (plugin.getConfig().getBoolean("Spawn.SpawnOnServerChange"))
                new BukkitRunnable() {
                    @Override
                    public void run() { playerManagement.sendToSpawn(server, event.getPlayer()); }
                }.runTaskLater(plugin, 1);
        }

        if (server.getWorlds().contains(event.getPlayer().getWorld().getName())
                && !server.getWorlds().contains(event.getFrom().getName())) {
            Server oldServer = null;

            for (Map.Entry<String, Server> entry : Server.getServers().entrySet()) {
                if (entry.getValue().getWorlds().contains(event.getFrom().getName()))
                    oldServer = entry.getValue();
            }

            if (event.getPlayer().hasPermission("yocore.chats.staff")) {
                if (plugin.getConfig().getBoolean("WorldChangeAlerts.Enabled")) {
                    for (Player staff : Bukkit.getOnlinePlayers()) {
                        if (staff.hasPermission("yocore.chats.staff"))
                            staff.sendMessage(Utils.translate(plugin.getConfig().getString("WorldChangeAlerts.Format")
                                    .replace("%player%", yoPlayer.getDisplayName())
                                    .replace("%old_server%", oldServer.getName())
                                    .replace("%new_server%", server.getName())
                                    .replace("%old_world%", event.getFrom().getName())
                                    .replace("%new_world%", event.getPlayer().getWorld().getName())));
                    }
                }
            }

            if (!plugin.vanished_players.contains(event.getPlayer().getUniqueId())) {
                if (plugin.getConfig().getBoolean("JoinMessage.Enabled")) {
                    for (Player player : Server.getPlayers(server))
                        player.sendMessage(Utils.translate(plugin.getConfig().getString("JoinMessage.Message")
                                .replace("%player%", yoPlayer.getDisplayName())));
                }
            }

            if (!plugin.vanished_players.contains(event.getPlayer().getUniqueId())) {
                if (plugin.getConfig().getBoolean("QuitMessage.Enabled")) {
                    for (Player player : Server.getPlayers(oldServer))
                        player.sendMessage(Utils.translate(plugin.getConfig().getString("QuitMessage.Message")
                                .replace("%player%", yoPlayer.getDisplayName())));
                }
            }
        }

        plugin.tsb.add(event.getPlayer().getUniqueId());

        new BukkitRunnable() {
            public void run() {
                event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }.runTaskLater(plugin, 5);

        plugin.tsb.remove(event.getPlayer().getUniqueId());
    }
}