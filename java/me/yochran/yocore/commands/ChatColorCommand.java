package me.yochran.yocore.commands;

import me.yochran.yocore.management.PlayerManagement;
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

public class ChatColorCommand implements CommandExecutor {

    private final yoCore plugin;
    private final PlayerManagement playerManagement = new PlayerManagement();

    public ChatColorCommand() {
        plugin = yoCore.getPlugin(yoCore.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ChatColor.MustBePlayer")));
            return true;
        }

        if (!sender.hasPermission("yocore.chatcolor")) {
            sender.sendMessage(Utils.translate(plugin.getConfig().getString("ChatColor.NoPermission")));
            return true;
        }

        openChatColorGUI((Player) sender);

        return true;
    }

    public void openChatColorGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 36, Utils.translate("&aSelect a chat color."));

        ItemStack darkRed = XMaterial.REDSTONE_BLOCK.parseItem();
        ItemStack lightRed = XMaterial.RED_WOOL.parseItem();
        ItemStack orange = XMaterial.ORANGE_WOOL.parseItem();
        ItemStack yellow = XMaterial.YELLOW_WOOL.parseItem();
        ItemStack lime = XMaterial.LIME_WOOL.parseItem();
        ItemStack green = XMaterial.GREEN_WOOL.parseItem();
        ItemStack aqua = XMaterial.LIGHT_BLUE_WOOL.parseItem();
        ItemStack blue = XMaterial.BLUE_WOOL.parseItem();
        ItemStack darkBlue = XMaterial.LAPIS_BLOCK.parseItem();
        ItemStack purple = XMaterial.PURPLE_WOOL.parseItem();
        ItemStack pink = XMaterial.PINK_WOOL.parseItem();
        ItemStack white = XMaterial.WHITE_WOOL.parseItem();
        ItemStack bold = XMaterial.OAK_SIGN.parseItem();
        ItemStack italics = XMaterial.OAK_SIGN.parseItem();

        ItemMeta darkRedMeta = darkRed.getItemMeta();
        ItemMeta lightRedMeta = lightRed.getItemMeta();
        ItemMeta orangeMeta = orange.getItemMeta();
        ItemMeta yellowMeta = yellow.getItemMeta();
        ItemMeta limeMeta = lime.getItemMeta();
        ItemMeta greenMeta = green.getItemMeta();
        ItemMeta aquaMeta = aqua.getItemMeta();
        ItemMeta blueMeta = blue.getItemMeta();
        ItemMeta darkBlueMeta = darkBlue.getItemMeta();
        ItemMeta purpleMeta = purple.getItemMeta();
        ItemMeta pinkMeta = pink.getItemMeta();
        ItemMeta whiteMeta = white.getItemMeta();
        ItemMeta boldMeta = bold.getItemMeta();
        ItemMeta italicsMeta = italics.getItemMeta();

        darkRedMeta.setDisplayName(Utils.translate("&4Dark Red"));
        lightRedMeta.setDisplayName(Utils.translate("&cLight Red"));
        orangeMeta.setDisplayName(Utils.translate("&6Orange"));
        yellowMeta.setDisplayName(Utils.translate("&eYellow"));
        limeMeta.setDisplayName(Utils.translate("&aLime"));
        greenMeta.setDisplayName(Utils.translate("&2Green"));
        aquaMeta.setDisplayName(Utils.translate("&bAqua"));
        blueMeta.setDisplayName(Utils.translate("&9Blue"));
        darkBlueMeta.setDisplayName(Utils.translate("&1Dark Blue"));
        purpleMeta.setDisplayName(Utils.translate("&5Purple"));
        pinkMeta.setDisplayName(Utils.translate("&dPink"));
        whiteMeta.setDisplayName(Utils.translate("&fWhite"));
        boldMeta.setDisplayName(Utils.translate("&lBold"));
        italicsMeta.setDisplayName(Utils.translate("&oItalics"));

        darkRed.setItemMeta(darkRedMeta);
        lightRed.setItemMeta(lightRedMeta);
        orange.setItemMeta(orangeMeta);
        yellow.setItemMeta(yellowMeta);
        lime.setItemMeta(limeMeta);
        green.setItemMeta(greenMeta);
        aqua.setItemMeta(aquaMeta);
        blue.setItemMeta(blueMeta);
        darkBlue.setItemMeta(darkBlueMeta);
        purple.setItemMeta(purpleMeta);
        pink.setItemMeta(pinkMeta);
        white.setItemMeta(whiteMeta);
        bold.setItemMeta(boldMeta);
        italics.setItemMeta(italicsMeta);

        inventory.setItem(10, darkRed);
        inventory.setItem(11, lightRed);
        inventory.setItem(12, orange);
        inventory.setItem(13, yellow);
        inventory.setItem(14, lime);
        inventory.setItem(15, green);
        inventory.setItem(16, aqua);
        inventory.setItem(19, blue);
        inventory.setItem(20, darkBlue);
        inventory.setItem(21, purple);
        inventory.setItem(22, pink);
        inventory.setItem(23, white);
        inventory.setItem(24, bold);
        inventory.setItem(25, italics);

        player.openInventory(inventory);
    }
}
