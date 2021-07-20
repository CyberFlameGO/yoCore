package me.yochran.yocore.runnables;

import me.yochran.yocore.grants.Grant;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class GrantUpdater extends BukkitRunnable {

    private final yoCore plugin;

    public GrantUpdater() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public void run() {
        for (String entry : plugin.grantData.config.getKeys(false)) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(entry));

            if (Grant.getGrants(player).size() <= 0)
                return;

            for (Map.Entry<Integer, Grant> grant : Grant.getGrants(player).entrySet()) {
                if (grant.getValue().getStatus().equalsIgnoreCase("Active")) {
                    if (!(grant.getValue().getDuration() instanceof String)) {
                        if ((long) grant.getValue().getDuration() <= System.currentTimeMillis())
                            grant.getValue().expire();
                    }
                }
            }
        }
    }
}
