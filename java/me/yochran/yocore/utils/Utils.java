package me.yochran.yocore.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Optional;

public class Utils {

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static long getDurationMS(String time) {
        long ms = 0;

        if (time.toLowerCase().contains("s"))
            ms = (Long.parseLong(time.replace("s", "")) * 1000) + System.currentTimeMillis();
        if (time.toLowerCase().contains("m") && !time.toLowerCase().contains("o"))
            ms = ((Long.parseLong(time.replace("m", "")) * 1000) * 60) + System.currentTimeMillis();
        if (time.toLowerCase().contains("h"))
            ms = (((Long.parseLong(time.replace("h", "")) * 1000) * 60) * 60) + System.currentTimeMillis();
        if (time.toLowerCase().contains("d"))
            ms = ((((Long.parseLong(time.replace("d", "")) * 1000) * 60) * 60) * 24) + System.currentTimeMillis();
        if (time.toLowerCase().contains("w"))
            ms = (((((Long.parseLong(time.replace("w", "")) * 1000) * 60) * 60) * 24) * 7) + System.currentTimeMillis();
        if (time.toLowerCase().contains("m") && time.toLowerCase().contains("o"))
            ms = (((((Long.parseLong(time.replace("mo", "")) * 1000) * 60) * 60) * 24) * 30) + System.currentTimeMillis();
        if (time.toLowerCase().contains("y"))
            ms = ((((((Long.parseLong(time.replace("y", "")) * 1000) * 60) * 60) * 24) * 7) * 52) + System.currentTimeMillis();

        return ms;
    }

    public static String getDurationString(String time) {
        String str = "1 day";

        if (time.toLowerCase().contains("s")) {
            str = time.replace("s", "") + " second";
            if (Integer.parseInt(time.replace("s", "")) > 1)
                str = str + "s";
        }

        if (time.toLowerCase().contains("m") && !time.toLowerCase().contains("o")) {
            str = time.replace("m", "") + " minute";
            if (Integer.parseInt(time.replace("m", "")) > 1)
                str = str + "s";
        }

        if (time.toLowerCase().contains("h")) {
            str = time.replace("h", "") + " hour";
            if (Integer.parseInt(time.replace("h", "")) > 1)
                str = str + "s";
        }

        if (time.toLowerCase().contains("d")) {
            str = time.replace("d", "") + " day";
            if (Integer.parseInt(time.replace("d", "")) > 1)
                str = str + "s";
        }

        if (time.toLowerCase().contains("w")) {
            str = time.replace("w", "") + " week";
            if (Integer.parseInt(time.replace("w", "")) > 1)
                str = str + "s";
        }

        if (time.toLowerCase().contains("m") && time.toLowerCase().contains("o")) {
            str = time.replace("mo", "") + " month";
            if (Integer.parseInt(time.replace("mo", "")) > 1)
                str = str + "s";
        }

        if (time.toLowerCase().contains("y")) {
            str = time.replace("y", "") + " year";
            if (Integer.parseInt(time.replace("y", "")) > 1)
                str = str + "s";
        }

        return str;
    }

    public static String getExpirationDate(long ms) {
        SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss, z");
        return SDF.format(ms);
    }

    public static ItemStack getMaterialFromConfig(String name) {
        Optional<XMaterial> mat = XMaterial.matchXMaterial(name);
        return mat.map(XMaterial::parseItem).orElse(null);
    }
}
