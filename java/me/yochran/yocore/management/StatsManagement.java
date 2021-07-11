package me.yochran.yocore.management;

import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;

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
        for (String server : plugin.worldData.config.getConfigurationSection("Servers").getKeys(false)) {
            plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".Kills", 0);
            plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".Deaths", 0);
            plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".KDR", 0);
            plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".Streak", 0);
        }

        plugin.statsData.saveData();
    }

    public boolean isInitialized(OfflinePlayer target) {
        return plugin.statsData.config.contains(target.getUniqueId().toString());
    }

    public boolean statsAreEnabled(String server) {
        return (plugin.getConfig().getStringList("Stats.EnabledServers").contains(server.toLowerCase()));
    }

    public int getKills(String server, OfflinePlayer target) {
        return plugin.statsData.config.getInt(target.getUniqueId().toString() + "." + server + ".Kills");
    }

    public int getDeaths(String server, OfflinePlayer target) {
        return plugin.statsData.config.getInt(target.getUniqueId().toString() + "." + server + ".Deaths");
    }

    public double getKDR(String server, OfflinePlayer target) {
        return plugin.statsData.config.getDouble(target.getUniqueId().toString() + "." + server + ".KDR");
    }

    public int getStreak(String server, OfflinePlayer target) {
        return plugin.statsData.config.getInt(target.getUniqueId().toString() + "." + server + ".Streak");
    }

    public Map<String, String> getAllStats(String server, OfflinePlayer target) {
        DecimalFormat df = new DecimalFormat("###,###.##");
        Map<String, String> stats = new HashMap<>();

        String kills = df.format(getKills(server, target));
        String deaths = df.format(getDeaths(server, target));
        String kdr = df.format(getKDR(server, target));
        String streak = df.format(getStreak(server, target));

        stats.put("Kills", kills);
        stats.put("Deaths", deaths);
        stats.put("KDR", kdr);
        stats.put("Streak", streak);

        return stats;
    }

    public void addKill(String server, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".Kills", getKills(server, target) + 1);
        updateKDR(server, target);
        plugin.statsData.saveData();
    }

    public void addDeath(String server, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".Deaths", getDeaths(server, target) + 1);
        updateKDR(server, target);
        plugin.statsData.saveData();
    }

    public void updateKDR(String server, OfflinePlayer target) {
        int kills = getKills(server, target);
        int deaths = getDeaths(server, target);

        int alternateKills;
        int alternateDeaths;

        if (deaths == 0) {
            alternateDeaths = 1;
        } else {
            alternateDeaths = deaths;
        }

        alternateKills = kills;

        double kdr = (double) alternateKills / (double) alternateDeaths;

        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".KDR", kdr);
        plugin.statsData.saveData();
    }

    public void addToStreak(String server, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".Streak", getStreak(server, target) + 1);
        plugin.statsData.saveData();
    }

    public void endStreak(String server, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".Streak", 0);
        plugin.statsData.saveData();
    }

    public boolean hasStreak(String server, OfflinePlayer target) {
        if (getStreak(server, target) > 0) {
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

    public void resetPlayer(String server, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".Kills", 0);
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".Deaths", 0);
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".KDR", 0.0);
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server + ".Streak", 0);
        plugin.statsData.saveData();
    }
}
