package me.saplingshop.utils;

import org.bukkit.ChatColor;

public class MessageUtil {

    public static final String PREFIX = color("&2[&a🌿 SaplingShop&2] &r");

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String prefixed(String message) {
        return PREFIX + color(message);
    }
}
