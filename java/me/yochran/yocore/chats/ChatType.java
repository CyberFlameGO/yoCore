package me.yochran.yocore.chats;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.server.Server;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.entity.Player;

public enum ChatType {

    BUILD,
    STAFF,
    ADMIN,
    MANAGEMENT;

    private static final yoCore plugin = yoCore.getInstance();
    private static final PlayerManagement playerManagement = new PlayerManagement();

    public static ChatType getChatFromPrefix(String prefix) {
        switch (prefix) {
            case "$": return BUILD;
            case "#": return STAFF;
            case "@": return ADMIN;
            case "!": return MANAGEMENT;
        }

        return null;
    }

    public static boolean hasPermission(Player player, ChatType type) {
        return player.hasPermission("yocore.chats." + type.toString().toLowerCase());
    }

    public static void sendMessage(Player player, Player target, ChatType type, String message) {
        target.sendMessage(Utils.translate(plugin.getConfig().getString(Utils.capitalizeFirst(type.toString().toLowerCase()) + "Chat.Format")
                .replace("%player%", playerManagement.getPlayerColor(player))
                .replace("%message%", message)
                .replace("%server%", Server.getServer(player).getName())
                .replace("%world%", player.getWorld().getName())));
    }

    public static boolean hasToggleOn(Player player) {
        return plugin.bchat_toggle.contains(player.getUniqueId())
                || plugin.schat_toggle.contains(player.getUniqueId())
                || plugin.achat_toggle.contains(player.getUniqueId())
                || plugin.mchat_toggle.contains(player.getUniqueId());
    }

    public static ChatType getToggle(Player player) {
        if (plugin.mchat_toggle.contains(player.getUniqueId())) return MANAGEMENT;
        else if (plugin.achat_toggle.contains(player.getUniqueId())) return ADMIN;
        else if (plugin.schat_toggle.contains(player.getUniqueId())) return STAFF;
        else if (plugin.bchat_toggle.contains(player.getUniqueId())) return BUILD;

        return null;
    }
}
