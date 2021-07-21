package me.yochran.yocore.commands;

import me.yochran.yocore.management.PermissionManagement;
import me.yochran.yocore.ranks.Rank;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class RankCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PermissionManagement permissionManagement = new PermissionManagement();

    private String[] validArgs = new String[] {
            "add",
            "create",
            "remove",
            "delete",
            "prefix",
            "color",
            "display",
            "item",
            "permission",
            "permissions",
            "tabindex",
            "index"
    };

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

        if (!Arrays.asList(validArgs).contains(args[0].toLowerCase())) {
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

                if (Rank.getRanks().containsKey(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                Rank add = new Rank(args[1].toUpperCase(), "&7", "&7", "&7" + args[1], "z", XMaterial.GRAY_WOOL.parseItem(), false);
                add.create();

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.CreatedRank")
                        .replace("%rank%", args[1])));

                break;
            case "remove":
            case "delete":
                if (args.length != 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                    return true;
                }

                if (!Rank.getRanks().containsKey(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                Rank delete = Rank.getRank(args[1].toUpperCase());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.RemovedRank")
                        .replace("%rank%", delete.getDisplay())));

                delete.delete();

                break;
            case "prefix":
                if (!Rank.getRanks().containsKey(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                Rank rprefix = Rank.getRank(args[1]);

                String prefix = "";
                for (int i = 2; i < args.length; i++) {
                    if (prefix.equalsIgnoreCase("")) prefix = args[i];
                    else prefix = prefix + " " + args[i];
                }

                rprefix.setPrefix(prefix);

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.PrefixChanged")
                        .replace("%rank%", rprefix.getDisplay())
                        .replace("%prefix%", rprefix.getPrefix())));

                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    for (Team team : player1.getScoreboard().getTeams())
                        player1.getScoreboard().getTeam(team.getName()).unregister();
                }

                break;
            case "color":
                if (args.length != 3) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                    return true;
                }

                if (!Rank.getRanks().containsKey(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                Rank rcolor = Rank.getRank(args[1]);
                rcolor.setColor(args[2]);

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.ColorChanged")
                        .replace("%rank%", rcolor.getDisplay())
                        .replace("%color%", rcolor.getColor() + "color")));

                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    for (Team team : player1.getScoreboard().getTeams())
                        player1.getScoreboard().getTeam(team.getName()).unregister();
                }

                break;
            case "display":
                if (!Rank.getRanks().containsKey(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                Rank rdisplay = Rank.getRank(args[1]);

                String display = "";
                for (int i = 2; i < args.length; i++) {
                    if (display.equalsIgnoreCase("")) display = args[i];
                    else display = display + " " + args[i];
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.DisplayChanged")
                        .replace("%rank%", rdisplay.getDisplay())
                        .replace("%display%", display)));

                rdisplay.setDisplay(display);

                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    for (Team team : player1.getScoreboard().getTeams())
                        player1.getScoreboard().getTeam(team.getName()).unregister();
                }

                break;
            case "item":
            case "grantitem":
                if (args.length != 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                    return true;
                }

                if (!Rank.getRanks().containsKey(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                if (((Player) sender).getInventory().getItemInHand().getType() == XMaterial.AIR.parseMaterial()) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidItem")));
                    return true;
                }

                Rank ritem = Rank.getRank(args[1]);

                ItemStack item = ((Player) sender).getInventory().getItemInHand();

                ritem.setGrantItem(item);

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.GrantItemChanged")
                        .replace("%rank%", ritem.getDisplay())
                        .replace("%item%", Utils.getColoredItemData(ritem.getGrantItem(), ritem.getGrantItem().getType().toString()))));

                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    for (Team team : player1.getScoreboard().getTeams())
                        player1.getScoreboard().getTeam(team.getName()).unregister();
                }

                break;
            case "index":
            case "tabindex":
                if (args.length != 3) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                    return true;
                }

                if (!Rank.getRanks().containsKey(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                Rank rtabindex = Rank.getRank(args[1]);
                rtabindex.setTabIndex(args[2]);

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.TabIndexChanged")
                        .replace("%rank%", rtabindex.getDisplay())
                        .replace("%index%", rtabindex.getTabIndex())));

                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    for (Team team : player1.getScoreboard().getTeams())
                        player1.getScoreboard().getTeam(team.getName()).unregister();
                }

                break;
            case "permission":
            case "permissions":
                if (args.length < 3) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                    return true;
                }

                if (!Rank.getRanks().containsKey(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                if (!args[2].equalsIgnoreCase("add") && !args[2].equalsIgnoreCase("remove") && !args[2].equalsIgnoreCase("list")) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                    return true;
                }

                Rank rpermission = Rank.getRank(args[1]);

                switch (args[2].toLowerCase()) {
                    case "add":
                        if (args.length != 4) {
                            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                            return true;
                        }

                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.PermissionAdded")
                                .replace("%permission%", args[3])
                                .replace("%rank%", rpermission.getDisplay())));

                        rpermission.permissions().add(args[3].toLowerCase());

                        break;
                    case "remove":
                        if (args.length != 4) {
                            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                            return true;
                        }

                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.PermissionRemoved")
                                .replace("%permission%", args[3])
                                .replace("%rank%", rpermission.getDisplay())));

                        rpermission.permissions().remove(args[3].toLowerCase());

                        break;
                    case "list":
                        if (args.length != 3) {
                            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                            return true;
                        }

                        String permissions = "";
                        for (String perm : rpermission.permissions().getPermissions()) {
                            if (permissions.equalsIgnoreCase("")) permissions = "&7 - " + perm;
                            else permissions = permissions + "\n&7 - " + perm;
                        }

                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.RankPermissions")
                                .replace("%permissions%", permissions)
                                .replace("%rank%", rpermission.getDisplay())));

                        break;
                }

                break;
        }

        return true;
    }
}
