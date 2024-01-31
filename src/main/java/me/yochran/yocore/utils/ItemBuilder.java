package me.yochran.yocore.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private ItemStack item;
    private int amount;
    private String name;
    private List<String> lore;

    public ItemBuilder(ItemStack item, int amount, String name, List<String> lore) {
        this.item = item;
        this.amount = amount;
        this.name = name;
        this.lore = lore;
    }

    public ItemStack getItem() { return item; }
    public int getAmount() { return amount; }
    public String getName() { return name; }
    public List<String> getLore() { return lore; }

    public ItemStack getItemStack() {
        ItemStack item = getItem();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(Utils.translate(getName()));
        itemMeta.setLore(translateLore(getLore()));
        item.setItemMeta(itemMeta);

        return item;
    }

    public void setItem(ItemStack item) { this.item = item; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setName(String name) { this.name = name; }
    public void setLore(List<String> lore) { this.lore = lore; }

    public void give(Player player) {
        ItemStack item = getItemStack();

        for (int i = 0; i < getAmount(); i++) player.getInventory().addItem(item);
    }

    public void give(Player player, int slot) {
        ItemStack item = getItemStack();

        player.getInventory().setItem(slot, item);
        if (getAmount() > 1) {
            for (int i = 0; i < getAmount(); i++) player.getInventory().addItem(item);
        }
    }

    public static List<String> translateLore(List<String> lore) {
        List<String> translated = new ArrayList<>();

        for (String entry : lore)
            translated.add(Utils.translate(entry));

        return translated;
    }

    public static List<String> formatLore(String[] lore) {
        return new ArrayList<>(Arrays.asList(lore));
    }
}
