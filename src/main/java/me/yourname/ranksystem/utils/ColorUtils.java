package me.yourname.ranksystem.utils;

import org.bukkit.ChatColor;

public final class ColorUtils {

    private ColorUtils() {}

    public static String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
