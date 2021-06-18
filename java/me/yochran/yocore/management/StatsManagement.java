package me.yochran.yocore.management;

import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class StatsManagement {

    private final yoCore plugin;

    public StatsManagement() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setupPlayer(OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + ".Name", target.getName());
        for (World world : Bukkit.getWorlds()) {
            plugin.statsData.config.set(target.getUniqueId().toString() + "." + world.getName() + ".Kills", 0);
            plugin.statsData.config.set(target.getUniqueId().toString() + "." + world.getName() + ".Deaths", 0);
            plugin.statsData.config.set(target.getUniqueId().toString() + "." + world.getName() + ".KDR", 0);
            plugin.statsData.config.set(target.getUniqueId().toString() + "." + world.getName() + ".Streak", 0);
        }

        plugin.statsData.saveData();
    }

    public boolean isInitialized(String world, OfflinePlayer target) {
        return plugin.statsData.config.contains(target.getUniqueId().toString());
    }

    public boolean statsAreEnabled(String world) {
        return (plugin.getConfig().getStringList("Stats.EnabledWorlds").contains(world));
    }

    public int getKills(String world, OfflinePlayer target) {
        return plugin.statsData.config.getInt(target.getUniqueId().toString() + "." + world + ".Kills");
    }

    public int getDeaths(String world, OfflinePlayer target) {
        return plugin.statsData.config.getInt(target.getUniqueId().toString() + "." + world + ".Deaths");
    }

    public double getKDR(String world, OfflinePlayer target) {
        return plugin.statsData.config.getDouble(target.getUniqueId().toString() + "." + world + ".KDR");
    }

    public int getStreak(String world, OfflinePlayer target) {
        return plugin.statsData.config.getInt(target.getUniqueId().toString() + "." + world + ".Streak");
    }

    public Map<String, String> getAllStats(String world, OfflinePlayer target) {
        DecimalFormat df = new DecimalFormat("###,###.##");
        Map<String, String> stats = new HashMap<>();

        String kills = df.format(getKills(world, target));
        String deaths = df.format(getDeaths(world, target));
        String kdr = df.format(getKDR(world, target));
        String streak = df.format(getStreak(world, target));

        stats.put("Kills", kills);
        stats.put("Deaths", deaths);
        stats.put("KDR", kdr);
        stats.put("Streak", streak);

        return stats;
    }

    public void addKill(String world, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + world + ".Kills", getKills(world, target) + 1);
        updateKDR(world, target);
        plugin.statsData.saveData();
    }

    public void addDeath(String world, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + world + ".Deaths", getDeaths(world, target) + 1);
        updateKDR(world, target);
        plugin.statsData.saveData();
    }

    public void updateKDR(String world, OfflinePlayer target) {
        int kills = getKills(world, target);
        int deaths = getDeaths(world, target);

        int alternateKills;
        int alternateDeaths;

        if (deaths == 0) {
            alternateDeaths = 1;
        } else {
            alternateDeaths = deaths;
        }

        alternateKills = kills;

        double kdr = (double) alternateKills / (double) alternateDeaths;

        plugin.statsData.config.set(target.getUniqueId().toString() + "." + world + ".KDR", kdr);
        plugin.statsData.saveData();
    }

    public void addToStreak(String world, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + world + ".Streak", getStreak(world, target) + 1);
        plugin.statsData.saveData();
    }

    public void endStreak(String world, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + world + ".Streak", 0);
        plugin.statsData.saveData();
    }

    public boolean hasStreak(String world, OfflinePlayer target) {
        if (getStreak(world, target) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean streakShouldBeAnnounced(int streak) {
        if (streak >= plugin.getConfig().getInt("Stats.MinimumStreakEndBroadcast")) {
            return true;
        } else {
            return false;
        }
    }

    public void resetPlayer(String world, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + world + ".Kills", 0);
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + world + ".Deaths", 0);
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + world + ".KDR", 0.0);
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + world + ".Streak", 0);
        plugin.statsData.saveData();
    }
}
