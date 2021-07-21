package me.yochran.yocore.commands;

import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.gui.guis.GrantsGUI;
import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantsCommand implements CommandExecutor {

    private final yoCore plugin;

    public GrantsCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("GrantHistory.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.granthistory")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("GrantHistory.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("GrantHistory.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("GrantHistory.InvalidPlayer")));
            return true;
        }

        plugin.selected_grant_history.remove(((Player) sender).getUniqueId());

        GrantsGUI grantsGUI = new GrantsGUI((Player) sender, 18, yoTarget.getDisplayName() + "&a's grant history.");
        grantsGUI.setup((Player) sender, target, 1);
        GUI.open(grantsGUI.getGui());

        plugin.selected_grant_history.put(((Player) sender).getUniqueId(), target.getUniqueId());

        return true;
    }
}
