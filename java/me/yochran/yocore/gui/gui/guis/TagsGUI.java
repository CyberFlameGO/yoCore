package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.CustomGUI;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.Utils;
import me.yochran.yocore.yoCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TagsGUI extends CustomGUI {

    private final yoCore plugin;

    public TagsGUI(Player player, int size, String title) {
        super(player, size, title);
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup() {
        int loop = -1;
        for (String tag : plugin.getConfig().getConfigurationSection("Tags").getKeys(false)) {
            loop++;
            ItemBuilder itemBuilder = new ItemBuilder(
                    Utils.getMaterialFromConfig(plugin.getConfig().getString("TagsCommand.TagItem")),
                    1,
                    plugin.getConfig().getString("Tags." + tag + ".Display"),
                    ItemBuilder.formatLore(new String[] {
                            "&7&m--------------------",
                            "&eTag: &f" + plugin.getConfig().getString("Tags." + tag + ".ID"),
                            "&ePrefix: &f" + plugin.getConfig().getString("Tags." + tag + ".Prefix"),
                            "&eDisplay: &f" + plugin.getConfig().getString("Tags." + tag + ".Display"),
                            "&7&m--------------------"
                    })
            );

            if (player.hasPermission(plugin.getConfig().getString("Tags." + tag + ".Permission")))
                itemBuilder.getLore().add(Utils.translate("&aClick to select this tag."));
            else itemBuilder.getLore().add(Utils.translate("&cYou cannot use this tag."));

            gui.setButton(loop, new Button(
                    itemBuilder.getItem(),
                    () -> {
                        GUI.close(gui);

                        if (itemBuilder.getLore().contains(Utils.translate("&aClick to select this tag."))) {
                            plugin.tag.put(gui.getPlayer().getUniqueId(), plugin.getConfig().getString("Tags." + tag + ".ID"));

                            gui.getPlayer().sendMessage(Utils.translate(plugin.getConfig().getString("TagsCommand.FormatOn")
                                    .replace("%tag%", plugin.getConfig().getString("Tags." + tag + ".Display"))));
                        }
                    },
                    itemBuilder.getName(),
                    itemBuilder.getLore()
            ));
        }
    }
}
