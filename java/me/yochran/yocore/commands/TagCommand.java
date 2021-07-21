package me.yochran.yocore.commands;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class TagCommand implements CommandExecutor {

    private final yoCore plugin;

    public TagCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.tag")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.NoPermission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.IncorrectUsage")));
            return true;
        }

        if (!args[0].equalsIgnoreCase("add")
                && !args[0].equalsIgnoreCase("create")
                && !args[0].equalsIgnoreCase("remove")
                && !args[0].equalsIgnoreCase("delete")
                && !args[0].equalsIgnoreCase("prefix")
                && !args[0].equalsIgnoreCase("display")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.IncorrectUsage")));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add":
            case "create":
                if (args.length != 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.IncorrectUsage")));
                    return true;
                }

                plugin.getConfig().set("Tags." + args[1].toUpperCase() + ".ID", args[1].toUpperCase());
                plugin.getConfig().set("Tags." + args[1].toUpperCase() + ".Prefix", "&7");
                plugin.getConfig().set("Tags." + args[1].toUpperCase() + ".Display", "&7" + args[1]);
                plugin.getConfig().set("Tags." + args[1].toUpperCase() + ".Permission", "yocore.tags." + args[1].toLowerCase());
                plugin.saveConfig();

                plugin.tags.add(args[1].toUpperCase());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.AddedTag")
                        .replace("%tag%", args[1])));

                break;
            case "remove":
            case "delete":
                if (args.length != 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.IncorrectUsage")));
                    return true;
                }

                if (!plugin.tags.contains(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.InvalidTag")));
                    return true;
                }

                plugin.getConfig().set("Tags." + args[1].toUpperCase(), null);
                plugin.saveConfig();

                plugin.tags.remove(args[1].toUpperCase());

                for (Map.Entry<UUID, String> tag : plugin.tag.entrySet()) {
                    if (tag.getValue().equalsIgnoreCase(args[1].toUpperCase()))
                        plugin.tag.remove(tag.getKey());
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.RemovedTag")
                        .replace("%tag%", args[1])));

                break;
            case "prefix":
                if (!plugin.tags.contains(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.InvalidTag")));
                    return true;
                }

                String prefix = "";
                for (int i = 2; i < args.length; i++) {
                    if (prefix.equalsIgnoreCase("")) prefix = args[i];
                    else prefix = prefix + " " + args[i];
                }

                plugin.getConfig().set("Tags." + args[1].toUpperCase() + ".Prefix", prefix);
                plugin.saveConfig();

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.ChangedPrefix")
                        .replace("%tag%", plugin.getConfig().getString("Tags." + args[1].toUpperCase() + ".Display"))
                        .replace("%prefix%", prefix)));

                break;
            case "display":
                if (!plugin.tags.contains(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.InvalidTag")));
                    return true;
                }

                if (args.length != 3) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.IncorrectUsage")));
                    return true;
                }

                String display = "";
                for (int i = 2; i < args.length; i++) {
                    if (display.equalsIgnoreCase("")) display = args[i];
                    else display = display + " " + args[i];
                }

                plugin.getConfig().set("Tags." + args[1].toUpperCase() + ".Display", display);
                plugin.saveConfig();

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.ChangedPrefix")
                        .replace("%tag%", plugin.getConfig().getString("Tags." + args[1].toUpperCase() + ".Display"))
                        .replace("%prefix%", display)));

                break;
        }

        return true;
    }
}
