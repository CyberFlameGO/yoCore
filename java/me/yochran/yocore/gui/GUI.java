package me.yochran.yocore.gui;

import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class GUI {

    private Player player;
    private Inventory inventory;

    public static Map<Player, GUI> open_guis = new HashMap<>();
    public static Map<Integer, Button> buttons = new HashMap<>();

    public GUI(Player player, int size, String title) {
        this.player = player;
        this.inventory = Bukkit.createInventory(null, size, Utils.translate(title));
    }

    public void open() {
        open_guis.put(player, this);
        player.openInventory(inventory);
    }

    public void close() {
        open_guis.remove(player);
        player.closeInventory();
    }

    public Player getPlayer() { return player; }

    public Inventory getInventory() { return inventory; }

    public void setButton(int slot, Button button) {
        ItemStack item = button.getItem();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Utils.translate(button.getName()));
        itemMeta.setLore(button.getLore());
        item.setItemMeta(itemMeta);

        buttons.put(slot, button);

        inventory.setItem(slot, item);
        if (button.getAmount() > 1) {
            for (int i = 1; i < button.getAmount(); i++)
                inventory.addItem(item);
        }
    }

    public Button getButton(int slot) { return buttons.get(slot); }

    public void setFiller(int slots) {
        ItemStack filler = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(Utils.translate("&7 "));
        fillerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < slots; i++) this.inventory.setItem(i, filler);
    }
}
