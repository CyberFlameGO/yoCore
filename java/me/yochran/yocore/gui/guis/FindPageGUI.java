package me.yochran.yocore.gui.guis;

import me.yochran.yocore.gui.Button;
import me.yochran.yocore.gui.CustomGUI;
import me.yochran.yocore.gui.GUI;
import me.yochran.yocore.player.yoPlayer;
import me.yochran.yocore.punishments.PunishmentType;
import me.yochran.yocore.utils.ItemBuilder;
import me.yochran.yocore.utils.XMaterial;
import me.yochran.yocore.yoCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class FindPageGUI extends CustomGUI {

    private final yoCore plugin;

    public FindPageGUI(Player player, int size, String title) {
        super(player, size, title);
        plugin = yoCore.getPlugin(yoCore.class);
    }

    public void setup(String cl, PunishmentType type, OfflinePlayer target, int pages) {
        ItemBuilder itemBuilder = new ItemBuilder(XMaterial.BOOK.parseItem(), 1, "&4&lNULL", new ArrayList<>());
        yoPlayer yoTarget = new yoPlayer(target);

        for (int i = 0; i < pages; i++) {
            int page = i;
            gui.setButton(i, new Button(
                    itemBuilder.getItem(),
                    () -> {
                        GUI.close(gui);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                switch (cl.toLowerCase()) {
                                    case "punishments":
                                        DetailedPunishmentHistoryGUI detailedPunishmentHistoryGUI = new DetailedPunishmentHistoryGUI(getGui().getPlayer(), 27, yoTarget.getDisplayName() + "&a's " + type.toString().toLowerCase() + "&as.");
                                        detailedPunishmentHistoryGUI.setup(type, getGui().getPlayer(), target, (page + 1));
                                        GUI.open(detailedPunishmentHistoryGUI.getGui());
                                        break;
                                    case "grants":
                                        GrantsGUI grantsGUI = new GrantsGUI(getGui().getPlayer(), 18, yoTarget.getDisplayName() + "&a's grant history.");
                                        grantsGUI.setup(getGui().getPlayer(), target, (page + 1));
                                        GUI.open(grantsGUI.getGui());
                                        break;
                                    case "reports":
                                        ReportHistoryGUI reportHistoryGUI = new ReportHistoryGUI(getGui().getPlayer(), 18, yoTarget.getDisplayName() + "&a's report history.");
                                        reportHistoryGUI.setup(target, (page + 1));
                                        GUI.open(reportHistoryGUI.getGui());
                                        break;
                                    case "grant":
                                        GrantGUI grantGUI = new GrantGUI(getGui().getPlayer(), 18, "&aSelect a grant.");
                                        grantGUI.setup(getGui().getPlayer(), target, (page + 1));
                                        GUI.open(grantGUI.getGui());
                                        break;
                                    case "onlineplayers":
                                        OnlinePlayersGUI onlinePlayersGUI = new OnlinePlayersGUI(getGui().getPlayer(), 18, "&aOnline players.");
                                        onlinePlayersGUI.setup(page + 1);
                                        GUI.open(onlinePlayersGUI.getGui());
                                        break;
                                    case "tags":
                                        TagsGUI tagsGUI = new TagsGUI(getGui().getPlayer(), 18, "&aChat tags.");
                                        tagsGUI.setup(page + 1);
                                        GUI.open(tagsGUI.getGui());
                                        break;
                                }
                            }
                        }.runTaskLater(plugin, 1);
                    },
                    "&a&lPage " + (i + 1),
                    itemBuilder.getLore()
            ));
        }
    }
}
