package me.yochran.yocore.listeners;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.EconomyManagement;
import me.yochran.yocore.management.StatsManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.text.DecimalFormat;

public class PlayerDeathListener implements Listener {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final EconomyManagement economyManagement = new EconomyManagement();
    private final StatsManagement statsManagement = new StatsManagement();

    public PlayerDeathListener() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getEntity().getKiller() == null)
            return;

        EntityDamageEvent.DamageCause cause = event.getEntity().getLastDamageCause().getCause();
        if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK)
            return;

        if (economyManagement.economyIsEnabled(event.getEntity().getKiller().getWorld().getName())) {
            DecimalFormat df = new DecimalFormat("###,###,###,###,###,###.##");

            if (economyManagement.moneyPerKillEnabled(event.getEntity().getKiller().getWorld().getName())) {
                double amount = economyManagement.getMoneyPerKill();

                economyManagement.addMoney(event.getEntity().getKiller().getWorld().getName(), event.getEntity().getKiller(), amount);

                event.getEntity().getKiller().sendMessage(Utils.translate(plugin.getConfig().getString("Economy.MoneyOnKillMessage")
                        .replace("%target%", playerManagement.getPlayerColor(Bukkit.getOfflinePlayer(event.getEntity().getUniqueId())))
                        .replace("%amount%", df.format(amount))));
            }

            if (economyManagement.isBountied(event.getEntity().getKiller().getWorld().getName(), (OfflinePlayer) event.getEntity())) {
                double amount = economyManagement.getBountyAmount(event.getEntity().getKiller().getWorld().getName(), (OfflinePlayer) event.getEntity());

                economyManagement.claimBounty(event.getEntity().getKiller().getWorld().getName(), (OfflinePlayer) event.getEntity(), event.getEntity().getKiller(), amount);

                for (Player players : Bukkit.getWorld(event.getEntity().getKiller().getWorld().getName()).getPlayers()) {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.Completed")
                            .replace("%player%", playerManagement.getPlayerColor(event.getEntity().getKiller()))
                            .replace("%target%", playerManagement.getPlayerColor(Bukkit.getOfflinePlayer(event.getEntity().getUniqueId())))
                            .replace("%amount%", df.format(amount))));
                }
            }
        }

        if (statsManagement.statsAreEnabled(event.getEntity().getKiller().getWorld().getName())) {
            statsManagement.addDeath(event.getEntity().getKiller().getWorld().getName(), (OfflinePlayer) event.getEntity());
            statsManagement.addKill(event.getEntity().getKiller().getWorld().getName(), event.getEntity().getKiller());
            statsManagement.addToStreak(event.getEntity().getKiller().getWorld().getName(), event.getEntity().getKiller());

            event.getEntity().sendMessage(Utils.translate(plugin.getConfig().getString("Stats.KilledMessage")
                    .replace("%player%", playerManagement.getPlayerColor(event.getEntity().getKiller()))));

            if (statsManagement.hasStreak(event.getEntity().getKiller().getWorld().getName(), (OfflinePlayer) event.getEntity())) {
                DecimalFormat df = new DecimalFormat("###,###.##");

                int streak = statsManagement.getStreak(event.getEntity().getKiller().getWorld().getName(), (OfflinePlayer) event.getEntity());

                statsManagement.endStreak(event.getEntity().getKiller().getWorld().getName(), (OfflinePlayer) event.getEntity());
                if (statsManagement.streakShouldBeAnnounced(streak)) {
                    for (Player players : Bukkit.getWorld(event.getEntity().getKiller().getWorld().getName()).getPlayers()) {
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
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");
    }
}