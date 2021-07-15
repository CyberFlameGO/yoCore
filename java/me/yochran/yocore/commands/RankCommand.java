package me.yochran.yocore.commands;

import me.yochran.yocore.management.PermissionManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
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

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.CreatedRank")
                        .replace("%rank%", args[1])));

                List<Integer> ranks = new ArrayList<>();
                for (String rank : plugin.getConfig().getConfigurationSection("Ranks").getKeys(false))
                    ranks.add(plugin.getConfig().getInt("Ranks." + rank + ".Priority"));

                int priority = Collections.max(ranks) + 1;

                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".ID", args[1].toUpperCase());
                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".Prefix", "&7[" + args[1] + "&7] &7");
                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".Color", "&7");
                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".Display", "&7" + args[1]);
                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".TabIndex", "z");
                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".GrantItem", "GRAY_WOOL");
                plugin.saveConfig();

                Permission newPermission = new Permission("yocore.grant." + args[1].toLowerCase());
                newPermission.setDescription("Permission");

                if (permissionManagement.getAllServerPerms().contains(newPermission))
                    plugin.getServer().getPluginManager().addPermission(newPermission);

                plugin.ranks.add(args[1].toUpperCase());

                plugin.permissionsData.config.set("Ranks." + args[1].toUpperCase() + ".Permissions", new ArrayList<>());
                plugin.permissionsData.saveData();

                for (Player players : Bukkit.getOnlinePlayers()) {
                    for (Team team : players.getScoreboard().getTeams())
                        players.getScoreboard().getTeam(team.getName()).unregister();
                }

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
                            if (plugin.grantData.config.getString(player + ".Grants." + grant + ".Grant").equalsIgnoreCase(args[1].toUpperCase())) {
                                plugin.grantData.config.set(player + ".Grants." + grant + ".Status", "Expired");
                                plugin.grantData.config.set(player + ".Grants." + grant + ".Rank", "(Removed Rank)");
                            }
                        }
                    }
                }

                for (Permission permission : permissionManagement.getAllServerPerms()) {
                    if (permission.getName().equalsIgnoreCase("yocore.grant." + args[1].toLowerCase()))
                        plugin.getServer().getPluginManager().removePermission(permission);
                }

                plugin.getConfig().set("Ranks." + args[1].toUpperCase(), null);
                plugin.saveConfig();

                plugin.permissionsData.config.set("Ranks." + args[1].toUpperCase(), null);
                plugin.permissionsData.saveData();

                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    for (Team team : player1.getScoreboard().getTeams())
                        player1.getScoreboard().getTeam(team.getName()).unregister();
                }

                plugin.ranks.remove(args[1].toUpperCase());

                for (Player players : Bukkit.getOnlinePlayers())
                    permissionManagement.refreshPlayer(players);

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

                if (!plugin.ranks.contains(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.ColorChanged")
                        .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))
                        .replace("%color%", args[2] + "color")));

                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".Color", args[2]);
                plugin.saveConfig();

                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    for (Team team : player1.getScoreboard().getTeams())
                        player1.getScoreboard().getTeam(team.getName()).unregister();
                }

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

                if (!plugin.ranks.contains(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                if (((Player) sender).getInventory().getItemInHand().getType() == XMaterial.AIR.parseMaterial()) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidItem")));
                    return true;
                }

                ItemStack item = ((Player) sender).getInventory().getItemInHand();
                String name = Utils.getColoredItemData(item, item.getType().toString());

                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".GrantItem", name);
                plugin.saveConfig();

                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    for (Team team : player1.getScoreboard().getTeams())
                        player1.getScoreboard().getTeam(team.getName()).unregister();
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.GrantItemChanged")
                        .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))
                        .replace("%item%", name)));

                break;
            case "index":
            case "tabindex":
                if (args.length != 3) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                    return true;
                }

                if (!plugin.ranks.contains(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.TabIndexChanged")
                        .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".TabIndex"))
                        .replace("%index%", args[2])));

                plugin.getConfig().set("Ranks." + args[1].toUpperCase() + ".TabIndex", args[2]);
                plugin.saveConfig();

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

                if (!plugin.ranks.contains(args[1].toUpperCase())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.InvalidRank")));
                    return true;
                }

                if (!args[2].equalsIgnoreCase("add") && !args[2].equalsIgnoreCase("remove") && !args[2].equalsIgnoreCase("list")) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                    return true;
                }

                switch (args[2].toLowerCase()) {
                    case "add":
                        if (args.length != 4) {
                            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                            return true;
                        }

                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.PermissionAdded")
                                .replace("%permission%", args[3])
                                .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))));

                        permissionManagement.addRankPermission(args[1].toUpperCase(), args[3]);

                        break;
                    case "remove":
                        if (args.length != 4) {
                            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                            return true;
                        }

                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.PermissionRemoved")
                                .replace("%permission%", args[3])
                                .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))));

                        permissionManagement.removeRankPermission(args[1].toUpperCase(), args[3]);

                        break;
                    case "list":
                        if (args.length != 3) {
                            sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.IncorrectUsage")));
                            return true;
                        }

                        List<String> rank_permissions = permissionManagement.getRankPermissions(args[1].toUpperCase());

                        String permissions = "";
                        for (String perm : rank_permissions) {
                            if (permissions.equalsIgnoreCase("")) permissions = "&7 - " + perm;
                            else permissions = permissions + "\n&7 - " + perm;
                        }

                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("RankCommand.RankPermissions")
                                .replace("%permissions%", permissions)
                                .replace("%rank%", plugin.getConfig().getString("Ranks." + args[1].toUpperCase() + ".Display"))));

                        break;
                }

                break;
        }

        return true;
    }
}
