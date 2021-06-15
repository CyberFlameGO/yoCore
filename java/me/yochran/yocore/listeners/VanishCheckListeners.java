package me.yochran.yocore.listeners;

import me.yochran.yocore.yoCore;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;

public class VanishCheckListeners implements Listener {

    private final yoCore plugin;

    public VanishCheckListeners() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;

        if (plugin.vanished_players.contains(event.getEntity().getUniqueId()) || plugin.vanished_players.contains(event.getDamager().getUniqueId()))
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
    public void onMobNotice(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            if (plugin.vanished_players.contains(event.getTarget().getUniqueId()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            if (plugin.vanished_players.contains(event.getEntity().getUniqueId()))
                event.setCancelled(true);
        }
    }
}
