package me.yochran.yocore.commands;

import me.yochran.yocore.management.GrantManagement;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class UngrantCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final GrantManagement grantManagement = new GrantManagement();

    public UngrantCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.grant")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ungrant.NoPermission")));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ungrant.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString()) || !plugin.grantData.config.contains(target.getUniqueId().toString()) || !plugin.grantData.config.contains(target.getUniqueId().toString() + ".Grants")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ungrant.InvalidPlayer")));
            return true;
        }

        List<Integer> grantIDs = new ArrayList<>();
        for (String grant : plugin.grantData.config.getConfigurationSection(target.getUniqueId().toString() + ".Grants").getKeys(false)) {
            grantIDs.add(plugin.grantData.config.getInt(target.getUniqueId().toString() + ".Grants." + grant + ".ID"));
        }

        if (!grantIDs.contains(Integer.parseInt(args[1]))) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ungrant.InvalidGrant")));
            return true;
        }

        grantManagement.revokeGrant(target, Integer.parseInt(args[1]));
        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Ungrant.ExecutorMessage")
                .replace("%grant%", args[1])
                .replace("%target%", playerManagement.getPlayerColor(target))));

        return true;
    }
}
