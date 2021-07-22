package me.yochran.yocore.commands;

import me.yochran.yocore.tags.Tag;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class TagCommand implements CommandExecutor {

    private final yoCore plugin;

    private final String[] validArgs = new String[] {
            "add",
            "create",
            "remove",
            "delete",
            "prefix",
            "display"
    };

    public TagCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.tag")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.NoPermission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.IncorrectUsage")));
            return true;
        }

        if (!Arrays.asList(validArgs).contains(args[0].toLowerCase())) {
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

                if (Tag.getTags().containsKey(args[1].toUpperCase())){
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.InvalidTag")));
                    return true;
                }

                Tag tag = new Tag(args[1].toUpperCase().replace("&", ""), "&7[" + args[1] + "] &7", "&7" + args[1]);
                tag.create();

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.AddedTag")
                        .replace("%tag%", args[1])));

                break;
            case "remove":
            case "delete":
                if (args.length != 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.IncorrectUsage")));
                    return true;
                }

                if (!Tag.getTags().containsKey(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.InvalidTag")));
                    return true;
                }

                Tag tdelete = Tag.getTag(args[1]);

                for (Map.Entry<UUID, Tag> tags : plugin.tag.entrySet()) {
                    if (tags.getValue() == tdelete)
                        plugin.tag.remove(tags.getKey());
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.RemovedTag")
                        .replace("%tag%", tdelete.getDisplay())));

                tdelete.delete();

                break;
            case "prefix":
                if (!Tag.getTags().containsKey(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.InvalidTag")));
                    return true;
                }

                Tag tprefix = Tag.getTag(args[1]);

                String prefix = "";
                for (int i = 2; i < args.length; i++) {
                    if (prefix.equalsIgnoreCase("")) prefix = args[i];
                    else prefix = prefix + " " + args[i];
                }

                tprefix.setPrefix(prefix);

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.ChangedPrefix")
                        .replace("%tag%", tprefix.getDisplay())
                        .replace("%prefix%", tprefix.getPrefix())));

                break;
            case "display":
                if (!Tag.getTags().containsKey(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.InvalidTag")));
                    return true;
                }

                Tag tdisplay = Tag.getTag(args[1]);

                String display = "";
                for (int i = 2; i < args.length; i++) {
                    if (display.equalsIgnoreCase("")) display = args[i];
                    else display = display + " " + args[i];
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("TagCommand.ChangedPrefix")
                        .replace("%tag%", tdisplay.getDisplay())
                        .replace("%prefix%", display)));

                tdisplay.setDisplay(display);

                break;
        }

        return true;
    }
}
