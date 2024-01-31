package me.yochran.yocore.commands;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ToggleScoreboardCommand implements CommandExecutor {

    private final yoCore plugin;

    public ToggleScoreboardCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ToggleScoreboard.MustBePlayer")));
            return true;
        }

        if (!plugin.tsb.contains(((Player) sender).getUniqueId())) {
            plugin.tsb.add(((Player) sender).getUniqueId());

            new BukkitRunnable() {
                public void run() { ((Player) sender).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); }
            }.runTaskLater(plugin, 5);

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ToggleScoreboard.ToggleOff")));
        } else {
            plugin.tsb.remove(((Player) sender).getUniqueId());

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ToggleScoreboard.ToggleOn")));
        }

        return true;
    }
}