package me.yochran.yocore.commands;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.management.PunishmentManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XSound;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class MessageCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();
    private final PunishmentManagement punishmentManagement = new PunishmentManagement();

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
            if (plugin.muted_players.get(((Player) sender).getUniqueId())) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Temporary.TargetAttemptToSpeak")
                        .replace("%reason%", plugin.punishmentData.config.getString(((Player) sender).getUniqueId().toString() + ".Mute." + punishmentManagement.getInfractionAmount(((Player) sender), "Mute") + ".Reason"))
                        .replace("%expiration%", Utils.getExpirationDate(plugin.punishmentData.config.getLong(((Player) sender).getUniqueId().toString() + ".Mute." + punishmentManagement.getInfractionAmount(((Player) sender), "Mute") + ".Duration")))));
            } else {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Mute.Permanent.TargetAttemptToSpeak")
                        .replace("%reason%", plugin.punishmentData.config.getString(((Player) sender).getUniqueId().toString() + ".Mute." + punishmentManagement.getInfractionAmount(((Player) sender), "Mute") + ".Reason"))));
            }
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Message.IncorrectUsageMessage")));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
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
                .replace("%target%", playerManagement.getPlayerColor(target))
                .replace("%message%", message)));
        target.sendMessage(Utils.translate(plugin.getConfig().getString("Message.TargetMessage")
                .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                .replace("%message%", message)));

        if (!plugin.message_sounds_toggled.contains(target.getUniqueId()))
            target.playSound(target.getLocation(), XSound.ENTITY_ARROW_HIT_PLAYER.parseSound(), 100, (float) 0.1);

        return true;
    }
}
