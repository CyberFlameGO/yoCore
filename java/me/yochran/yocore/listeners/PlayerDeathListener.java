package me.yochran.yocore.listeners;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.EconomyManagement;
import me.yochran.yocore.management.StatsManagement;
import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.server.Server;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Arrays;

public class PlayerDeathListener implements Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final EconomyManagement economyManagement = new EconomyManagement();
    private final StatsManagement statsManagement = new StatsManagement();
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

        Server server = Server.getServer(event.getEntity().getKiller());

        yoPlayer attacker = new yoPlayer(event.getEntity().getKiller());
        yoPlayer victim = new yoPlayer((Player) event.getEntity());

        if (economyManagement.economyIsEnabled(server)) {
            DecimalFormat df = new DecimalFormat("###,###,###,###,###,###.##");

            if (economyManagement.moneyPerKillEnabled(server)) {
                double amount = economyManagement.getMoneyPerKill();

                economyManagement.addMoney(server, event.getEntity().getKiller(), amount);

                event.getEntity().getKiller().sendMessage(Utils.translate(plugin.getConfig().getString("Economy.MoneyOnKillMessage")
                        .replace("%target%", victim.getDisplayName())
                        .replace("%amount%", df.format(amount))));
            }

            if (economyManagement.isBountied(server, (OfflinePlayer) event.getEntity())) {
                double amount = economyManagement.getBountyAmount(server, (OfflinePlayer) event.getEntity());

                economyManagement.claimBounty(server, (OfflinePlayer) event.getEntity(), event.getEntity().getKiller(), amount);

                for (Player players : Server.getPlayers(server)) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.Completed")
                            .replace("%player%", attacker.getDisplayName())
                            .replace("%target%", victim.getDisplayName())
                            .replace("%amount%", df.format(amount))));
                }
            }
        }

        if (statsManagement.statsAreEnabled(server)) {
            statsManagement.addDeath(server, (OfflinePlayer) event.getEntity());
            statsManagement.addKill(server, event.getEntity().getKiller());
            statsManagement.addToStreak(server, event.getEntity().getKiller());

            event.getEntity().sendMessage(Utils.translate(plugin.getConfig().getString("Stats.KilledMessage")
                    .replace("%player%", attacker.getDisplayName())));

            if (statsManagement.hasStreak(server, (OfflinePlayer) event.getEntity())) {
                DecimalFormat df = new DecimalFormat("###,###.##");

                int streak = statsManagement.getStreak(server, (OfflinePlayer) event.getEntity());

                statsManagement.endStreak(server, (OfflinePlayer) event.getEntity());
                if (statsManagement.streakShouldBeAnnounced(streak)) {
                    for (Player players : Server.getPlayers(server)) {
                        players.sendMessage(Utils.translate(plugin.getConfig().getString("Stats.StreakEndBroadcast")
                                .replace("%player%", attacker.getDisplayName())
                                .replace("%target%", victim.getDisplayName())
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
                public void run() { playerManagement.sendToSpawn(Server.getServer(event.getPlayer()), event.getPlayer()); }
            }.runTaskLater(plugin, 1);
        } else {
            if (plugin.getConfig().getBoolean("Spawn.OverrideBeds")) {
                new BukkitRunnable() {
                    @Override
                    public void run() { playerManagement.sendToSpawn(Server.getServer(event.getPlayer()), event.getPlayer()); }
                }.runTaskLater(plugin, 1);
            }
        }
    }
}