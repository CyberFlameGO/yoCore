package me.yochran.yocore.commands.staff;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SeenCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public SeenCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.seen")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Seen.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Seen.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Seen.InvalidPlayer")));
            return true;
        }

        String ip;
        if (sender.hasPermission("yocore.seen.ip")) {
            ip = plugin.playerData.config.getString(target.getUniqueId().toString() + ".IP");
        } else {
            ip = "Hidden";
        }
        String rank = plugin.playerData.config.getString(target.getUniqueId().toString() + ".Rank");
        String rankDisplay = plugin.getConfig().getString("Ranks." + rank + ".Display");
        String allIPsMessage = "";
        for (String entry : plugin.playerData.config.getStringList(target.getUniqueId().toString() + ".TotalIPs")) {
            if (allIPsMessage.equalsIgnoreCase("")) allIPsMessage = "&7- " + entry;
            else allIPsMessage = allIPsMessage + "\n&7- " + entry;
        }

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Seen.Format")
                .replace("%target%", playerManagement.getPlayerColor(target))
                .replace("%name%", target.getName())
                .replace("%rank%", rankDisplay)
                .replace("%ip%", ip)
                .replace("%firstjoined%", Utils.getExpirationDate(plugin.playerData.config.getLong(target.getUniqueId().toString() + ".FirstJoined")))
                .replace("%all_ips%", allIPsMessage)));

        return true;
    }
}
