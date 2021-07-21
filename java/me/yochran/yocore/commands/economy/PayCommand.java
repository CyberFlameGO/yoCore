package me.yochran.yocore.commands.economy;

import me.yochran.yocore.management.PlayerManagement;
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

public class PayCommand implements CommandExecutor {

    private final yoCore plugin;

    private final PlayerManagement playerManagement = new PlayerManagement();
    private final EconomyManagement economyManagement = new EconomyManagement();

    public PayCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Pay.MustBePlayer")));
            return true;
        }

        Server server = Server.getServer((Player) sender);

        if (!economyManagement.economyIsEnabled(server)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Economy.NotEnabledMessage")));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Pay.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (!economyManagement.isInitialized(target)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Pay.InvalidPlayer")));
            return true;
        }


        DecimalFormat df = new DecimalFormat("###,###,###,###,###,###.##");

        try { Integer.parseInt(args[1]); } catch (NumberFormatException exception) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Pay.InvalidAmount")
                    .replace("%minimum%",df.format(plugin.getConfig().getDouble("Pay.MinimumAmount")))
                    .replace("%maximum%", df.format(plugin.getConfig().getDouble("Economy.MaximumAmount")))));
            return true;
        }

        if (!economyManagement.hasEnoughMoney(server, (Player) sender, Double.parseDouble(args[1]))) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Pay.NotEnoughMoney")));
            return true;
        }

        if (economyManagement.isUnderPayMinimum(Double.parseDouble(args[1])) || economyManagement.isOverMaximum(Double.parseDouble(args[1]))
                || economyManagement.isOverMaximum(Double.parseDouble(args[1]) + economyManagement.getMoney(server, (Player) sender))) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Pay.InvalidAmount")
                    .replace("%minimum%", df.format(plugin.getConfig().getDouble("Pay.MinimumAmount")))
                    .replace("%maximum%", df.format(plugin.getConfig().getDouble("Economy.MaximumAmount")))));
            return true;
        }

        economyManagement.removeMoney(server, (Player) sender, Double.parseDouble(args[1]));
        economyManagement.addMoney(server, target, Double.parseDouble(args[1]));

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Pay.FormatSender")
                .replace("%target%", yoTarget.getDisplayName())
                .replace("%amount%", df.format(Double.parseDouble(args[1])))));

        if (target.isOnline()) {
            Bukkit.getPlayer(target.getName()).sendMessage(Utils.translate(plugin.getConfig().getString("Pay.FormatTarget")
                    .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())
                    .replace("%amount%", df.format(Double.parseDouble(args[1])))));
        }

        return true;
    }
}