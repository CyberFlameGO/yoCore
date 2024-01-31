package me.yochran.yocore.commands;

import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.gui.guis.SettingsGUI;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand implements CommandExecutor {

    private final yoCore plugin;

    public SettingsCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Settings.MustBePlayer")));
            return true;
        }

        SettingsGUI settingsGUI = new SettingsGUI((Player) sender, 9, "&aPlayer settings.");
        settingsGUI.setup();
        GUI.open(settingsGUI.getGui());

        return true;
    }
}
