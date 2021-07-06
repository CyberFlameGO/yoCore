package me.yochran.yocore.listeners;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.management.PermissionManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIClickListener implements Listener {

    private final yoCore plugin;
    private final PermissionManagement permissionManagement = new PermissionManagement();

    public GUIClickListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == XMaterial.AIR.parseMaterial())
            return;

        if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aSelect a chat color."))) {
            plugin.chat_color.remove(event.getWhoClicked().getUniqueId());

            event.getWhoClicked().closeInventory();
            event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("ChatColor.SelectedColor")
                    .replace("%color%", event.getCurrentItem().getItemMeta().getDisplayName())));

            plugin.chat_color.put(event.getWhoClicked().getUniqueId(), ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aPlayer settings."))) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains(Utils.translate("&bPrivate Messages:"))) {
                Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).performCommand("tpm");
                event.getWhoClicked().closeInventory();
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&bToggle Scoreboard"))) {
                Bukkit.getPlayer(event.getWhoClicked().getUniqueId()).performCommand("tsb");
                event.getWhoClicked().closeInventory();
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains(Utils.translate("&bMessage Sounds:"))) {
                if (!plugin.message_sounds_toggled.contains(event.getWhoClicked().getUniqueId())) {
                    event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("Settings.MessageSoundsOff")));
                    plugin.message_sounds_toggled.add(event.getWhoClicked().getUniqueId());
                } else {
                    event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("Settings.MessageSoundsOn")));
                    plugin.message_sounds_toggled.remove(event.getWhoClicked().getUniqueId());
                }
                event.getWhoClicked().closeInventory();
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains(Utils.translate("&bGlobal Chat:"))) {
                if (!plugin.chat_toggled.contains(event.getWhoClicked().getUniqueId())) {
                    event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("Settings.GlobalChatOff")));
                    plugin.chat_toggled.add(event.getWhoClicked().getUniqueId());
                } else {
                    event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("Settings.GlobalChatOn")));
                    plugin.chat_toggled.remove(event.getWhoClicked().getUniqueId());
                }
                event.getWhoClicked().closeInventory();
            }
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aChat tags."))) {
            if (event.getCurrentItem().getItemMeta().hasLore() && event.getCurrentItem().getItemMeta().getLore().contains(Utils.translate("&aClick to select this tag."))) {
                plugin.tag.put(event.getWhoClicked().getUniqueId(), ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(1).replace("Tag: ", "")));

                event.getWhoClicked().closeInventory();
                for (String tag : plugin.getConfig().getConfigurationSection("Tags").getKeys(false)) {
                    if (plugin.getConfig().getString("Tags." + tag + ".ID").equalsIgnoreCase(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(1).replace("Tag: ", "")))) {
                        event.getWhoClicked().sendMessage(Utils.translate(plugin.getConfig().getString("TagsCommand.FormatOn")
                                .replace("%tag%", plugin.getConfig().getString("Tags." + tag + ".Display"))));
                    }
                }
            } else event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aEnder Chest."))) {
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&aInventory Inspect")))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == XMaterial.AIR.parseMaterial())
            return;

        if (GUI.open_guis.containsKey(event.getWhoClicked())) {
            GUI gui = GUI.open_guis.get(event.getWhoClicked());
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
