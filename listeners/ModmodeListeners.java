package me.yochran.yocore.listeners;

import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.gui.guis.OnlinePlayersGUI;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class ModmodeListeners implements Listener {

    private final yoCore plugin;

    public ModmodeListeners() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        if (plugin.modmode_players.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().getInventory().clear();

            event.getPlayer().getInventory().setContents(plugin.inventory_contents.get(event.getPlayer().getUniqueId()));
            event.getPlayer().getInventory().setArmorContents(plugin.armor_contents.get(event.getPlayer().getUniqueId()));

            event.getPlayer().updateInventory();

            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);

            plugin.modmode_players.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!plugin.modmode_players.contains(event.getPlayer().getUniqueId()))
            return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        if (event.getItem() == null || event.getItem() == XMaterial.AIR.parseItem())
            return;

        if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&6&lLauncher"))) {
            double X = event.getPlayer().getLocation().getDirection().getX() * 7.0;
            double Z = event.getPlayer().getLocation().getDirection().getZ() * 7.0;
            Vector vector = new Vector(X, 1.35, Z);
            vector = vector.normalize();
            event.getPlayer().setVelocity(vector.multiply(2.25));
            event.getPlayer().setSprinting(true);
        }

        if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&6&lOnline Players"))) {
            OnlinePlayersGUI onlinePlayersGUI = new OnlinePlayersGUI(event.getPlayer(), 27, "&aOnline players.");
            onlinePlayersGUI.setup(1);
            GUI.open(onlinePlayersGUI.getGui());
        }

        if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&7&lBecome Invisible"))) {
            ItemStack item = XMaterial.LIME_DYE.parseItem();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(Utils.translate("&a&lBecome Visible"));
            item.setItemMeta(itemMeta);

            event.getPlayer().getInventory().setItem(7, item);

            event.getPlayer().performCommand("v");
        }

        if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&a&lBecome Visible"))) {
            ItemStack item = XMaterial.GRAY_DYE.parseItem();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(Utils.translate("&7&lBecome Invisible"));
            item.setItemMeta(itemMeta);

            event.getPlayer().getInventory().setItem(7, item);

            event.getPlayer().performCommand("v");
        }
    }

    @EventHandler
    public void onFreeze(PlayerInteractAtEntityEvent event) {
        if (!plugin.modmode_players.contains(event.getPlayer().getUniqueId()))
            return;

        if (event.getPlayer().getInventory().getItemInHand() == XMaterial.AIR.parseItem())
            return;

        if (event.getPlayer().getInventory().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&6&lFreeze"))) {
            if (event.getRightClicked() instanceof Player)
                event.getPlayer().performCommand("freeze " + event.getRightClicked().getName());
        } else if (event.getPlayer().getInventory().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&6&lInspect Player"))) {
            if (event.getRightClicked() instanceof Player)
                event.getPlayer().performCommand("invsee " + event.getRightClicked().getName());
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            if (plugin.modmode_players.contains(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
                ((Player) event.getEntity()).setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (plugin.modmode_players.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (plugin.modmode_players.contains(event.getDamager().getUniqueId()))
                event.setCancelled(true);
        }

        if (event.getEntity() instanceof Player) {
            if (plugin.modmode_players.contains(event.getEntity().getUniqueId()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (plugin.modmode_players.contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        for (LivingEntity splashed : event.getAffectedEntities()) {
            if (splashed instanceof Player) {
                if (plugin.vanished_players.contains(splashed.getUniqueId()))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.modmode_players.contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.modmode_players.contains(event.getPlayer().getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (plugin.modmode_players.contains(event.getEntity().getUniqueId())) {
            event.getDrops().clear();
            plugin.modmode_players.remove(event.getEntity().getUniqueId());
        }
    }
}
