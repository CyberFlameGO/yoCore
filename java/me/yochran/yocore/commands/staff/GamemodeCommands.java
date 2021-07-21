package me.yochran.yocore.commands.staff;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommands implements CommandExecutor {

    private final yoCore plugin;

    public GamemodeCommands() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.gamemode")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.NoPermission")));
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "gamemode":
                if (args.length < 1 || args.length > 2) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.IncorrectUsage")));
                    return true;
                }

                if (!args[0].equalsIgnoreCase("creative")
                        && !args[0].equalsIgnoreCase("survival")
                        && !args[0].equalsIgnoreCase("spectator")
                        && !args[0].equalsIgnoreCase("adventure")
                        && !args[0].equalsIgnoreCase("c")
                        && !args[0].equalsIgnoreCase("s")
                        && !args[0].equalsIgnoreCase("a")
                        && !args[0].equalsIgnoreCase("sp")
                        && !args[0].equalsIgnoreCase("0")
                        && !args[0].equalsIgnoreCase("1")
                        && !args[0].equalsIgnoreCase("2")
                        && !args[0].equalsIgnoreCase("3")) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.InvalidGamemode")));
                    return true;
                }

                Player target;
                if (args.length == 1) target = (Player) sender;
                else {
                    target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.InvalidPlayer")));
                        return true;
                    }
                }

                yoPlayer yoTarget = new yoPlayer(target);

                switch (args[0].toLowerCase()) {
                    case "creative":
                    case "c":
                    case "1":
                        target.setGameMode(GameMode.CREATIVE);
                        target.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.TargetMessage")
                                .replace("%gamemode%", "creative")));

                        if (target != sender) {
                            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.ExecutorMessage")
                                    .replace("%gamemode%", "creative")
                                    .replace("%target%", yoTarget.getDisplayName())));
                        }

                        break;
                    case "survival":
                    case "s":
                    case "0":
                        target.setGameMode(GameMode.SURVIVAL);
                        target.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.TargetMessage")
                                .replace("%gamemode%", "survival")));

                        if (target != sender) {
                            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.ExecutorMessage")
                                    .replace("%gamemode%", "survival")
                                    .replace("%target%", yoTarget.getDisplayName())));
                        }

                        break;
                    case "adventure":
                    case "a":
                    case "3":
                        target.setGameMode(GameMode.ADVENTURE);
                        target.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.TargetMessage")
                                .replace("%gamemode%", "adventure")));

                        if (target != sender) {
                            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.ExecutorMessage")
                                    .replace("%gamemode%", "adventure")
                                    .replace("%target%", yoTarget.getDisplayName())));
                        }

                        break;
                    case "spectator":
                    case "sp":
                    case "2":
                        target.setGameMode(GameMode.SPECTATOR);
                        target.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.TargetMessage")
                                .replace("%gamemode%", "spectator")));

                        if (target != sender) {
                            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.ExecutorMessage")
                                    .replace("%gamemode%", "spectator")
                                    .replace("%target%", yoTarget.getDisplayName())));
                        }

                        break;
                }

                return true;

            case "gmc":
                if (args.length > 1) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.IncorrectUsage")));
                     return true;
                }

                if (args.length == 0) {
                    ((Player) sender).setGameMode(GameMode.CREATIVE);
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.TargetMessage")
                            .replace("%gamemode%", "creative")));
                } else {
                    if (Bukkit.getPlayer(args[0]) == null) {
                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.InvalidPlayer")));
                        return true;
                    }

                    Bukkit.getPlayer(args[0]).setGameMode(GameMode.CREATIVE);
                    Bukkit.getPlayer(args[0]).sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.TargetMessage")
                            .replace("%gamemode%", "creative")));
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.ExecutorMessage")
                            .replace("%gamemode%", "creative")
                            .replace("%target%", yoPlayer.getYoPlayer(Bukkit.getPlayer(args[0])).getDisplayName())));
                }

                break;
            case "gms":
                if (args.length > 1) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.IncorrectUsage")));
                    return true;
                }

                if (args.length == 0) {
                    ((Player) sender).setGameMode(GameMode.SURVIVAL);
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.TargetMessage")
                            .replace("%gamemode%", "survival")));
                } else {
                    if (Bukkit.getPlayer(args[0]) == null) {
                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.InvalidPlayer")));
                        return true;
                    }

                    Bukkit.getPlayer(args[0]).setGameMode(GameMode.SURVIVAL);
                    Bukkit.getPlayer(args[0]).sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.TargetMessage")
                            .replace("%gamemode%", "survival")));
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.ExecutorMessage")
                            .replace("%gamemode%", "survival")
                            .replace("%target%", yoPlayer.getYoPlayer(Bukkit.getPlayer(args[0])).getDisplayName())));
                }

                break;
            case "gmsp":
                if (args.length > 1) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.IncorrectUsage")));
                    return true;
                }

                if (args.length == 0) {
                    ((Player) sender).setGameMode(GameMode.SPECTATOR);
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.TargetMessage")
                            .replace("%gamemode%", "spectator")));
                } else {
                    if (Bukkit.getPlayer(args[0]) == null) {
                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.InvalidPlayer")));
                        return true;
                    }

                    Bukkit.getPlayer(args[0]).setGameMode(GameMode.SPECTATOR);
                    Bukkit.getPlayer(args[0]).sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.TargetMessage")
                            .replace("%gamemode%", "spectator")));
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.ExecutorMessage")
                            .replace("%gamemode%", "spectator")
                            .replace("%target%", yoPlayer.getYoPlayer(Bukkit.getPlayer(args[0])).getDisplayName())));
                }

                break;
            case "gma":
                if (args.length > 1) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.IncorrectUsage")));
                    return true;
                }

                if (args.length == 0) {
                    ((Player) sender).setGameMode(GameMode.ADVENTURE);
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.TargetMessage")
                            .replace("%gamemode%", "adventure")));
                } else {
                    if (Bukkit.getPlayer(args[0]) == null) {
                        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.InvalidPlayer")));
                        return true;
                    }

                    Bukkit.getPlayer(args[0]).setGameMode(GameMode.ADVENTURE);
                    Bukkit.getPlayer(args[0]).sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.TargetMessage")
                            .replace("%gamemode%", "adventure")));
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Gamemode.ExecutorMessage")
                            .replace("%gamemode%", "adventure")
                            .replace("%target%", yoPlayer.getYoPlayer(Bukkit.getPlayer(args[0])).getDisplayName())));
                }

                break;
        }

        return true;
    }
}
