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
        player.setPlayerListName(Utils.translate(playerManagement.getPlayerColor(player)));
    }
}
