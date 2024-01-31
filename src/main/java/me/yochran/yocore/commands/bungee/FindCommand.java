package me.yochran.yocore.commands.bungee;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.server.Server;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FindCommand implements CommandExecutor {

    private final yoCore plugin;

    public FindCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.find")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Find.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Find.IncorrectUsage")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Find.InvalidPlayer")));
            return true;
        }

        Server server = Server.getServer((Player) sender);

        if (server == null) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Find.InvalidPlayer")));
            return true;
        }

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Find.Format")
                .replace("%target%", yoPlayer.getYoPlayer(target).getDisplayName())
                .replace("%server%", server.getName())));

        return true;
    }
}
