package me.yochran.yocore.commands.bungee;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.server.Server;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public SpawnCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Spawn.MustBePlayer")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Spawn.IncorrectUsage")));
            return true;
        }

        if (args.length == 0) {
            playerManagement.sendToSpawn(Server.getServer((Player) sender), (Player) sender);

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Spawn.TargetMessage")));
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Spawn.InvalidPlayer")));
                return true;
            }

            playerManagement.sendToSpawn(Server.getServer(target), target);

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Spawn.ExecutorMessage")
                    .replace("%target%", yoPlayer.getYoPlayer(target).getDisplayName())));

            target.sendMessage(Utils.translate(plugin.getConfig().getString("Spawn.TargetMessage")));
        }

        return true;
    }
}
