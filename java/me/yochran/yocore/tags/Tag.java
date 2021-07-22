package me.yochran.yocore.tags;

import me.yochran.yocore.yoCore;

import java.util.HashMap;
import java.util.Map;

public class Tag {

    private final yoCore plugin = yoCore.getInstance();

    private String ID;
    private String prefix;
    private String display;

    private static final Map<String, Tag> tags;

    static {
        tags = new HashMap<>();
    }

    public Tag(String ID, String prefix, String display) {
        this.ID = ID;
        this.prefix = prefix;
        this.display = display;
    }

    public static Map<String, Tag> getTags() { return tags; }

    public String getID() { return ID; }
    public String getPrefix() { return prefix; }
    public String getDisplay() { return display; }
    public String getPermission() { return "yocore.tags." + getID().toLowerCase(); }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        plugin.getConfig().set("Tags." + getID() + ".Prefix", getPrefix());
        plugin.saveConfig();
    }

    public void setDisplay(String display) {
        this.display = display;
        plugin.getConfig().set("Tags." + getID() + ".Display", getDisplay());
        plugin.saveConfig();
    }

    public void create() {
        plugin.getConfig().set("Tags." + getID() + ".ID", getID());
        plugin.getConfig().set("Tags." + getID() + ".Prefix", getPrefix());
        plugin.getConfig().set("Tags." + getID() + ".Display", getDisplay());
        plugin.saveConfig();

        getTags().put(getID(), this);
    }

    public void delete() {
        plugin.getConfig().set("Tags." + getID(), null);
        plugin.saveConfig();

        getTags().remove(getID());
    }

    public static Tag getTag(String name) {
        return getTags().get(name.toUpperCase());
    }

}
