package me.yochran.yocore.data;

import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class StatsData {

    private final yoCore plugin;
    public File file;
    public FileConfiguration config;

    public StatsData() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setupData() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();

        file = new File(plugin.getDataFolder(), "stats.yml");

        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage("[yoCore] stats.yml file could not load.");
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getData() { return config; }

    public void saveData() {
        try { config.save(file); } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("[yoCore] stats.yml file could not save.");
        }
    }

    public void reloadData() { config = YamlConfiguration.loadConfiguration(file); }
}
