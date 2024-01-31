package me.yochran.yocore.commands;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

    private final yoCore plugin;

    public PingCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ping.MustBePlayer")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ping.IncorrectUsage")));
            return true;
        }

        Player target;
        String ping;

        if (args.length == 0) { target = (Player) sender; } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ping.InvalidPlayer")));
                return true;
            }
        }

        yoPlayer yoTarget = new yoPlayer(target);
        ping = String.valueOf(yoTarget.getPing());

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ping.Format")
                .replace("%player%", yoTarget.getDisplayName())
                .replace("%ping%", ping)));

        return true;
    }
}
