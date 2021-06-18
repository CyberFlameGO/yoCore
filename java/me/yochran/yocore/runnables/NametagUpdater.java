package me.yochran.yocore.runnables;

import me.yochran.yocore.nametags.NametagSetter;
import me.yochran.yocore.nametags.TabSetter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NametagUpdater extends BukkitRunnable {

    private final NametagSetter nametagSetter = new NametagSetter();
    private final TabSetter tabSetter = new TabSetter();

    @Override
    public void run() {
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            for (Player player2 : Bukkit.getOnlinePlayers())
                nametagSetter.setNametag(player1, player2);

            tabSetter.setTabName(player1);
        }
    }
}
