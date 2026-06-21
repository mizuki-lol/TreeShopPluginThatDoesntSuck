package me.saplingshop.managers;

import me.saplingshop.SaplingShop;
import me.saplingshop.utils.GuiUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ShopSellManager {

    public static final String SELL_TITLE = "§2§l🌿 Магазин продажи";

    // Все виды саженцев в 1.21.1
    public static final Set<Material> SAPLINGS = EnumSet.of(
            Material.OAK_SAPLING,
            Material.SPRUCE_SAPLING,
            Material.BIRCH_SAPLING,
            Material.JUNGLE_SAPLING,
            Material.ACACIA_SAPLING,
            Material.DARK_OAK_SAPLING,
            Material.CHERRY_SAPLING,
            Material.MANGROVE_PROPAGULE,
            Material.BAMBOO_SAPLING
    );

    // Слоты для ручной продажи (ряды 2-4, колонки 1-7)
    public static final int[] SELL_SLOTS = {
            10,11,12,13,14,15,16,
            19,20,21,22,23,24,25,
            28,29,30,31,32,33,34
    };

    private final SaplingShop plugin;

    public ShopSellManager(SaplingShop plugin) {
        this.plugin = plugin;
    }

    public void openShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, SELL_TITLE);

        GuiUtil.fillBorder(inv, Material.GREEN_STAINED_GLASS_PANE, "§a");

        // Информация
        inv.setItem(4, GuiUtil.makeItem(Material.LIME_DYE, "§a§lИнформация", List.of(
                "§7Положите саженцы в слоты ниже",
                "§7или нажмите §e[Продать всё]",
                "",
                "§e1 саженец = §61 монета",
                "",
                "§7Тратить монеты: §b/shopbuy"
        )));

        // Баланс
        long balance = plugin.getCoinManager().getBalance(player);
        inv.setItem(8, GuiUtil.makeItem(Material.NETHER_STAR,
                "§e§lВаш баланс: §6" + balance + " монет", List.of(
                "§7Монеты хранятся на счёте",
                "§7Потратьте их в §b/shopbuy"
        )));

        // Слоты продажи — заглушки
        for (int slot : SELL_SLOTS) {
            inv.setItem(slot, GuiUtil.makeItem(Material.GRAY_STAINED_GLASS_PANE,
                    "§7Слот для саженца", List.of("§7Положите сюда саженец")));
        }

        // Кнопка: продать всё из инвентаря
        inv.setItem(40, GuiUtil.makeItem(Material.GOLD_INGOT,
                "§6§lПродать ВСЁ из инвентаря", List.of(
                "§7Автоматически продаст все саженцы",
                "§7из вашего инвентаря",
                "",
                "§eНажмите!"
        )));

        // Кнопка: продать из слотов выше
        inv.setItem(49, GuiUtil.makeItem(Material.EMERALD,
                "§a§lПродать из слотов выше", List.of(
                "§7Продаёт саженцы из слотов",
                "§7в этом окне",
                "",
                "§eНажмите!"
        )));

        // Закрыть
        inv.setItem(45, GuiUtil.makeItem(Material.BARRIER, "§c§lЗакрыть", List.of("§7Закрыть магазин")));

        player.openInventory(inv);
    }

    public boolean isSapling(Material mat) {
        return SAPLINGS.contains(mat);
    }

    /** Продать все саженцы из инвентаря игрока. Возвращает кол-во монет. */
    public long sellAllFromInventory(Player player) {
        long total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || !isSapling(item.getType())) continue;
            total += item.getAmount();
            item.setAmount(0);
        }
        if (total > 0) plugin.getCoinManager().addCoins(player, total);
        return total;
    }

    /** Продать саженцы из слотов GUI. Возвращает кол-во монет. */
    public long sellFromGuiSlots(Inventory inv, Player player) {
        long total = 0;
        List<ItemStack> nonSaplings = new ArrayList<>();

        for (int slot : SELL_SLOTS) {
            ItemStack item = inv.getItem(slot);
            if (item == null) continue;
            // Пропускаем заглушки (серое стекло)
            if (item.getType() == Material.GRAY_STAINED_GLASS_PANE) continue;

            if (isSapling(item.getType())) {
                total += item.getAmount();
                inv.setItem(slot, null);
            } else {
                nonSaplings.add(item.clone());
                inv.setItem(slot, null);
            }
        }

        if (total > 0) plugin.getCoinManager().addCoins(player, total);

        // Вернуть не-саженцы игроку
        for (ItemStack item : nonSaplings) {
            player.getInventory().addItem(item).values()
                    .forEach(left -> player.getWorld().dropItemNaturally(player.getLocation(), left));
        }

        return total;
    }
}
