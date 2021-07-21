package me.yochran.yocore.commands.staff;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCommand implements CommandExecutor {

    private final yoCore plugin;

    public InvseeCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Invsee.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.invsee")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Invsee.NoPermission")));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Invsee.IncorrectUsage")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Invsee.InvalidPlayer")));
            return true;
        }

        ((Player) sender).openInventory(target.getInventory());

        return true;
    }
}
