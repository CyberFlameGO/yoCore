package me.yochran.yocore.management;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.server.Server;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerManagement {

    private yoCore plugin;

    public PlayerManagement() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setupPlayer(Player player) {
        plugin.playerData.config.set(player.getUniqueId().toString() + ".Name", player.getName());
        plugin.playerData.config.set(player.getUniqueId().toString() + ".Rank", "DEFAULT");
        plugin.playerData.config.set(player.getUniqueId().toString() + ".IP", player.getAddress().getAddress().getHostAddress());
        plugin.playerData.config.set(player.getUniqueId().toString() + ".ReportsAmount", 0);
        plugin.playerData.config.set(player.getUniqueId().toString() + ".FirstJoined", System.currentTimeMillis());

        List<String> totalIPs = new ArrayList<>();
        totalIPs.add(player.getAddress().getAddress().getHostAddress());
        plugin.playerData.config.set(player.getUniqueId().toString() + ".TotalIPs", totalIPs);

        plugin.playerData.saveData();
    }

    public int getReportsAmount(OfflinePlayer target) {
        return plugin.playerData.config.getInt(target.getUniqueId().toString() + ".ReportsAmount");
    }

    public void addReport(OfflinePlayer target, String executor, String reason, long date) {
        int ID = getReportsAmount(target) + 1;

        plugin.playerData.config.set(target.getUniqueId().toString() + ".ReportsAmount", ID);
        plugin.playerData.config.set(target.getUniqueId().toString() + ".Report." + ID + ".Executor", executor);
        plugin.playerData.config.set(target.getUniqueId().toString() + ".Report." + ID + ".Reason", reason);
        plugin.playerData.config.set(target.getUniqueId().toString() + ".Report." + ID + ".Date", date);

        plugin.playerData.saveData();
    }

    public void sendToSpawn(Server server, Player player) {
        Location location = server.getSpawn();

        player.teleport(location);
    }
}
