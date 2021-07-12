package me.yochran.yocore.nametags;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.entity.Player;

public class TabSetter {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public TabSetter() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setTabName(Player player) {
        String displayName = playerManagement.getPlayerColor(player);
        if (plugin.modmode_players.contains(player.getUniqueId()))
            displayName = "&7[M] " + playerManagement.getPlayerColor(player);
        if (plugin.vanished_players.contains(player.getUniqueId()))
            displayName = "&7[V] " + playerManagement.getPlayerColor(player);

        player.setPlayerListName(Utils.translate(displayName));
    }
}
