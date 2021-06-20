package me.yochran.yocore.commands;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

public class RealNameCommand implements CommandExecutor {

    private final yoCore plugin;

    public RealNameCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RealName.IncorrectUsage")));
            return true;
        }

        if (!plugin.nickname.containsValue(args[0].replace("&", ""))) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RealName.InvalidNick")));
            return true;
        }

        OfflinePlayer nicked = null;
        for (Map.Entry<UUID, String> iterator : plugin.nickname.entrySet()) {
            if (plugin.nickname.get(iterator.getKey()).equalsIgnoreCase(iterator.getValue()))
                nicked = Bukkit.getOfflinePlayer(iterator.getKey());
        }

        String targetDisplay = plugin.getConfig().getString("Ranks." + plugin.playerData.config.getString(nicked.getUniqueId().toString() + ".Rank") + ".Color") + nicked.getName();

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("RealName.Format")
                .replace("%target%", targetDisplay)
                .replace("%nickname%", plugin.nickname.get(nicked.getUniqueId()))));

        return true;
    }
}
