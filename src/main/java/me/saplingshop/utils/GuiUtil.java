package me.saplingshop.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GuiUtil {

    public static ItemStack makeItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static void fillBorder(Inventory inv, Material mat, String colorCode) {
        ItemStack glass = makeItem(mat, colorCode + " ", List.of());
        int size = inv.getSize();
        int rows = size / 9;

        for (int col = 0; col < 9; col++) {
            inv.setItem(col, glass);               // верхний ряд
            inv.setItem(size - 9 + col, glass);   // нижний ряд
        }
        for (int row = 1; row < rows - 1; row++) {
            inv.setItem(row * 9, glass);           // левая колонка
            inv.setItem(row * 9 + 8, glass);      // правая колонка
        }
    }
}
