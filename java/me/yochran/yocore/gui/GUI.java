package me.yochran.yocore.gui;

import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GUI {

    private Player player;
    private Inventory inventory;

    private static Map<Player, GUI> open_guis;
    protected Map<Integer, Button> buttons;

    static {
        open_guis = new HashMap<>();
    }

    public GUI(Player player, int size, String title) {
        this.player = player;

        this.inventory = Bukkit.createInventory(null, size, Utils.translate(title));
        this.buttons = new HashMap<>();
    }

    public static void open(GUI gui) {
        open_guis.put(gui.getPlayer(), gui);
        gui.getPlayer().openInventory(gui.getInventory());
    }

    public static void close(GUI gui) {
        open_guis.remove(gui.getPlayer());
        gui.getPlayer().closeInventory();
    }

    public Player getPlayer() { return player; }
    public Inventory getInventory() { return inventory; }

    public void setButton(int slot, Button button) {
        buttons.put(slot, button);
        ItemBuilder itemBuilder = new ItemBuilder(button.getItem(), button.getAmount(), button.getName(), button.getLore());

        inventory.setItem(slot, itemBuilder.getItemStack());
        if (button.getAmount() > 1) {
            for (int i = 1; i < button.getAmount(); i++)
                inventory.addItem(itemBuilder.getItemStack());
        }
    }

    public Button getButton(int slot) { return buttons.get(slot); }

    public void setFiller(int slots) {
        ItemBuilder itemBuilder = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), 1, "&7 ", new ArrayList<>());
        for (int i = 0; i < slots; i++) this.inventory.setItem(i, itemBuilder.getItemStack());
    }

    public boolean isSlotEmpty(int slot) {
        return inventory.getItem(slot) == null;
    }

    public void setFiller(int[] slots) {
        ItemBuilder itemBuilder = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(), 1, "&7 ", new ArrayList<>());
        for (int i = 0; i < slots.length; i++) this.inventory.setItem(slots[i], itemBuilder.getItemStack());
    }

    public void setFiller(int[] slots, ItemStack fillerItem) {
        ItemBuilder itemBuilder = new ItemBuilder(fillerItem, 1, "&7 ", new ArrayList<>());
        for (int i = 0; i < slots.length; i++) this.inventory.setItem(slots[i], itemBuilder.getItemStack());
    }

    public static Map<Player, GUI> getOpenGUIs() { return open_guis; }
}
