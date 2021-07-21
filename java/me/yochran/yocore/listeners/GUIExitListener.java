package me.yochran.yocore.listeners;

import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GUIExitListener implements Listener {

    private final yoCore plugin;

    public GUIExitListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aSelect Punishment Type.")))
            plugin.selected_history.remove(event.getPlayer().getUniqueId());
    }
}
