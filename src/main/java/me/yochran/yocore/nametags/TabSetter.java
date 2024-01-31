package me.yochran.yocore.nametags;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.entity.Player;

public class TabSetter {

    private final yoCore plugin;

    public TabSetter() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setTabName(Player player) {
        yoPlayer yoPlayer = new yoPlayer(player);

        Rank rank = yoPlayer.getRank();
        if (yoPlayer.isRankDisguised())
            rank = yoPlayer.getRankDisguise();

        String displayName = rank.getColor() + player.getName();
        if (yoPlayer.isNicked())
            displayName = rank.getColor() + yoPlayer.getDisplayNickname();

        if (plugin.modmode_players.contains(player.getUniqueId())) displayName = "&7[M] " + player.getName();
        if (plugin.vanished_players.contains(player.getUniqueId())) displayName = "&7[V] " + player.getName();

        player.setPlayerListName(Utils.translate(displayName));
    }
}
