package me.yochran.yocore.commands.staff;

import me.yochran.yocore.management.PlayerManagement;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class ModmodeCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public ModmodeCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Modmode.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.modmode")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Modmode.NoPermission")));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Modmode.IncorrectUsage")));
            return true;
        }

        if (args.length == 0) {
            if (!plugin.modmode_players.contains(((Player) sender).getUniqueId())) {
                plugin.modmode_players.add(((Player) sender).getUniqueId());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Modmode.TargetMessageOn")));

                enterModmode((Player) sender);

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.chats.staff") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.ModmodeOnSelf")
                                .replace("%player%", playerManagement.getPlayerColor((Player) sender))));
                }
            } else {
                plugin.modmode_players.remove(((Player) sender).getUniqueId());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Modmode.TargetMessageOff")));

                ((Player) sender).setAllowFlight(false);
                ((Player) sender).setFlying(false);

                ((Player) sender).getInventory().clear();

                ((Player) sender).getInventory().setContents(plugin.inventory_contents.get(((Player) sender).getUniqueId()));
                ((Player) sender).getInventory().setArmorContents(plugin.armor_contents.get(((Player) sender).getUniqueId()));

                ((Player) sender).updateInventory();

                plugin.inventory_contents.remove(((Player) sender).getUniqueId());
                plugin.armor_contents.remove(((Player) sender).getUniqueId());

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.chats.staff") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.ModmodeOffSelf")
                                .replace("%player%", playerManagement.getPlayerColor((Player) sender))));
                }
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Modmode.InvalidPlayer")));
                return true;
            }

            if (!plugin.modmode_players.contains(target.getUniqueId())) {
                plugin.modmode_players.add(target.getUniqueId());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Modmode.ExecutorMessageOn")
                        .replace("%target%", playerManagement.getPlayerColor(target))));
                target.sendMessage(Utils.translate(plugin.getConfig().getString("Modmode.TargetMessageOn")));

                enterModmode(target);

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.chats.staff") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.ModmodeOnOther")
                                .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                                .replace("%target%", playerManagement.getPlayerColor(target))));
                }
            } else {
                plugin.modmode_players.remove(target.getUniqueId());

                sender.sendMessage(Utils.translate(plugin.getConfig().getString("Modmode.ExecutorMessageOff")
                        .replace("%target%", playerManagement.getPlayerColor(target))));
                target.sendMessage(Utils.translate(plugin.getConfig().getString("Modmode.TargetMessageOff")));

                target.setAllowFlight(false);
                target.setFlying(false);

                target.getInventory().clear();

                target.getInventory().setContents(plugin.inventory_contents.get(target.getUniqueId()));
                target.getInventory().setArmorContents(plugin.armor_contents.get(target.getUniqueId()));

                target.updateInventory();

                plugin.inventory_contents.remove(target.getUniqueId());
                plugin.armor_contents.remove(target.getUniqueId());

                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (staff.hasPermission("yocore.chats.staff") && plugin.staff_alerts.contains(staff.getUniqueId()))
                        staff.sendMessage(Utils.translate(plugin.getConfig().getString("StaffAlerts.ModmodeOffOther")
                                .replace("%player%", playerManagement.getPlayerColor((Player) sender))
                                .replace("%target%", playerManagement.getPlayerColor(target))));
                }
            }
        }

        return true;
    }

    public void enterModmode(Player player) {
        player.setAllowFlight(true);
        player.setFlying(true);

        plugin.inventory_contents.put(player.getUniqueId(), player.getInventory().getContents());
        plugin.armor_contents.put(player.getUniqueId(), player.getInventory().getArmorContents());

        player.getInventory().clear();
        player.getInventory().setHelmet(XMaterial.AIR.parseItem());
        player.getInventory().setChestplate(XMaterial.AIR.parseItem());
        player.getInventory().setLeggings(XMaterial.AIR.parseItem());
        player.getInventory().setBoots(XMaterial.AIR.parseItem());

        ItemStack launcher = XMaterial.COMPASS.parseItem();
        ItemMeta launcherMeta = launcher.getItemMeta();
        launcherMeta.setDisplayName(Utils.translate("&6&lLauncher"));
        launcher.setItemMeta(launcherMeta);

        ItemStack freeze = XMaterial.PACKED_ICE.parseItem();
        ItemMeta freezeMeta = freeze.getItemMeta();
        freezeMeta.setDisplayName(Utils.translate("&6&lFreeze"));
        freeze.setItemMeta(freezeMeta);

        ItemStack vanish = XMaterial.BEDROCK.parseItem();
        ItemMeta vanishMeta = vanish.getItemMeta();
        if (plugin.vanished_players.contains(player.getUniqueId())) {
            vanish = XMaterial.LIME_DYE.parseItem();
            vanishMeta.setDisplayName(Utils.translate("&a&lBecome Visible"));
        } else {
            vanish = XMaterial.GRAY_DYE.parseItem();
            vanishMeta.setDisplayName(Utils.translate("&7&lBecome Invisible"));
        }
        vanish.setItemMeta(vanishMeta);

        ItemStack onlinePlayers = XMaterial.PLAYER_HEAD.parseItem();
        ItemMeta onlinePlayersMeta = onlinePlayers.getItemMeta();
        onlinePlayersMeta.setDisplayName(Utils.translate("&6&lOnline Players"));
        onlinePlayers.setItemMeta(onlinePlayersMeta);

        player.getInventory().setItem(0, launcher);
        player.getInventory().setItem(1, freeze);
        player.getInventory().setItem(7, vanish);
        player.getInventory().setItem(8, onlinePlayers);

        player.setHealth(20);
        player.setFoodLevel(20);

        player.updateInventory();

        new BukkitRunnable() {
            public void run() { player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); }
        }.runTaskLater(plugin, 0);
    }
}
