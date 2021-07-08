package me.yochran.yocore.commands;

import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.gui.guis.ChatColorGUI;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatColorCommand implements CommandExecutor {

    private final yoCore plugin;

    public ChatColorCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ChatColor.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.chatcolor")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ChatColor.NoPermission")));
            return true;
        }

        ChatColorGUI chatColorGUI = new ChatColorGUI((Player) sender, 36, "&aSelect a chat color.");
        chatColorGUI.setup();
        GUI.open(chatColorGUI.getGui());

        return true;
    }
}
