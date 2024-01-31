package me.yochran.yocore.commands.staff;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class ServerManagerCommand implements CommandExecutor {

    private final yoCore plugin;

    public ServerManagerCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerManager.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.servermanager")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerManager.NoPermission")));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerManager.IncorrectUsage")));
            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {

            DecimalFormat df = new DecimalFormat("##.#");
            String tps = "Unavailable";


            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerManager.InfoFormat")
                    .replace("%version%", plugin.getServer().getVersion())
                    .replace("%tps%", tps)
                    .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()))));
        } else if (args[0].equalsIgnoreCase("runcmd")) {
            if (args.length < 2) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerManager.IncorrectUsage")));
                return true;
            }

            String toRun = "";
            for (int i = 1; i < args.length; i++) {
                if (toRun.equalsIgnoreCase("")) toRun = args[i];
                else toRun = toRun + " " + args[i];
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), toRun);

            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ServerManager.CommandRan")
                    .replace("%command%", toRun)));
        }

        return true;
    }
}
