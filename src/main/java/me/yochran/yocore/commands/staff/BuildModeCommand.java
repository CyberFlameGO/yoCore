package me.yochran.yocore.commands.staff;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildModeCommand implements CommandExecutor {

    private final yoCore plugin;

    public BuildModeCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("BuildMode.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.buildmode")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("BuildMode.NoPermission")));
            return true;
        }

        if (plugin.buildmode_players.contains(((Player) sender).getUniqueId())) {
            plugin.buildmode_players.remove(((Player) sender).getUniqueId());

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("BuildMode.MessageOn")));
        } else {
            plugin.buildmode_players.add(((Player) sender).getUniqueId());

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("BuildMode.MessageOff")));
        }

        return true;
    }
}
