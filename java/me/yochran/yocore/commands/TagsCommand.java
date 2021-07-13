package me.yochran.yocore.commands;

import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.gui.guis.TagsGUI;
import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TagsCommand implements CommandExecutor {

    private final yoCore plugin;
    public PlayerManagement playerManagement = new PlayerManagement();

    public TagsCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagsCommand.MustBePlayer")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagsCommand.IncorrectUsage")));
            return true;
        }

        if (args.length == 0) {
            TagsGUI tagsGUI = new TagsGUI((Player) sender, 18, "&aChat tags.");
            tagsGUI.setup(1);
            GUI.open(tagsGUI.getGui());
        } else {
            if (args[0].equalsIgnoreCase("off")) {
                plugin.tag.remove(((Player) sender).getUniqueId());
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagsCommand.FormatOff")));
            } else {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagsCommand.IncorrectUsage")));
                return true;
            }
        }

        return true;
    }
}
