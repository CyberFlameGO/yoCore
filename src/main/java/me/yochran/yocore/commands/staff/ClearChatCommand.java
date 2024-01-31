package me.yochran.yocore.commands.staff;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearChatCommand implements CommandExecutor {

    private final yoCore plugin;

    public ClearChatCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.clearchat")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ClearChat.NoPermission")));
            return true;
        }

        String executorName;
        if (sender instanceof Player) executorName = yoPlayer.getYoPlayer((Player) sender).getDisplayName();
        else executorName = "&c&lConsole";

        for (Player players : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < 50; i++) {
                players.sendMessage(Utils.translate("&r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n " +
                        "&r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n " +
                        "&r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n &r \n "));
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    players.sendMessage(Utils.translate(plugin.getConfig().getString("ClearChat.MessageAfter")
                            .replace("%executor%", executorName)));
                }
            }.runTaskLater(plugin, 5);
        }

        return true;
    }
}
