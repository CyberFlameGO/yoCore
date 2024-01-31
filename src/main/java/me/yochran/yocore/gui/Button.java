package me.yochran.yocore.gui;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Button {

    private ItemStack item;
    private int amount;
    private Runnable action;
    private String name;
    private List<String> lore;

    public Button(ItemStack item, String name) { this(item, 1, null, name, new ArrayList<>()); }
    public Button(ItemStack item, Runnable action, String name) { this(item, 1, action, name, new ArrayList<>()); }
    public Button(ItemStack item, Runnable action, String name, List<String> lore) { this(item, 1, action, name, lore); }
    public Button(ItemStack item, int amount, Runnable action, String name) { this(item, amount, action, name, new ArrayList<>()); }
    public Button(ItemStack item, String name, List<String> lore) { this(item, 1, null, name, lore); }
    public Button(ItemStack item, int amount, String name, List<String> lore) { this(item, amount, null, name, lore); }
    public Button(ItemStack item, int amount, Runnable action, String name, List<String> lore) {
        this.item = item;
        this.amount = amount;
        this.action = action;
        this.name = name;
        this.lore = lore;
    }

    public ItemStack getItem() { return item; }
    public int getAmount() { return amount; }
    public Runnable getAction() { return action; }
    public String getName() { return name; }
    public List<String> getLore() { return lore; }

    public void setItem(ItemStack item) { this.item = item; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setAction(Runnable action) { this.action = action; }
    public void setName(String name) { this.name = name; }
    public void setLore(List<String> lore) { this.lore = lore; }
}
