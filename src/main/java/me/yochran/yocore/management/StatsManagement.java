package me.yochran.yocore.management;

import me.yochran.yocore.server.Server;
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
        for (Map.Entry<String, Server> entry : Server.getServers().entrySet()) {
            plugin.statsData.config.set(target.getUniqueId().toString() + "." + entry.getValue().getName().toUpperCase() + ".Kills", 0);
            plugin.statsData.config.set(target.getUniqueId().toString() + "." + entry.getValue().getName().toUpperCase() + ".Deaths", 0);
            plugin.statsData.config.set(target.getUniqueId().toString() + "." + entry.getValue().getName().toUpperCase() + ".KDR", 0);
            plugin.statsData.config.set(target.getUniqueId().toString() + "." + entry.getValue().getName().toUpperCase() + ".Streak", 0);
        }

        plugin.statsData.saveData();
    }

    public boolean isInitialized(OfflinePlayer target) {
        return plugin.statsData.config.contains(target.getUniqueId().toString());
    }

    public boolean statsAreEnabled(Server server) {
        return plugin.getConfig().getStringList("Stats.EnabledServers").contains(server.getName());
    }

    public int getKills(Server server, OfflinePlayer target) {
        return plugin.statsData.config.getInt(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Kills");
    }

    public int getDeaths(Server server, OfflinePlayer target) {
        return plugin.statsData.config.getInt(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Deaths");
    }

    public double getKDR(Server server, OfflinePlayer target) {
        return plugin.statsData.config.getDouble(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".KDR");
    }

    public int getStreak(Server server, OfflinePlayer target) {
        return plugin.statsData.config.getInt(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Streak");
    }

    public Map<String, String> getAllStats(Server server, OfflinePlayer target) {
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

    public void addKill(Server server, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Kills", getKills(server, target) + 1);
        updateKDR(server, target);
        plugin.statsData.saveData();
    }

    public void addDeath(Server server, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Deaths", getDeaths(server, target) + 1);
        updateKDR(server, target);
        plugin.statsData.saveData();
    }

    public void updateKDR(Server server, OfflinePlayer target) {
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

        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".KDR", kdr);
        plugin.statsData.saveData();
    }

    public void addToStreak(Server server, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Streak", getStreak(server, target) + 1);
        plugin.statsData.saveData();
    }

    public void endStreak(Server server, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Streak", 0);
        plugin.statsData.saveData();
    }

    public boolean hasStreak(Server server, OfflinePlayer target) {
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

    public void resetPlayer(Server server, OfflinePlayer target) {
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Kills", 0);
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Deaths", 0);
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".KDR", 0.0);
        plugin.statsData.config.set(target.getUniqueId().toString() + "." + server.getName().toUpperCase() + ".Streak", 0);
        plugin.statsData.saveData();
    }
}
