package me.yochran.yocore.commands.economy;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.EconomyManagement;
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

    private final PlayerManagement playerManagement = new PlayerManagement();
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

        if (!economyManagement.economyIsEnabled(((Player) sender).getWorld().getName())) {
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
                    .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                    .replace("%balance%", df.format(economyManagement.getMoney(((Player) sender).getWorld().getName(), (Player) sender)))));
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!economyManagement.isInitialized(((Player) sender).getWorld().getName(), target)) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Balance.InvalidPlayer")));
                return true;
            }

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Balance.Format")
                    .replace("%player%", playerManagement.getPlayerColor(target))
                    .replace("%balance%", df.format(economyManagement.getMoney(((Player) sender).getWorld().getName(), target)))));
        }

        return true;
    }
}