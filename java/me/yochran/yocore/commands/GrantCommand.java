package me.yochran.yocore.commands;

import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.gui.guis.GrantGUI;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public GrantCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.grant")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Grant.InvalidPlayer")));
            return true;
        }

        plugin.grant_player.remove(((Player) sender).getUniqueId());
        plugin.grant_grant.remove(((Player) sender).getUniqueId());
        plugin.grant_type.remove(((Player) sender).getUniqueId());
        plugin.grant_reason.remove(((Player) sender).getUniqueId());
        plugin.grant_duration.remove(((Player) sender).getUniqueId());

        GrantGUI grantGUI = new GrantGUI((Player) sender, 54, "&aSelect a grant.");
        grantGUI.setup((Player) sender, target);
        GUI.open(grantGUI.getGui());

        plugin.grant_player.put(((Player) sender).getUniqueId(), target.getUniqueId());

        return true;
    }
}