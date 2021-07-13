package me.yochran.yocore.commands.staff;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final yoCore plugin;

    public ReloadCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.reload")) {
            return true;
        }

        plugin.saveConfig();
        plugin.reloadConfig();
        plugin.registerData();

        sender.sendMessage(Utils.translate("&aReloaded all files."));

        return true;
    }
}
