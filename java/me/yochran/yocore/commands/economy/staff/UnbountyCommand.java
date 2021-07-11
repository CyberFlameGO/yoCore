package me.yochran.yocore.commands.economy.staff;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.EconomyManagement;
import me.yochran.yocore.management.ServerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnbountyCommand implements CommandExecutor {

    private final yoCore plugin;

    private final PlayerManagement playerManagement = new PlayerManagement();
    private final EconomyManagement economyManagement = new EconomyManagement();
    private final ServerManagement serverManagement = new ServerManagement();

    public UnbountyCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unbounty.MustBePlayer")));
            return true;
        }

        if (!economyManagement.economyIsEnabled(serverManagement.getServer((Player) sender))) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Economy.NotEnabledMessage")));
            return true;
        }

        if (!sender.hasPermission("yocore.unbounty")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unbounty.NoPermission")));
            return true;
        }

        if (!economyManagement.bountyIsEnabled(((Player) sender).getWorld().getName())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.NotEnabledMessage")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unbounty.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!economyManagement.isInitialized(target)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unbounty.InvalidPlayer")));
            return true;
        }

        if (!economyManagement.isBountied(serverManagement.getServer((Player) sender), target)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unbounty.PlayerNotBountied")));
            return true;
        }

        OfflinePlayer executor = Bukkit.getOfflinePlayer(UUID.fromString(economyManagement.getBountyExecutor(serverManagement.getServer((Player) sender), target)));

        economyManagement.addMoney(serverManagement.getServer((Player) sender), executor, economyManagement.getBountyAmount(serverManagement.getServer((Player) sender), target));
        economyManagement.removeBounty(serverManagement.getServer((Player) sender), target);

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Unbounty.Format")
                .replace("%target%", playerManagement.getPlayerColor(target))));

        return true;
    }
}