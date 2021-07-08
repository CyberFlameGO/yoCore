package me.yochran.yocore.commands.punishments;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.gui.guis.PunishmentHistoryGUI;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HistoryCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

    public HistoryCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("History.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.history")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("History.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("History.IncorrectUsage")));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!plugin.playerData.config.contains(target.getUniqueId().toString())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("History.InvalidPlayer")));
            return true;
        }

        PunishmentHistoryGUI punishmentHistoryGUI = new PunishmentHistoryGUI((Player) sender, 36, "&aSelect Punishment Type.");
        punishmentHistoryGUI.setup((Player) sender, target);
        GUI.open(punishmentHistoryGUI.getGui());

        plugin.selected_history.put(((Player) sender).getUniqueId(), target.getUniqueId());

        return true;
    }
}
