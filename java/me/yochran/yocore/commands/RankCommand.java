package me.yochran.yocore.commands;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class RankCommand implements CommandExecutor {

    private final yoCore plugin;

    public RankCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("yocore.rank")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.NoPermission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
            return true;
        }

        if (!args[0].equalsIgnoreCase("add")
                && !args[0].equalsIgnoreCase("create")
                && !args[0].equalsIgnoreCase("remove")
                && !args[0].equalsIgnoreCase("delete")
                && !args[0].equalsIgnoreCase("prefix")
                && !args[0].equalsIgnoreCase("color")
                && !args[0].equalsIgnoreCase("display")
                && !args[0].equalsIgnoreCase("priority")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add":
            case "create":
                if (args.length != 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                    return true;
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.CreatedRank")
                        .replace("%rank%", args[1])));

                List<Integer> ranks = new ArrayList<>();
                for (String rank : plugin.getConfig().getConfigurationSection("Ranks").getKeys(false)) {
                    ranks.add(plugin.getConfig().getInt("Ranks." + rank + ".Priority"));
                }

                int priority = Collections.max(ranks) + 1;

                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".Prefix", "&7[" + args[1] + "&7] &7");
                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".Color", "&7");
                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".Display", "&7" + args[1]);
                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".Priority", priority);
                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".GrantItem", "GRAY_WOOL");
                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".GrantPermission", "yocore.grant." + args[1].toLowerCase());
                plugin.saveConfig();

                plugin.ranks.add(args[1].toUpperCase());

                break;
            case "remove":
            case "delete":
                if (args.length != 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                    return true;
                }

                if (!plugin.ranks.contains(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.RemovedRank")
                        .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))));

                int lowestPriority;
                String lowestRank = "DEFAULT";
                List<Integer> priorities = new ArrayList<>();

                for (String rank : plugin.ranks) {
                    if (!priorities.contains(plugin.getConfig().getInt("Ranks." + rank + ".Priority")) && !rank.equalsIgnoreCase(args[1].toUpperCase()))
                        priorities.add(plugin.getConfig().getInt("Ranks." + rank + ".Priority"));
                }

                lowestPriority = Collections.max(priorities);

                for (String rank : plugin.ranks) {
                    if (plugin.getConfig().getInt("Ranks." + rank + ".Priority") == lowestPriority && !rank.equalsIgnoreCase(args[1].toUpperCase()))
                        lowestRank = rank;
                }

                for (String player : plugin.playerData.config.getKeys(false)) {
                    if (plugin.playerData.config.getString(player + ".Rank").equalsIgnoreCase(args[1].toUpperCase()))
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setrank " + Bukkit.getOfflinePlayer(UUID.fromString(player)).getName() + " " + lowestRank);
                }

                for (String player : plugin.grantData.config.getKeys(false)) {
                    if (plugin.grantData.config.contains(player + ".Grants")) {
                        for (String grant : plugin.grantData.config.getConfigurationSection(player + ".Grants").getKeys(false)) {
                            if (plugin.grantData.config.getString(player + ".Grants." + grant + ".Rank").equalsIgnoreCase(args[1].toUpperCase())) {
                                plugin.grantData.config.set(player + ".Grants." + grant + ".Status", "Expired");
                                plugin.grantData.config.set(player + ".Grants." + grant + ".Rank", "(Removed Rank)");
                            }
                        }
                    }
                }

                plugin.getConfig().set("Ranks." + args[1].toUpperCase(), null);
                plugin.saveConfig();

                plugin.ranks.remove(args[1].toUpperCase());


                break;
            case "prefix":
                if (!plugin.ranks.contains(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                String prefix = "";
                for (int i = 2; i < args.length; i++) {
                    if (prefix.equalsIgnoreCase("")) prefix = args[i];
                    else prefix = prefix + " " + args[i];
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.PrefixChanged")
                        .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))
                        .replace("%prefix%", prefix)));

                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".Prefix", prefix);
                plugin.saveConfig();

                break;
            case "color":
                if (args.length != 3) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                    return true;
                }

                if (!plugin.ranks.contains(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.ColorChanged")
                        .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))
                        .replace("%color%", args[2] + "color")));

                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".Color", args[2]);
                plugin.saveConfig();

                break;
            case "display":
                if (!plugin.ranks.contains(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                String display = "";
                for (int i = 2; i < args.length; i++) {
                    if (display.equalsIgnoreCase("")) display = args[i];
                    else display = display + " " + args[i];
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.DisplayChanged")
                        .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))
                        .replace("%display%", display)));

                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".Display", display);
                plugin.saveConfig();

                break;
            case "priority":
                if (args.length != 3) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                    return true;
                }

                if (!plugin.ranks.contains(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.PriorityChanged")
                        .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))
                        .replace("%priority%", args[2])));

                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".Priority", Integer.parseInt(args[2]));
                plugin.saveConfig();
        }

        return true;
    }
}
