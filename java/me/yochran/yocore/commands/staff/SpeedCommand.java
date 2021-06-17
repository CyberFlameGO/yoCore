package me.yochran.yocore.commands.staff;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public SpeedCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Speed.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.speed")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Speed.NoPermission")));
            return true;
        }

        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Speed.IncorrectUsage")));
            return true;
        }

        try { Float.parseFloat(args[0]); }
        catch (NumberFormatException exception) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Speed.InvalidSpeed")));
            return true;
        }

        if (Float.parseFloat(args[0]) < -1 || Float.parseFloat(args[0]) > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Speed.InvalidSpeed")));
            return true;
        }

        if (args.length == 1) {
            if (((Player) sender).isFlying()) ((Player) sender).setFlySpeed(Float.parseFloat(args[0]));
            else ((Player) sender).setWalkSpeed(Float.parseFloat(args[0]));

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Speed.TargetMessage")
                    .replace("%speed%", args[0])));
        } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Speed.InvalidPlayer")));
                return true;
            }

            if (target.isFlying()) target.setFlySpeed(Float.parseFloat(args[0]));
            else target.setWalkSpeed(Float.parseFloat(args[0]));

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Speed.ExecutorMessage")
                    .replace("%speed%", args[0])
                    .replace("%target%", playerManagement.getPlayerColor(target))));

            target.sendMessage(Utils.translate(plugin.getConfig().getString("Speed.TargetMessage")
                    .replace("%speed%", args[0])));
        }

        return true;
    }
}
