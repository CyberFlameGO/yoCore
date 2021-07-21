package me.yochran.yocore.permissions;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Permissions {

    private final yoCore plugin = yoCore.getInstance();
    private static final yoCore splugin = yoCore.getInstance();

    private yoPlayer player;
    private Rank rank;

    public Permissions(yoPlayer player) { this.player = player; }
    public Permissions(Rank rank) { this.rank = rank; }

    public yoPlayer getPlayer() { return player; }
    public Rank getRank() { return rank; }

    public static void setup(yoPlayer player) {
        Player nativePlayer = (Player) player.getPlayer();

        splugin.player_permissions.remove(nativePlayer.getUniqueId());
        PermissionAttachment attachment = nativePlayer.addAttachment(splugin);

        Set<String> permissions = player.permissions().getAllPermissions();

        if (permissions.contains("*")) {
            player.getPlayer().setOp(true);
            for (Permission permission : getAllServerPermissions())
                attachment.setPermission(permission, true);
        }

        for (String permission : permissions) {
            if (permission.contains("*") && permission.length() != 1) {
                for (Permission plugin_permission : getAllServerPermissions()) {
                    if (plugin_permission.getName().contains(permission.split("\\.")[0])) {
                        if (permission.startsWith("-")) attachment.setPermission(plugin_permission.getName().replaceFirst("-", ""), false);
                        else attachment.setPermission(plugin_permission, true);
                    }
                }
            }

            if (permission.startsWith("-")) attachment.setPermission(permission.replaceFirst("-", ""), false);
            else attachment.setPermission(permission, true);
        }

        splugin.player_permissions.put(nativePlayer.getUniqueId(), attachment);

        if (!splugin.permissionsData.config.contains("Players." + nativePlayer.getUniqueId().toString()))
            player.permissions().setPermissions(new HashSet<>());
    }

    public Set<String> getPermissions() {
        Set<String> permissions = new HashSet<>();

        if (getPlayer() != null)
            permissions.addAll(plugin.permissionsData.config.getStringList("Players." + getPlayer().getPlayer().getUniqueId().toString() + ".Permissions"));

        if (getRank() != null)
            permissions.addAll(plugin.permissionsData.config.getStringList("Ranks." + getRank().getID() + ".Permissions"));

        return permissions;
    }

    public Set<String> getAllPermissions() {
        Set<String> permissions = new HashSet<>();

        if (getRank() != null || getPlayer() == null)
            return permissions;

        permissions.addAll(getPlayer().permissions().getPermissions());
        permissions.addAll(getPlayer().getRank().permissions().getPermissions());

        return permissions;
    }

    public static Set<Permission> getAllServerPermissions() {
        return splugin.getServer().getPluginManager().getPermissions();
    }

    public void setPermissions(Set<String> permissions) {
        if (getPlayer() != null)
            plugin.permissionsData.config.set("Players." + getPlayer().getPlayer().getUniqueId().toString() + ".Permissions", new ArrayList<>(permissions));

        if (getRank() != null)
            plugin.permissionsData.config.set("Ranks." + getRank().getID() + ".Permissions", new ArrayList<>(permissions));

        plugin.permissionsData.saveData();
    }

    public void add(String permission) {
        Set<String> permissions = new HashSet<>(getPermissions());
        permissions.add(permission);

        setPermissions(permissions);

        if (getPlayer() != null) refresh(getPlayer());
        if (getRank() != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                yoPlayer yoPlayer = new yoPlayer(player);

                if (yoPlayer.getRank() == getRank()) refresh(yoPlayer);
            }
        }
    }

    public void remove(String permission) {
        Set<String> permissions = new HashSet<>(getPermissions());
        permissions.remove(permission);

        setPermissions(permissions);

        if (getPlayer() != null) refresh(getPlayer());
        if (getRank() != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                yoPlayer yoPlayer = new yoPlayer(player);

                if (yoPlayer.getRank() == getRank()) refresh(yoPlayer);
            }
        }
    }

    public static void refresh(yoPlayer player) {
        Player nativePlayer = (Player) player.getPlayer();

        if (nativePlayer == null)
            return;

        try { nativePlayer.removeAttachment(splugin.player_permissions.get(nativePlayer.getUniqueId())); }
        catch (IllegalArgumentException ignored) {}

        splugin.player_permissions.remove(nativePlayer.getUniqueId());

        PermissionAttachment attachment = nativePlayer.addAttachment(splugin);

        for (Permission permission : splugin.getServer().getPluginManager().getPermissions())
            attachment.setPermission(permission, false);

        splugin.player_permissions.put(nativePlayer.getUniqueId(), attachment);

        nativePlayer.setOp(false);
        setup(player);
    }
}
