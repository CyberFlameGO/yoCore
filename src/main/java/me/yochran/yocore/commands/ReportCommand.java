package me.yochran.yocore.commands;

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

public class ReportCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public ReportCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Report.MustBePlayer")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Report.IncorrectUsage")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (target == null) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Report.InvalidPlayer")));
            return true;
        }

        String reason = "";
        for (int i = 1; i < args.length; i++) {
            if (reason.equalsIgnoreCase("")) reason = args[i];
            else reason = reason + " " + args[i];
        }

        playerManagement.addReport(target, ((Player) sender).getUniqueId().toString(), reason, System.currentTimeMillis());

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Report.ExecutorMessage")
                .replace("%target%", yoTarget.getDisplayName())
                .replace("%reason%", reason)));

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("yocore.chats.staff"))
                staff.sendMessage(Utils.translate(plugin.getConfig().getString("Report.StaffAlert")
                        .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())
                        .replace("%target%", yoTarget.getDisplayName())
                        .replace("%reason%", reason)
                        .replace("%word%", ((Player) sender).getWorld().getName())
                        .replace("%server%", Server.getServer((Player) sender).getName())));
        }

        return true;
    }
}
