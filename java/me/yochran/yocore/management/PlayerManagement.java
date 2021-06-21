package me.yochran.yocore.management;

import me.yochran.yocore.yoCore;
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

    public String getPlayerColor(OfflinePlayer player) {
        if (!plugin.playerData.config.contains(player.getUniqueId().toString()))
            return "&4&lNULL";

        String color;
        if (plugin.rank_disguise.containsKey(player.getUniqueId())) color = plugin.getConfig().getString("Ranks." + plugin.rank_disguise.get(player.getUniqueId()) + ".Color");
        else color = plugin.getConfig().getString("Ranks." + plugin.playerData.config.getString(player.getUniqueId().toString() + ".Rank").toUpperCase() + ".Color");

        String name;
        if (plugin.nickname.containsKey(player.getUniqueId())) name = plugin.getConfig().getString("Nickname.NickPrefix") + plugin.nickname.get(player.getUniqueId());
        else name = player.getName();

        return color + name;
    }

    public String getPlayerPrefix(OfflinePlayer player) {
        if (!plugin.playerData.config.contains(player.getUniqueId().toString()))
            return "&4&lNULL";

        String prefix;
        if (plugin.rank_disguise.containsKey(player.getUniqueId())) prefix = plugin.getConfig().getString("Ranks." + plugin.rank_disguise.get(player.getUniqueId()) + ".Prefix");
        else prefix = plugin.getConfig().getString("Ranks." + plugin.playerData.config.getString(player.getUniqueId().toString() + ".Rank").toUpperCase() + ".Prefix");
        String name;
        if (plugin.nickname.containsKey(player.getUniqueId())) name = plugin.getConfig().getString("Nickname.NickPrefix") + plugin.nickname.get(player.getUniqueId());
        else name = player.getName();

        System.out.println(name);

        return prefix + name;
    }

    public boolean checkIP(OfflinePlayer player, String ip) { return (plugin.playerData.config.getString(player.getUniqueId().toString() + ".IP").equalsIgnoreCase(ip)); }

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
}
