package me.yochran.yocore.commands.staff;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AltsCommand implements CommandExecutor {

    private final yoCore plugin;

    public AltsCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.alts")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Alts.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Alts.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Alts.InvalidPlayer")));
            return true;
        }

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Alts.GatheringMessage")
                .replace("%target%", yoTarget.getDisplayName())));

        List<String> alts = new ArrayList<>();

        for (String alt : plugin.playerData.config.getKeys(false)) {
            yoPlayer yoPlayer = new yoPlayer(UUID.fromString(alt));

            if (yoPlayer.getIP().equalsIgnoreCase(yoTarget.getIP())
                    || (yoTarget.getAllIPs().contains(yoPlayer.getIP())
                    || yoPlayer.getAllIPs().contains(yoTarget.getIP()))) {
                if (!target.getName().equalsIgnoreCase(yoPlayer.getPlayer().getName())) {
                    String display = "&7" + yoPlayer.getPlayer().getName();
                    if (yoPlayer.getPlayer().isOnline()) display = "&a" + yoPlayer.getPlayer().getName();
                    if (plugin.muted_players.containsKey(yoPlayer.getPlayer().getUniqueId())) display = "&6" + yoPlayer.getPlayer().getName();
                    if (plugin.banned_players.containsKey(yoPlayer.getPlayer().getUniqueId())) display = "&c" + yoPlayer.getPlayer().getName();
                    if (plugin.blacklisted_players.containsKey(yoPlayer.getPlayer().getUniqueId())) display = "&4" + yoPlayer.getPlayer().getName();

                    alts.add(display);
                }
            }
        }

        String altMessage = "";
        for (String alt : alts) {
            if (altMessage.equalsIgnoreCase("")) altMessage = alt;
            else altMessage = altMessage + "&3, " + alt;
        }

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Alts.ExecutorMessage")
                .replace("%target%", yoTarget.getDisplayName())
                .replace("%alts%", altMessage)));

        return true;
    }
}
