package me.yochran.yocore.listeners;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ModmodeListeners implements Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

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
            double Y = event.getPlayer().getLocation().getDirection().getY() * 2.0;
            double Z = event.getPlayer().getLocation().getDirection().getZ() * 7.0;
            Vector vector = new Vector(X, Y, Z);
            vector = vector.normalize();
            event.getPlayer().setVelocity(vector.multiply(2.25));
            event.getPlayer().setSprinting(true);
        }

        if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(Utils.translate("&6&lOnline Players"))) {
            Inventory inventory = Bukkit.createInventory(event.getPlayer(), 54, Utils.translate("&6&lOnline Players"));

            for (Player players : Bukkit.getOnlinePlayers()) {
                ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                skullMeta.setOwner(players.getName());
                skull.setItemMeta(skullMeta);

                ItemStack item = skull;
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(Utils.translate(playerManagement.getPlayerColor(players)));

                String rank = plugin.playerData.config.getString(players.getUniqueId().toString() + ".Rank");
                String rankDisplay = plugin.getConfig().getString("Ranks." + rank.toUpperCase() + ".Display");

                List<String> itemLore = new ArrayList<>();
                itemLore.add(Utils.translate("&7&m--------------------------"));
                itemLore.add(Utils.translate("&eRank: &f" + rankDisplay));
                itemLore.add(Utils.translate("&r "));
                itemLore.add(Utils.translate("&eClick to teleport to " + playerManagement.getPlayerColor(players)));
                itemLore.add(Utils.translate("&7&m--------------------------"));

                itemMeta.setLore(itemLore);
                item.setItemMeta(itemMeta);

                inventory.addItem(item);
            }

            event.getPlayer().openInventory(inventory);
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
            if (event.getRightClicked() instanceof Player) {
                event.getPlayer().performCommand("freeze " + event.getRightClicked().getName());
            }
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
    public void onInvClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(Utils.translate("&6&lOnline Players"))) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == XMaterial.AIR.parseMaterial())
                return;

            for (Player players : Bukkit.getOnlinePlayers()) {
                if (players.getName().equalsIgnoreCase(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())))
                    ((Player) event.getWhoClicked()).performCommand("tp " + players.getName());
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
}
