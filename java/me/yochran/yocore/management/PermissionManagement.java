package me.yochran.yocore.management;

import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionManagement {

    private final yoCore plugin;

    public PermissionManagement() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void initialize() {
        for (String rank : plugin.getConfig().getConfigurationSection("Ranks").getKeys(false)) {
            if (!plugin.permissionsData.config.contains("Ranks." + plugin.getConfig().getString("Ranks." + rank + ".ID")))
                plugin.permissionsData.config.set("Ranks." + plugin.getConfig().getString("Ranks." + rank + ".ID") + ".Permissions", new ArrayList<>());
        }

        plugin.permissionsData.saveData();
    }

    public List<String> getAllPlayerPermissions(Player player) {
        List<String> permissions = new ArrayList<>();

        permissions.addAll(getRankPermissions(plugin.playerData.config.getString(player.getUniqueId().toString() + ".Rank")));
        permissions.addAll(getPlayerPermissions(player));

        return permissions;
    }

    public List<Permission> getAllPluginPerms() {
        List<Permission> plugin_permissions = new ArrayList<>();
        for (Permission perm : plugin.getServer().getPluginManager().getPermissions()) {
            if (!plugin_permissions.contains(perm))
                plugin_permissions.add(perm);
        }

        return plugin_permissions;
    }

    public void setupPlayer(Player player) {
        plugin.player_permissions.remove(player.getUniqueId());

        PermissionAttachment attachment = player.addAttachment(plugin);

        for (Map.Entry<String, Boolean> oldPermissions : attachment.getPermissions().entrySet())
            attachment.unsetPermission(oldPermissions.getKey());

        for (String permission : getAllPlayerPermissions(player)) {
            if (permission.equalsIgnoreCase("*")) {
                for (Permission plugin_permission : getAllPluginPerms()) {
                    attachment.setPermission(plugin_permission, true);
                }
            }

            if (permission.contains("*") && permission.length() != 1) {
                String plugin = permission.split("\\.")[0];
                for (Permission plugin_permission : getAllPluginPerms()) {
                    if (plugin_permission.getName().contains(plugin)) {
                        if (permission.startsWith("-")) attachment.setPermission(plugin_permission.getName().replaceFirst("-", ""), false);
                        else attachment.setPermission(plugin_permission, true);
                    }
                }
            }

            if (permission.startsWith("-"))
                attachment.setPermission(permission.replaceFirst("-", ""), false);
            else attachment.setPermission(permission, true);
        }

        plugin.player_permissions.put(player.getUniqueId(), attachment);

        if (!plugin.permissionsData.config.contains("Players." + player.getUniqueId().toString())) {
            plugin.permissionsData.config.set("Players." + player.getUniqueId().toString() + ".Permissions", new ArrayList<>());
            plugin.permissionsData.saveData();
        }
    }

    public List<String> getRankPermissions(String rank) {
        List<String> permissions = new ArrayList<>();

        if (plugin.permissionsData.config.contains("Ranks." + rank))
            permissions.addAll(plugin.permissionsData.config.getStringList("Ranks." + rank + ".Permissions"));

        return permissions;
    }

    public List<String> getPlayerPermissions(Player player) {
        List<String> permissions = new ArrayList<>();

        if (plugin.permissionsData.config.contains("Players." + player.getUniqueId().toString()))
            permissions.addAll(plugin.permissionsData.config.getStringList("Players." + player.getUniqueId().toString() + ".Permissions"));

        return permissions;
    }

    public void addRankPermission(String rank, String permission) {
        if (plugin.permissionsData.config.contains("Ranks." + rank + ".Permissions")) {
            if (plugin.permissionsData.config.getStringList("Ranks." + rank + ".Permissions").contains(permission))
                return;

            List<String> rank_permissions = new ArrayList<>();

            rank_permissions.addAll(getRankPermissions(rank));
            rank_permissions.add(permission);

            plugin.permissionsData.config.set("Ranks." + rank + ".Permissions", rank_permissions);
            plugin.permissionsData.saveData();

            for (Player players : Bukkit.getOnlinePlayers()) {
                if (plugin.playerData.config.getString(players.getUniqueId().toString() + ".Rank").equalsIgnoreCase(rank))
                    refreshPlayer(players);
            }
        }
    }

    public void addPlayerPermission(OfflinePlayer player, String permission) {
        if (plugin.permissionsData.config.contains("Players." + player.getUniqueId().toString() + ".Permissions")) {
            if (plugin.permissionsData.config.getStringList("Players." + player.getUniqueId().toString() + ".Permissions").contains(permission))
                return;

            List<String> player_permissions = new ArrayList<>();

            player_permissions.addAll(plugin.permissionsData.config.getStringList("Players." + player.getUniqueId().toString() + ".Permissions"));
            player_permissions.add(permission);

            plugin.permissionsData.config.set("Players." + player.getUniqueId().toString() + ".Permissions", player_permissions);
            plugin.permissionsData.saveData();

            if (player.isOnline())
                refreshPlayer(Bukkit.getPlayer(player.getUniqueId()));
        }
    }

    public void removeRankPermission(String rank, String permission) {
        if (plugin.permissionsData.config.contains("Ranks." + rank + ".Permissions")) {
            List<String> rank_permissions = new ArrayList<>();

            rank_permissions.addAll(getRankPermissions(rank));
            rank_permissions.remove(permission);

            plugin.permissionsData.config.set("Ranks." + rank + ".Permissions", rank_permissions);
            plugin.permissionsData.saveData();
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            if (plugin.playerData.config.getString(players.getUniqueId().toString() + ".Rank").equalsIgnoreCase(rank))
                refreshPlayer(players);
        }
    }

    public void removePlayerPermission(OfflinePlayer player, String permission) {
        if (plugin.permissionsData.config.contains("Players." + player.getUniqueId().toString() + ".Permissions")) {
            List<String> rank_permissions = new ArrayList<>();

            rank_permissions.addAll(plugin.permissionsData.config.getStringList("Players." + player.getUniqueId().toString() + ".Permissions"));
            rank_permissions.remove(permission);

            plugin.permissionsData.config.set("Players." + player.getUniqueId().toString() + ".Permissions", rank_permissions);
            plugin.permissionsData.saveData();

            if (player.isOnline()) refreshPlayer(Bukkit.getPlayer(player.getUniqueId()));
        }
    }

    public void refreshPlayer(Player player) {
        try { player.removeAttachment(plugin.player_permissions.get(player.getUniqueId())); }
        catch (IllegalArgumentException ignored) {}
        plugin.player_permissions.remove(player.getUniqueId());

        PermissionAttachment attachment = player.addAttachment(plugin);
        for (Permission permission : getAllPluginPerms())
            attachment.setPermission(permission, false);
        plugin.player_permissions.put(player.getUniqueId(), attachment);

        player.recalculatePermissions();
        setupPlayer(player);
    }
}
