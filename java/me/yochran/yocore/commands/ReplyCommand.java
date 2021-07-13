package me.yochran.yocore.commands;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {

    private final yoCore plugin;

    public ReplyCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Message.MustBePlayer")));
            return true;
        }

        if (!sender.isOp()) {
            return true;
        }

        if (!plugin.reply.containsKey(((Player) sender).getUniqueId())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Message.NotMessaging")));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Message.IncorrectUsage")));
            return true;
        }

        String message = "";
        for (int i = 0; i < args.length; i++) {
            message = message + args[i] + " ";
        }

        ((Player) sender).performCommand("msg " + Bukkit.getPlayer(plugin.reply.get(((Player) sender).getUniqueId())).getName() + " " + message);

        return true;
    }
}
