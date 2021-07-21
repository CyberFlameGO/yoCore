package me.yochran.yocore.commands.staff;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    private final yoCore plugin;

    public FlyCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Fly.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.fly")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Fly.NoPermission")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Fly.IncorrectUsage")));
            return true;
        }

        if (args.length == 0) {
            if (!((Player) sender).isFlying()) {
                ((Player) sender).setAllowFlight(true);
                ((Player) sender).setFlying(true);

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Fly.TargetMessageOn")));
            } else {
                ((Player) sender).setAllowFlight(false);
                ((Player) sender).setFlying(false);

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Fly.TargetMessageOff")));
            }
        } else {
            if (!sender.hasPermission("yocore.fly.others")) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Fly.NoPermission")));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            yoPlayer yoTarget = new yoPlayer(target);

            if (target == null) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Fly.InvalidPlayer")));
                return true;
            }

            if (!target.isFlying()) {
                target.setAllowFlight(true);
                target.setFlying(true);

                target.sendMessage(Utils.translate(plugin.getConfig().getString("Fly.TargetMessageOn")));
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Fly.ExecutorMessageOn")
                        .replace("%target%", yoTarget.getDisplayName())));
            } else {
                target.setAllowFlight(false);
                target.setFlying(false);

                target.sendMessage(Utils.translate(plugin.getConfig().getString("Fly.TargetMessageOff")));
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Fly.ExecutorMessageOff")
                        .replace("%target%", yoTarget.getDisplayName())));
            }
        }

        return true;
    }
}
