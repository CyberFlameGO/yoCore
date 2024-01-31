package me.yochran.yocore.commands;

import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.punishments.Punishment;
import me.yochran.yocore.punishments.PunishmentType;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XSound;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class MessageCommand implements CommandExecutor {

    private final yoCore plugin;

    public MessageCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Message.MustBePlayer")));
            return true;
        }

        if (plugin.message_toggled.contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Message.MessagesOffSelf")));
            return true;
        }

        if (plugin.muted_players.containsKey(((Player) sender).getUniqueId())) {
            Punishment punishment = null;

            for (Map.Entry<Integer, Punishment> entry : Punishment.getPunishments(yoPlayer.getYoPlayer((Player) sender)).entrySet()) {
                if (entry.getValue().getType() == PunishmentType.MUTE && entry.getValue().getStatus().equalsIgnoreCase("Active"))
                    punishment = entry.getValue();
            }

            if (punishment != null) {
                if (plugin.muted_players.get(((Player) sender).getUniqueId())) {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Temporary.TargetAttemptToSpeak")
                            .replace("%reason%", punishment.getReason())
                            .replace("%expiration%", Utils.getExpirationDate((long) punishment.getDuration()))));
                } else {
                    sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Permanent.TargetAttemptToSpeak")
                            .replace("%reason%", punishment.getReason())));

                }
            }

            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Message.IncorrectUsageMessage")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        yoPlayer yoTarget = new yoPlayer(target);

        if (target == null) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Message.InvalidPlayer")));
            return true;
        }

        if (plugin.message_toggled.contains(target.getUniqueId())) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Message.MessagesOffOther")));
            return true;
        }

        plugin.reply.remove(((Player) sender).getUniqueId());
        plugin.reply.remove(target.getUniqueId());
        plugin.reply.put(((Player) sender).getUniqueId(), target.getUniqueId());
        plugin.reply.put(target.getUniqueId(), ((Player) sender).getUniqueId());

        String message = "";
        for (int i = 1; i < args.length; i++) {
            message = message + args[i] + " ";
        }

        sender.sendMessage(Utils.translate(plugin.getConfig().getString("Message.ExecutorMessage")
                .replace("%target%", yoTarget.getDisplayName())
                .replace("%message%", message)));
        target.sendMessage(Utils.translate(plugin.getConfig().getString("Message.TargetMessage")
                .replace("%player%", yoPlayer.getYoPlayer((Player) sender).getDisplayName())
                .replace("%message%", message)));

        if (!plugin.message_sounds_toggled.contains(target.getUniqueId()))
            target.playSound(target.getLocation(), XSound.ENTITY_ARROW_HIT_PLAYER.parseSound(), 100, (float) 0.1);

        return true;
    }
}
