package me.yochran.yocore.listeners;

import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.gui.guis.GrantConfirmGUI;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GrantCustomReasonListener implements Listener {

    private final yoCore plugin;

    public GrantCustomReasonListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (plugin.grant_custom_reason.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);

            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.grant_player.get(event.getPlayer().getUniqueId()));

            plugin.grant_reason.put(event.getPlayer().getUniqueId(), event.getMessage().replace("&", ""));

            new BukkitRunnable() {
                @Override
                public void run() {
                    GrantConfirmGUI grantConfirmGUI = new GrantConfirmGUI(event.getPlayer(), 45, "&aConfirm the grant.");
                    grantConfirmGUI.setup(event.getPlayer(), target, plugin.grant_grant.get(event.getPlayer().getUniqueId()), plugin.grant_duration.get(event.getPlayer().getUniqueId()), plugin.grant_reason.get(event.getPlayer().getUniqueId()));
                    GUI.open(grantConfirmGUI.getGui());
                }
            }.runTaskLater(plugin, 1);

            plugin.grant_custom_reason.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (plugin.grant_custom_reason.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Utils.translate(plugin.getConfig().getString("Grant.Reason.CustomReasonCommandAttempt")));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) { plugin.grant_custom_reason.remove(event.getPlayer().getUniqueId()); }
}
