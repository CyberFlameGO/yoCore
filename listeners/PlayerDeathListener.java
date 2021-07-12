package me.yochran.yocore.listeners;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.EconomyManagement;
import me.yochran.yocore.management.ServerManagement;
import me.yochran.yocore.management.StatsManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerDeathListener implements Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final EconomyManagement economyManagement = new EconomyManagement();
    private final StatsManagement statsManagement = new StatsManagement();
    private final ServerManagement serverManagement = new ServerManagement();
    private final EntityDamageEvent.DamageCause[] causes = new EntityDamageEvent.DamageCause[] {
            EntityDamageEvent.DamageCause.ENTITY_ATTACK,
            EntityDamageEvent.DamageCause.PROJECTILE,
            EntityDamageEvent.DamageCause.FALL,
            EntityDamageEvent.DamageCause.MAGIC,
            EntityDamageEvent.DamageCause.LAVA,
            EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.FIRE_TICK,
            EntityDamageEvent.DamageCause.VOID
    };

    public PlayerDeathListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getEntity().getKiller() == null)
            return;

        if (event.getEntity().getKiller() == event.getEntity())
            return;

        EntityDamageEvent.DamageCause cause = event.getEntity().getLastDamageCause().getCause();
        if (!Arrays.asList(causes).contains(cause))
            return;

        if (economyManagement.economyIsEnabled(serverManagement.getServer(event.getEntity().getKiller()))) {
            DecimalFormat df = new DecimalFormat("###,###,###,###,###,###.##");

            if (economyManagement.moneyPerKillEnabled(serverManagement.getServer(event.getEntity().getKiller()))) {
                double amount = economyManagement.getMoneyPerKill();

                economyManagement.addMoney(serverManagement.getServer(event.getEntity().getKiller()), event.getEntity().getKiller(), amount);

                event.getEntity().getKiller().sendMessage(Utils.translate(plugin.getConfig().getString("Economy.MoneyOnKillMessage")
                        .replace("%target%", playerManagement.getPlayerColor(Bukkit.getOfflinePlayer(event.getEntity().getUniqueId())))
                        .replace("%amount%", df.format(amount))));
            }

            if (economyManagement.isBountied(serverManagement.getServer(event.getEntity().getKiller()), (OfflinePlayer) event.getEntity())) {
                double amount = economyManagement.getBountyAmount(serverManagement.getServer(event.getEntity().getKiller()), (OfflinePlayer) event.getEntity());

                economyManagement.claimBounty(serverManagement.getServer(event.getEntity().getKiller()), (OfflinePlayer) event.getEntity(), event.getEntity().getKiller(), amount);

                for (Player players : serverManagement.getPlayers(serverManagement.getServer(event.getEntity().getKiller()))) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.Completed")
                            .replace("%player%", playerManagement.getPlayerColor(event.getEntity().getKiller()))
                            .replace("%target%", playerManagement.getPlayerColor(Bukkit.getOfflinePlayer(event.getEntity().getUniqueId())))
                            .replace("%amount%", df.format(amount))));
                }
            }
        }

        if (statsManagement.statsAreEnabled(serverManagement.getServer(event.getEntity().getKiller()))) {
            statsManagement.addDeath(serverManagement.getServer(event.getEntity().getKiller()), (OfflinePlayer) event.getEntity());
            statsManagement.addKill(serverManagement.getServer(event.getEntity().getKiller()), event.getEntity().getKiller());
            statsManagement.addToStreak(serverManagement.getServer(event.getEntity().getKiller()), event.getEntity().getKiller());

            event.getEntity().sendMessage(Utils.translate(plugin.getConfig().getString("Stats.KilledMessage")
                    .replace("%player%", playerManagement.getPlayerColor(event.getEntity().getKiller()))));

            if (statsManagement.hasStreak(serverManagement.getServer(event.getEntity().getKiller()), (OfflinePlayer) event.getEntity())) {
                DecimalFormat df = new DecimalFormat("###,###.##");

                int streak = statsManagement.getStreak(serverManagement.getServer(event.getEntity().getKiller()), (OfflinePlayer) event.getEntity());

                statsManagement.endStreak(serverManagement.getServer(event.getEntity().getKiller()), (OfflinePlayer) event.getEntity());
                if (statsManagement.streakShouldBeAnnounced(streak)) {
                    for (Player players : serverManagement.getPlayers(serverManagement.getServer(event.getEntity().getKiller()))) {
                        players.sendMessage(Utils.translate(plugin.getConfig().getString("Stats.StreakEndBroadcast")
                                .replace("%player%", playerManagement.getPlayerColor(event.getEntity().getKiller()))
                                .replace("%target%", playerManagement.getPlayerColor(Bukkit.getOfflinePlayer(event.getEntity().getUniqueId())))
                                .replace("%streak%", df.format(streak))));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.getPlayer().getBedSpawnLocation() == null) {
            new BukkitRunnable() {
                @Override
                public void run() { playerManagement.sendToSpawn(serverManagement.getServer(event.getPlayer()), event.getPlayer()); }
            }.runTaskLater(plugin, 1);
        } else {
            if (plugin.getConfig().getBoolean("Spawn.OverrideBeds")) {
                new BukkitRunnable() {
                    @Override
                    public void run() { playerManagement.sendToSpawn(serverManagement.getServer(event.getPlayer()), event.getPlayer()); }
                }.runTaskLater(plugin, 1);
            }
        }
    }
}