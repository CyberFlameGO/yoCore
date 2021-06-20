package me.yochran.yocore.management;

import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
        plugin.playerData.saveData();
    }

    public String getPlayerColor(OfflinePlayer player) {
        if (!plugin.playerData.config.contains(player.getUniqueId().toString()))
            return "&4&lNULL";

        return plugin.getConfig().getString("Ranks." + plugin.playerData.config.getString(player.getUniqueId().toString() + ".Rank").toUpperCase() + ".Color") + player.getName();
    }

    public String getPlayerPrefix(OfflinePlayer player) {
        if (!plugin.playerData.config.contains(player.getUniqueId().toString()))
            return "&4&lNULL";

        return plugin.getConfig().getString("Ranks." + plugin.playerData.config.getString(player.getUniqueId().toString() + ".Rank").toUpperCase() + ".Prefix") + player.getName();
    }

    public boolean checkIP(OfflinePlayer player, String ip) { return (plugin.playerData.config.getString(player.getUniqueId().toString() + ".IP").equalsIgnoreCase(ip)); }

    public int getReportsAmount(OfflinePlayer target) {
        return plugin.playerData.config.getInt(target.getUniqueId().toString() + ".ReportsAmount");
    }

    public void addReport(OfflinePlayer target, String executor, String reason, long date) {
        int ID = getReportsAmount(target) + 1;

        plugin.playerData.config.set(target.getUniqueId().toString() + ".Report." + ID + ".Executor", executor);
        plugin.playerData.config.set(target.getUniqueId().toString() + ".Report." + ID + ".Reason", reason);
        plugin.playerData.config.set(target.getUniqueId().toString() + ".Report." + ID + ".Date", date);

        plugin.playerData.saveData();
    }
}
