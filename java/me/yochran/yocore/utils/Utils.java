package me.yochran.yocore.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;

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

    public static String getColoredItemData(ItemStack item, String name) {
        switch (item.getType()) {
            case WHITE_WOOL: name = "WHITE_WOOL"; break;
            case RED_WOOL: name = "RED_WOOL"; break;
            case ORANGE_WOOL: name = "ORANGE_WOOL"; break;
            case YELLOW_WOOL: name = "YELLOW_WOOL"; break;
            case LIME_WOOL: name = "LIME_WOOL"; break;
            case GREEN_WOOL: name = "GREEN_WOOL"; break;
            case LIGHT_BLUE_WOOL: name = "LIGHT_BLUE_WOOL"; break;
            case BLUE_WOOL: name = "BLUE_WOOL"; break;
            case PURPLE_WOOL: name = "PURPLE_WOOL"; break;
            case PINK_WOOL: name = "PINK_WOOL"; break;
            case BROWN_WOOL: name = "BROWN_WOOL"; break;
            case CYAN_WOOL: name = "CYAN_WOOL"; break;
            case LIGHT_GRAY_WOOL: name = "LIGHT_GRAY_WOOL"; break;
            case GRAY_WOOL: name = "GRAY_WOOL"; break;
            case BLACK_WOOL: name = "BLACK_WOOL"; break;
            case MAGENTA_WOOL: name = "MAGENTA_WOOL"; break;

            case WHITE_TERRACOTTA: name = "WHITE_TERRACOTTA"; break;
            case RED_TERRACOTTA: name = "RED_TERRACOTTA"; break;
            case ORANGE_TERRACOTTA: name = "ORANGE_TERRACOTTA"; break;
            case YELLOW_TERRACOTTA: name = "YELLOW_TERRACOTTA"; break;
            case LIME_TERRACOTTA: name = "LIME_TERRACOTTA"; break;
            case GREEN_TERRACOTTA: name = "GREEN_TERRACOTTA"; break;
            case LIGHT_BLUE_TERRACOTTA: name = "LIGHT_BLUE_TERRACOTTA"; break;
            case BLUE_TERRACOTTA: name = "BLUE_TERRACOTTA"; break;
            case PURPLE_TERRACOTTA: name = "PURPLE_TERRACOTTA"; break;
            case PINK_TERRACOTTA: name = "PINK_TERRACOTTA"; break;
            case BROWN_TERRACOTTA: name = "BROWN_TERRACOTTA"; break;
            case CYAN_TERRACOTTA: name = "CYAN_TERRACOTTA"; break;
            case LIGHT_GRAY_TERRACOTTA: name = "LIGHT_GRAY_TERRACOTTA"; break;
            case GRAY_TERRACOTTA: name = "GRAY_TERRACOTTA"; break;
            case BLACK_TERRACOTTA: name = "BLACK_TERRACOTTA"; break;
            case MAGENTA_TERRACOTTA: name = "MAGENTA_TERRACOTTA"; break;
        }

        return name;
    }

    public static String capitalizeFirst(String str) {
        String[] words = str.split(" ");
        String capitalizedWord = "";
        for(String character : words){
            String first = character.substring(0,1);
            String afterfirst = character.substring(1);
            capitalizedWord += first.toUpperCase() + afterfirst + " ";
        }
        return capitalizedWord.trim();
    }

    public static int[] getHistorySlotData(int id) {
        int[] data = new int[] { 1, 0 };

        if (id >= 0 && id < 9) data = new int[] { 1, id };
        if (id >= 9 && id < 18) data = new int[] { 2, (id - 9) };
        if (id >= 18 && id < 27) data = new int[] { 3, (id - 18) };;
        if (id >= 27 && id < 36) data = new int[] { 4, (id - 27) };
        if (id >= 36 && id < 45) data = new int[] { 5, (id - 36) };
        if (id >= 45 && id < 54) data = new int[] { 6, (id - 45) };
        if (id >= 54 && id < 63) data = new int[] { 7, (id - 54) };
        if (id >= 63 && id < 72) data = new int[] { 8, (id - 63) };
        if (id >= 72 && id < 81) data = new int[] { 9, (id - 72) };

        if (id >= 81) {
            System.out.println("Too many pages!");
            return null;
        }

        return data;
    }
}
