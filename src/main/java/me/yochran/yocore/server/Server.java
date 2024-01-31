package me.yochran.yocore.server;

import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private final yoCore plugin = yoCore.getInstance();

    private static final Map<String, Server> servers;

    static {
        servers = new HashMap<>();
    }

    private String name;
    private List<String> worlds;
    private Location spawn;

    public Server(String name, List<String> worlds, Location spawn) {
        this.name = name;
        this.worlds = worlds;
        this.spawn = spawn;
    }

    public String getName() { return name; }
    public List<String> getWorlds() { return worlds; }
    public Location getSpawn() { return spawn; }

    public void setName(String name) { this.name = name; }

    public void create() {
        getServers().put(getName(), this);
        plugin.worldData.config.set("Servers." + getName().toUpperCase() + ".ID", getName().toUpperCase());
        plugin.worldData.config.set("Servers." + getName().toUpperCase() + ".Name", getName());
        plugin.worldData.config.set("Servers." + getName().toUpperCase() + ".Worlds", getWorlds());
        plugin.worldData.config.set("Servers." + getName().toUpperCase() + ".Spawn.World", getSpawn().getWorld().getName());
        plugin.worldData.config.set("Servers." + getName().toUpperCase() + ".Spawn.X", getSpawn().getX());
        plugin.worldData.config.set("Servers." + getName().toUpperCase() + ".Spawn.Y", getSpawn().getY());
        plugin.worldData.config.set("Servers." + getName().toUpperCase() + ".Spawn.Z", getSpawn().getZ());
        plugin.worldData.config.set("Servers." + getName().toUpperCase() + ".Spawn.Yaw", getSpawn().getYaw());
        plugin.worldData.config.set("Servers." + getName().toUpperCase() + ".Spawn.Pitch", getSpawn().getPitch());
        plugin.worldData.saveData();
    }

    public static Server getServer(String name) {
        return getServers().get(name);
    }

    public static List<Player> getPlayers(Server server) {
        List<Player> players = new ArrayList<>();

        for (String world : server.getWorlds())
            players.addAll(Bukkit.getWorld(world).getPlayers());

        return players;
    }

    public static Map<String, Server> getServers() {
        return servers;
    }

    public static Server getServer(Player player) {
        for (Map.Entry<String, Server> entry : getServers().entrySet()) {
            if (entry.getValue().getWorlds().contains(player.getWorld().getName()))
                return entry.getValue();
        }

        return null;
    }
}
