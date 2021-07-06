package me.yochran.yocore.listeners;

import me.yochran.yocore.yoCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final yoCore plugin;

    public PlayerInteractListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!plugin.powertool_material.containsKey(event.getPlayer().getUniqueId()) || !plugin.powertool_command.containsKey(event.getPlayer().getUniqueId()))
            return;

        if (event.getPlayer().getInventory().getItemInHand().getType() == plugin.powertool_material.get(event.getPlayer().getUniqueId()))
            event.getPlayer().performCommand(plugin.powertool_command.get(event.getPlayer().getUniqueId()));
    }
}
