package me.yochran.yocore.management;

import me.yochran.yocore.punishments.Punishment;
import me.yochran.yocore.punishments.PunishmentType;
import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;

import java.util.Map;

public class PunishmentManagement {

    private final yoCore plugin;

    public PunishmentManagement() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setupPlayer(OfflinePlayer player) {
        plugin.punishmentData.config.set(player.getUniqueId().toString() + ".Name", player.getName());
        plugin.punishmentData.config.set(player.getUniqueId().toString() + ".Warn", null);
        plugin.punishmentData.config.set(player.getUniqueId().toString() + ".Kick", null);
        plugin.punishmentData.config.set(player.getUniqueId().toString() + ".Mute", null);
        plugin.punishmentData.config.set(player.getUniqueId().toString() + ".Ban", null);
        plugin.punishmentData.config.set(player.getUniqueId().toString() + ".Blacklist", null);
        plugin.punishmentData.saveData();
    }

    public int getAmount(OfflinePlayer player, PunishmentType type) {
        int amount = 0;

        for (Map.Entry<Integer, Punishment> entry : Punishment.getPunishments(player).entrySet()) {
            if (entry.getValue().getType() == type)
                amount++;
        }

        return amount;
    }

    public void clearHistory(OfflinePlayer target) {
        setupPlayer(target);

        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Warn", null);
        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Mute", null);
        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Kick", null);
        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Ban", null);
        plugin.punishmentData.config.set(target.getUniqueId().toString() + ".Blacklist", null);

        if (plugin.punishmentData.config.contains("MutedPlayers")) {
            for (String muted : plugin.punishmentData.config.getConfigurationSection("MutedPlayers").getKeys(false)) {
                if (muted.equalsIgnoreCase(target.getUniqueId().toString())) {
                    plugin.punishmentData.config.set("MutedPlayers." + target.getUniqueId().toString(), null);
                    plugin.muted_players.remove(target.getUniqueId());
                }
            }
        }

        if (plugin.punishmentData.config.contains("BannedPlayers")) {
            for (String banned : plugin.punishmentData.config.getConfigurationSection("BannedPlayers").getKeys(false)) {
                if (banned.equalsIgnoreCase(target.getUniqueId().toString())) {
                    plugin.punishmentData.config.set("BannedPlayers." + target.getUniqueId().toString(), null);
                    plugin.banned_players.remove(target.getUniqueId());
                }
            }
        }

        if (plugin.punishmentData.config.contains("BlacklistedPlayers")) {
            for (String blacklisted : plugin.punishmentData.config.getConfigurationSection("BlacklistedPlayers").getKeys(false)) {
                if (blacklisted.equalsIgnoreCase(target.getUniqueId().toString())) {
                    plugin.punishmentData.config.set("BlacklistedPlayers." + target.getUniqueId().toString(), null);
                    plugin.blacklisted_players.remove(target.getUniqueId());
                }
            }
        }

        plugin.punishmentData.saveData();

        for (Map.Entry<Integer, Punishment> entry : Punishment.getPunishments(target).entrySet())
            Punishment.getPunishments().remove(entry.getKey());
    }
}
