package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.CustomGUI;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ChatColorGUI extends CustomGUI {

    private final yoCore plugin;

    public ChatColorGUI(Player player, int size, String title) {
        super(player, size, title);
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup() {
        gui.setFiller(36);

        ItemBuilder darkRed = new ItemBuilder(XMaterial.REDSTONE_BLOCK.parseItem(), 1, "&4Dark Red", new ArrayList<>());
        ItemBuilder lightRed = new ItemBuilder(XMaterial.RED_WOOL.parseItem(), 1, "&cLight Red", new ArrayList<>());
        ItemBuilder orange = new ItemBuilder(XMaterial.ORANGE_WOOL.parseItem(), 1, "&6Orange", new ArrayList<>());
        ItemBuilder yellow = new ItemBuilder(XMaterial.YELLOW_WOOL.parseItem(), 1, "&eYellow", new ArrayList<>());
        ItemBuilder lime = new ItemBuilder(XMaterial.LIME_WOOL.parseItem(), 1, "&aLime", new ArrayList<>());
        ItemBuilder green = new ItemBuilder(XMaterial.GREEN_WOOL.parseItem(), 1, "&2Green", new ArrayList<>());
        ItemBuilder aqua = new ItemBuilder(XMaterial.LIGHT_BLUE_WOOL.parseItem(), 1, "&bAqua", new ArrayList<>());
        ItemBuilder blue = new ItemBuilder(XMaterial.BLUE_WOOL.parseItem(), 1, "&9Blue", new ArrayList<>());
        ItemBuilder darkBlue = new ItemBuilder(XMaterial.LAPIS_BLOCK.parseItem(), 1, "&1Dark Blue", new ArrayList<>());
        ItemBuilder purple = new ItemBuilder(XMaterial.PURPLE_WOOL.parseItem(), 1, "&5Purple", new ArrayList<>());
        ItemBuilder pink = new ItemBuilder(XMaterial.PINK_WOOL.parseItem(), 1, "&dPink", new ArrayList<>());
        ItemBuilder white = new ItemBuilder(XMaterial.WHITE_WOOL.parseItem(), 1, "&fWhite", new ArrayList<>());
        ItemBuilder bold = new ItemBuilder(XMaterial.OAK_SIGN.parseItem(), 1, "&f&lBold", new ArrayList<>());
        ItemBuilder italics = new ItemBuilder(XMaterial.OAK_SIGN.parseItem(), 1, "&f&oItalics", new ArrayList<>());

        gui.setButton(10, new Button(darkRed.getItem(), action(player, darkRed), darkRed.getName()));
        gui.setButton(11, new Button(lightRed.getItem(), action(player, lightRed), lightRed.getName()));
        gui.setButton(12, new Button(orange.getItem(), action(player, orange), orange.getName()));
        gui.setButton(13, new Button(yellow.getItem(), action(player, yellow), yellow.getName()));
        gui.setButton(14, new Button(lime.getItem(), action(player, lime), lime.getName()));
        gui.setButton(15, new Button(green.getItem(), action(player, green), green.getName()));
        gui.setButton(16, new Button(aqua.getItem(), action(player, aqua), aqua.getName()));
        gui.setButton(19, new Button(blue.getItem(), action(player, blue), blue.getName()));
        gui.setButton(20, new Button(darkBlue.getItem(), action(player, darkBlue), darkBlue.getName()));
        gui.setButton(21, new Button(purple.getItem(), action(player, purple), purple.getName()));
        gui.setButton(22, new Button(pink.getItem(), action(player, pink), pink.getName()));
        gui.setButton(23, new Button(white.getItem(), action(player, white), white.getName()));
        gui.setButton(24, new Button(bold.getItem(), action(player, bold), bold.getName()));
        gui.setButton(25, new Button(italics.getItem(), action(player, italics), italics.getName()));
    }

    public Runnable action(Player player, ItemBuilder itemBuilder) {
        return () -> {
            GUI.close(gui);
            plugin.chat_color.remove(player.getUniqueId());
            plugin.chat_color.put(player.getUniqueId(), Utils.translate(itemBuilder.getName()));
            player.sendMessage(Utils.translate(plugin.getConfig().getString("ChatColor.SelectedColor")
                    .replace("%color%", itemBuilder.getName())));
        };
    }
}
