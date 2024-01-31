package me.yochran.yocore.commands.stats;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.StatsManagement;
import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.server.Server;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class StatsCommand implements CommandExecutor {

    private final yoCore plugin;
    private final StatsManagement statsManagement = new StatsManagement();
    private final PlayerManagement playerManagement = new PlayerManagement();

    public StatsCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Stats.Command.MustBePlayer")));
            return true;
        }

        Server server = Server.getServer((Player) sender);

        if (!statsManagement.statsAreEnabled(server)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Stats.NotEnabledMessage")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Stats.Command.IncorrectUsage")));
            return true;
        }

        if (args.length == 0) {
            Map<String, String> stats = statsManagement.getAllStats(server, (Player) sender);

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Stats.Command.Format")
                    .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())
                    .replace("%kills%", stats.get("Kills"))
                    .replace("%deaths%", stats.get("Deaths"))
                    .replace("%kdr%", stats.get("KDR"))
                    .replace("%streak%", stats.get("Streak"))));
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            yoPlayer yoTarget = new yoPlayer(target);

            if (!statsManagement.isInitialized(target)) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Stats.Command.InvalidPlayer")));
                return true;
            }

            Map<String, String> stats = statsManagement.getAllStats(server, target);

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Stats.Command.Format")
                    .replace("%player%", yoTarget.getDisplayName())
                    .replace("%kills%", stats.get("Kills"))
                    .replace("%deaths%", stats.get("Deaths"))
                    .replace("%kdr%", stats.get("KDR"))
                    .replace("%streak%", stats.get("Streak"))));
        }

        return true;
    }
}