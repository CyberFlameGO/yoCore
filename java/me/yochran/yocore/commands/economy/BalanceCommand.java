package me.yochran.yocore.commands.economy;

import me.yochran.yocore.management.EconomyManagement;
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

import java.text.DecimalFormat;

public class BalanceCommand implements CommandExecutor {

    private final yoCore plugin;

    private final EconomyManagement economyManagement = new EconomyManagement();

    public BalanceCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Balance.MustBePlayer")));
            return true;
        }

        Server server = Server.getServer((Player) sender);

        if (!economyManagement.economyIsEnabled(server)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Economy.NotEnabledMessage")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Balance.IncorrectUsage")));
            return true;
        }

        DecimalFormat df = new DecimalFormat("###,###,###,###,###,###.##");

        if (args.length == 0) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Balance.Format")
                    .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())
                    .replace("%balance%", df.format(economyManagement.getMoney(server, (Player) sender)))));
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            if (!economyManagement.isInitialized(target)) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Balance.InvalidPlayer")));
                return true;
            }

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Balance.Format")
                    .replace("%player%", yoPlayer.getYoPlayer(target).getDisplayName())
                    .replace("%balance%", df.format(economyManagement.getMoney(server, target)))));
        }

        return true;
    }
}