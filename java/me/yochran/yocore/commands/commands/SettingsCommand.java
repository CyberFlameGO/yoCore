package me.yochran.yocore.commands;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
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

public class SettingsCommand implements CommandExecutor {

    private final yoCore plugin;

    public SettingsCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("Settings.MustBePlayer")));
            return true;
        }

        Inventory inventory = Bukkit.createInventory((Player) sender, 9, Utils.translate("&aPlayer settings."));

        String tpmStatus;
        if (plugin.message_toggled.contains(((Player) sender).getUniqueId())) tpmStatus = "&cOff";
        else tpmStatus = "&aOn";

        String soundsStatus;
        if (plugin.message_sounds_toggled.contains(((Player) sender).getUniqueId())) soundsStatus = "&cOff";
        else soundsStatus = "&aOn";

        String chatStatus;
        if (plugin.chat_toggled.contains(((Player) sender).getUniqueId())) chatStatus = "&cOff";
        else chatStatus = "&aOn";

        ItemStack tpm = XMaterial.WRITABLE_BOOK.parseItem();
        ItemMeta tpmMeta = tpm.getItemMeta();
        tpmMeta.setDisplayName(Utils.translate("&bPrivate Messages: &3" + tpmStatus));
        List<String> tpmLore = new ArrayList<>();
        tpmLore.add(Utils.translate("&aClick to select."));
        tpmMeta.setLore(tpmLore);
        tpm.setItemMeta(tpmMeta);

        ItemStack sounds = XMaterial.CLOCK.parseItem();
        ItemMeta soundsMeta = sounds.getItemMeta();
        soundsMeta.setDisplayName(Utils.translate("&bMessage Sounds: &3" + soundsStatus));
        List<String> soundsLore = new ArrayList<>();
        soundsLore.add(Utils.translate("&aClick to select."));
        soundsMeta.setLore(soundsLore);
        sounds.setItemMeta(soundsMeta);

        ItemStack chat = XMaterial.PAPER.parseItem();
        ItemMeta chatMeta = chat.getItemMeta();
        chatMeta.setDisplayName(Utils.translate("&bGlobal Chat: &3" + chatStatus));
        List<String> chatLore = new ArrayList<>();
        chatLore.add(Utils.translate("&aClick to select."));
        chatMeta.setLore(chatLore);
        chat.setItemMeta(chatMeta);

        ItemStack scoreboard = XMaterial.PAINTING.parseItem();
        ItemMeta scoreboardMeta = scoreboard.getItemMeta();
        scoreboardMeta.setDisplayName(Utils.translate("&bToggle Scoreboard"));
        List<String> scoreboardLore = new ArrayList<>();
        scoreboardLore.add(Utils.translate("&aClick to select."));
        scoreboardMeta.setLore(scoreboardLore);
        scoreboard.setItemMeta(scoreboardMeta);

        inventory.addItem(tpm);
        inventory.addItem(sounds);
        inventory.addItem(chat);
        inventory.setItem(8, scoreboard);

        ((Player) sender).openInventory(inventory);

        return true;
    }
}
