package me.yochran.yocore.player;

import me.yochran.yocore.management.PermissionManagement;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class yoPlayer {

    private final yoCore plugin = yoCore.getInstance();
    private final PermissionManagement permissionManagement = new PermissionManagement();

    private OfflinePlayer player;

    public yoPlayer(String name) { player = Bukkit.getOfflinePlayer(name); }
    public yoPlayer(UUID uuid) { player = Bukkit.getOfflinePlayer(uuid); }
    public yoPlayer(OfflinePlayer player) { this.player = player; }

    public static yoPlayer getYoPlayer(OfflinePlayer player) { return new yoPlayer(player); }
    public static yoPlayer getYoPlayer(UUID uuid) { return new yoPlayer(uuid); }
    public static yoPlayer getYoPlayer(String name) { return new yoPlayer(name); }

    public OfflinePlayer getPlayer() { return player; }

    public Rank getRank() { return Rank.getRank(plugin.playerData.config.getString(getPlayer().getUniqueId().toString() + ".Rank")); }
    public void setRank(Rank rank) {
        plugin.playerData.config.set(getPlayer().getUniqueId().toString() + ".Rank", rank.getID());
        plugin.playerData.saveData();

        if (getPlayer().isOnline()) permissionManagement.refreshPlayer(Bukkit.getPlayer(getPlayer().getUniqueId()));
    }

    public boolean isRankDisguised() { return plugin.rank_disguise.containsKey(getPlayer().getUniqueId()); }
    public Rank getRankDisguise() { return plugin.rank_disguise.get(getPlayer().getUniqueId()); }
    public void setRankDisguise(Rank rank) { plugin.rank_disguise.put(getPlayer().getUniqueId(), rank); }

    public String getColor() { return getRank().getColor(); }
    public String getDisplayName() { return getColor() + getPlayer().getName(); }
    public String getDisplayNickname() { return plugin.getConfig().getString("Nickname.NickPrefix") + getNickname(); }
    public String getPrefix() { return getRank().getPrefix(); }

    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();

        permissions.addAll(plugin.permissionsData.config.getStringList("Players." + getPlayer().getUniqueId().toString() + ".Permissions"));
        permissions.addAll(getRank().getPermissions());

        return permissions;
    }

    public String getIP() { return plugin.playerData.config.getString(getPlayer().getUniqueId().toString() + ".IP"); }
    public List<String> getAllIPs() { return plugin.playerData.config.getStringList(getPlayer().getUniqueId().toString() + ".TotalIPs"); }
    public void setIP(String ip) {
        plugin.playerData.config.set(getPlayer().getUniqueId().toString() + ".IP", ip);
        plugin.playerData.saveData();
    }
    public void setAllIPs(List<String> ips) {
        plugin.playerData.config.set(getPlayer().getUniqueId().toString() + ".TotalIPs", ips);
        plugin.playerData.saveData();
    }

    public boolean isNicked() { return plugin.nickname.containsKey(getPlayer().getUniqueId()); }
    public String getNickname() { return plugin.nickname.get(getPlayer().getUniqueId()); }
    public void setNickname(String nickname) {
        plugin.nickname.remove(getPlayer().getUniqueId());
        plugin.nickname.put(getPlayer().getUniqueId(), nickname);
    }
    public void removeNick() { plugin.nickname.remove(getPlayer().getUniqueId()); }

    public long getFirstJoined() { return plugin.playerData.config.getLong(getPlayer().getUniqueId().toString() + ".FirstJoined"); }

    public String getPlayTime() {
        String playTime;

        try {
            int ticks = getPlayer().getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE);
            playTime = ((ticks / 20) / 3600) + " hours.";
        } catch (NoSuchFieldError ignored) { playTime = "Unavailable."; }

        return playTime;
    }

    public int getPing() {
        int ping = 0;

        try {
            Object entityPlayer = ((Player) getPlayer()).getClass().getMethod("getHandle").invoke(getPlayer());
            ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException ignored) {}

        return ping;
    }
}
