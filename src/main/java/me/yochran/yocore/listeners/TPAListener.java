package me.yochran.yocore.listeners;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TPAListener implements Listener {

    private final yoCore plugin;

    public TPAListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (plugin.tpa.containsKey(event.getPlayer().getUniqueId())
                && plugin.tpa_timer.containsKey(event.getPlayer().getUniqueId())
                && plugin.tpa_coords.containsKey(event.getPlayer().getUniqueId())) {
            if (plugin.tpa_coords.get(event.getPlayer().getUniqueId()).getX() != event.getPlayer().getLocation().getX()
                    || plugin.tpa_coords.get(event.getPlayer().getUniqueId()).getZ() != event.getPlayer().getLocation().getZ()) {
                plugin.tpa.remove(event.getPlayer().getUniqueId());
                plugin.tpa_coords.remove(event.getPlayer().getUniqueId());
                plugin.tpa_timer.remove(event.getPlayer().getUniqueId());

                event.getPlayer().sendMessage(Utils.translate(plugin.getConfig().getString("Teleport.TeleportRequestCancelled")));
            }
        }
    }
}
