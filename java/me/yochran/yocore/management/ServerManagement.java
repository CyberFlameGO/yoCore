package me.yochran.yocore.management;

import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ServerManagement {

    private final yoCore plugin;

    public ServerManagement() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public List<String> getServers() {
        List<String> servers = new ArrayList<>();

        for (String server : plugin.worldData.config.getConfigurationSection("Servers").getKeys(false))
            servers.add(plugin.worldData.config.getString("Servers." + server + ".ID"));

        return servers;
    }

    public String getName(String server) {
        return plugin.worldData.config.getString("Servers." + server.toUpperCase() + ".Name");
    }

    public String getServer(Player player) {
        for (String server : getServers()) {
            if (plugin.worldData.config.getStringList("Servers." + server + ".Worlds").contains(player.getWorld().getName()))
                return plugin.worldData.config.getString("Servers." + server + ".ID");
        }

        return null;
    }

    public List<Player> getPlayers(String server) {
        List<Player> players = new ArrayList<>();

        for (String world : plugin.worldData.config.getStringList("Servers." + server.toUpperCase() + ".Worlds")) {
            for (Player player : Bukkit.getWorld(world).getPlayers())
                players.add(player);
        }

        return players;
    }
}
