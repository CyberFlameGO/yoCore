package me.yochran.yocore.listeners;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.utils.XMaterial;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIClickListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == XMaterial.AIR.parseMaterial())
            return;

        if (GUI.getOpenGUIs().containsKey(event.getWhoClicked())) {
            GUI gui = GUI.getOpenGUIs().get(event.getWhoClicked());
            if (gui != null && event.getClickedInventory() != null && event.getClickedInventory().equals(gui.getInventory())) {
                event.setCancelled(true);

                Button button = gui.getButton(event.getSlot());
                if (button != null) {
                    Runnable action = button.getAction();
                    if (action != null)  action.run();
                }
            }
        }
    }
}
