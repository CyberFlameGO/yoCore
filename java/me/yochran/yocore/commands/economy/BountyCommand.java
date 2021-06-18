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

public class BountyCommand implements CommandExecutor {

    private final yoCore plugin;

    private final PlayerManagement playerManagement = new PlayerManagement();
    private final EconomyManagement economyManagement = new EconomyManagement();

    public BountyCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.MustBePlayer")));
            return true;
        }

        if (!economyManagement.economyIsEnabled(((Player) sender).getWorld().getName())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Economy.NotEnabledMessage")));
            return true;
        }

        if (!economyManagement.bountyIsEnabled(((Player) sender).getWorld().getName())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.NotEnabledMessage")));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!economyManagement.isInitialized(((Player) sender).getWorld().getName(), target)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.InvalidPlayer")));
            return true;
        }

        DecimalFormat df = new DecimalFormat("###,###,###,###,###,###.##");

        try { Integer.parseInt(args[1]); } catch (NumberFormatException exception) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.InvalidAmount")
                    .replace("%minimum%",df.format(plugin.getConfig().getDouble("Bounty.MinimumAmount")))
                    .replace("%maximum%", df.format(plugin.getConfig().getDouble("Economy.MaximumAmount")))));
            return true;
        }

        if (!economyManagement.hasEnoughMoney(((Player) sender).getWorld().getName(), (Player) sender, Double.parseDouble(args[1]))) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.NotEnoughMoney")));
            return true;
        }

        if (economyManagement.isUnderBountyMinimum(Double.parseDouble(args[1])) || economyManagement.isOverMaximum(Double.parseDouble(args[1]))) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.InvalidAmount")
                    .replace("%minimum%",df.format(plugin.getConfig().getDouble("Bounty.MinimumAmount")))
                    .replace("%maximum%", df.format(plugin.getConfig().getDouble("Economy.MaximumAmount")))));
            return true;
        }

        if (!economyManagement.isBountied(((Player) sender).getWorld().getName(), target)) {
            economyManagement.setBounty(((Player) sender).getWorld().getName(), (Player) sender, target, Double.parseDouble(args[1]));

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.Format")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%amount%", df.format(Double.parseDouble(args[1])))));

            for (Player players : Bukkit.getWorld(((Player) sender).getWorld().getName()).getPlayers())
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.Broadcast")
                        .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                        .replace("%target%", playerManagement.getPlayerColor(target))
                        .replace("%amount%", df.format(Double.parseDouble(args[1])))));
        } else {
            economyManagement.increaseBounty(((Player) sender).getWorld().getName(), (Player) sender, target, Double.parseDouble(args[1]));

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.FormatIncreased")
                    .replace("%target%", playerManagement.getPlayerColor(target))
                    .replace("%amount%", df.format(economyManagement.getBountyAmount(((Player) sender).getWorld().getName(), target)))));

            for (Player players : Bukkit.getWorld(((Player) sender).getWorld().getName()).getPlayers())
                players.sendMessage(Utils.translate(plugin.getConfig().getString("Bounty.BroadcastIncreased")
                        .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                        .replace("%target%", playerManagement.getPlayerColor(target))
                        .replace("%amount%", df.format(economyManagement.getBountyAmount(((Player) sender).getWorld().getName(), target)))));
        }

        return true;
    }
}