package me.saplingshop.managers;

import me.saplingshop.SaplingShop;
import me.saplingshop.utils.GuiUtil;
import me.saplingshop.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

public class ShopBuyManager {

    public static final String BUY_TITLE_ORES     = "§9§l⚒ Магазин — Руды";
    public static final String BUY_TITLE_ENCHANTS = "§5§l✦ Магазин — Зачарования";
    public static final String BUY_TITLE_DECO     = "§6§l✦ Магазин — Украшения";

    // ─── Записи каталога ──────────────────────────────────────────

    public record ShopEntry(Material material, String displayName, int price, int amount, List<String> description) {}
    public record EnchantEntry(Enchantment ench, int level, int price) {}

    // ─── Руды ─────────────────────────────────────────────────────

    private static final List<ShopEntry> ORES = List.of(
            new ShopEntry(Material.COAL,              "§8Уголь",               1,  4,  List.of("§7x4 угля")),
            new ShopEntry(Material.IRON_INGOT,        "§fЖелезный слиток",     2,  1,  List.of("§7x1 железный слиток")),
            new ShopEntry(Material.GOLD_INGOT,        "§6Золотой слиток",      3,  1,  List.of("§7x1 золотой слиток")),
            new ShopEntry(Material.COPPER_INGOT,      "§cМедный слиток",       1,  2,  List.of("§7x2 медных слитка")),
            new ShopEntry(Material.LAPIS_LAZULI,      "§1Лазурит",             2,  4,  List.of("§7x4 лазурита")),
            new ShopEntry(Material.QUARTZ,            "§fКварц",               1,  4,  List.of("§7x4 кварца")),
            new ShopEntry(Material.REDSTONE,          "§cРедстоун",            1,  4,  List.of("§7x4 редстоуна")),
            new ShopEntry(Material.EMERALD,           "§aИзумруд",             5,  1,  List.of("§7x1 изумруд")),
            new ShopEntry(Material.DIAMOND,           "§bАлмаз",               10, 1,  List.of("§7x1 алмаз")),
            new ShopEntry(Material.AMETHYST_SHARD,    "§dАметист",             2,  2,  List.of("§7x2 осколка аметиста")),
            new ShopEntry(Material.RAW_IRON,          "§fСырое железо",        1,  2,  List.of("§7x2 сырого железа")),
            new ShopEntry(Material.RAW_GOLD,          "§6Сырое золото",        2,  2,  List.of("§7x2 сырого золота")),
            new ShopEntry(Material.RAW_COPPER,        "§cСырая медь",          1,  3,  List.of("§7x3 сырой меди")),
            new ShopEntry(Material.COAL_ORE,          "§8Угольная руда",       2,  1,  List.of("§7x1 угольная руда")),
            new ShopEntry(Material.IRON_ORE,          "§fЖелезная руда",       3,  1,  List.of("§7x1 железная руда")),
            new ShopEntry(Material.GOLD_ORE,          "§6Золотая руда",        4,  1,  List.of("§7x1 золотая руда")),
            new ShopEntry(Material.DIAMOND_ORE,       "§bАлмазная руда",       15, 1,  List.of("§7x1 алмазная руда")),
            new ShopEntry(Material.EMERALD_ORE,       "§aИзумрудная руда",     8,  1,  List.of("§7x1 изумрудная руда"))
    );

    // ─── Украшения / наковальня ───────────────────────────────────

    private static final List<ShopEntry> DECO = List.of(
            new ShopEntry(Material.ANVIL,                  "§8Наковальня",                 20, 1, List.of("§7Используется для зачарований и ремонта")),
            new ShopEntry(Material.CHIPPED_ANVIL,          "§8Треснутая наковальня",        12, 1, List.of("§7Слегка повреждённая")),
            new ShopEntry(Material.DAMAGED_ANVIL,          "§8Сильно повреждённая наков.",   7, 1, List.of("§7Почти сломана")),
            new ShopEntry(Material.GOLDEN_HELMET,          "§6Золотой шлем",                 8, 1, List.of("§7Украшение для головы")),
            new ShopEntry(Material.GOLDEN_CHESTPLATE,      "§6Золотой нагрудник",           12, 1, List.of("§7Украшение торса")),
            new ShopEntry(Material.GOLDEN_LEGGINGS,        "§6Золотые поножи",              10, 1, List.of("§7Украшение ног")),
            new ShopEntry(Material.GOLDEN_BOOTS,           "§6Золотые сапоги",               6, 1, List.of("§7Украшение для ног")),
            new ShopEntry(Material.GOLDEN_APPLE,           "§6Золотое яблоко",              15, 1, List.of("§7Восстанавливает здоровье")),
            new ShopEntry(Material.ENCHANTED_GOLDEN_APPLE, "§6§lЗаворожённое яблоко",       80, 1, List.of("§7Даёт мощные эффекты")),
            new ShopEntry(Material.GOLD_NUGGET,            "§6Самородок золота",             1, 4, List.of("§7x4 самородка")),
            new ShopEntry(Material.GOLD_BLOCK,             "§6Золотой блок",                25, 1, List.of("§7Декоративный блок"))
    );

    // ─── Зачарования ──────────────────────────────────────────────

    private static final List<EnchantEntry> ENCHANTS = List.of(
            new EnchantEntry(Enchantment.PROTECTION,            4, 30),
            new EnchantEntry(Enchantment.FIRE_PROTECTION,       4, 20),
            new EnchantEntry(Enchantment.BLAST_PROTECTION,      4, 20),
            new EnchantEntry(Enchantment.PROJECTILE_PROTECTION, 4, 20),
            new EnchantEntry(Enchantment.FEATHER_FALLING,       4, 15),
            new EnchantEntry(Enchantment.THORNS,                3, 25),
            new EnchantEntry(Enchantment.RESPIRATION,           3, 20),
            new EnchantEntry(Enchantment.AQUA_AFFINITY,         1, 15),
            new EnchantEntry(Enchantment.DEPTH_STRIDER,         3, 20),
            new EnchantEntry(Enchantment.FROST_WALKER,          2, 20),
            new EnchantEntry(Enchantment.SOUL_SPEED,            3, 25),
            new EnchantEntry(Enchantment.SWIFT_SNEAK,           3, 30),
            new EnchantEntry(Enchantment.SHARPNESS,             5, 30),
            new EnchantEntry(Enchantment.SMITE,                 5, 20),
            new EnchantEntry(Enchantment.BANE_OF_ARTHROPODS,    5, 20),
            new EnchantEntry(Enchantment.KNOCKBACK,             2, 15),
            new EnchantEntry(Enchantment.FIRE_ASPECT,           2, 20),
            new EnchantEntry(Enchantment.LOOTING,               3, 35),
            new EnchantEntry(Enchantment.SWEEPING_EDGE,         3, 25),
            new EnchantEntry(Enchantment.EFFICIENCY,            5, 25),
            new EnchantEntry(Enchantment.SILK_TOUCH,            1, 40),
            new EnchantEntry(Enchantment.FORTUNE,               3, 40),
            new EnchantEntry(Enchantment.UNBREAKING,            3, 20),
            new EnchantEntry(Enchantment.MENDING,               1, 50),
            new EnchantEntry(Enchantment.POWER,                 5, 25),
            new EnchantEntry(Enchantment.PUNCH,                 2, 20),
            new EnchantEntry(Enchantment.FLAME,                 1, 20),
            new EnchantEntry(Enchantment.INFINITY,              1, 40),
            new EnchantEntry(Enchantment.LOYALTY,               3, 30),
            new EnchantEntry(Enchantment.IMPALING,              5, 25),
            new EnchantEntry(Enchantment.RIPTIDE,               3, 30),
            new EnchantEntry(Enchantment.CHANNELING,            1, 35)
    );

    private final SaplingShop plugin;

    public ShopBuyManager(SaplingShop plugin) {
        this.plugin = plugin;
    }

    // ─── Открытие окон ────────────────────────────────────────────

    public void openOresShop(Player player) {
        Inventory inv = buildCatalogueGui(ORES, BUY_TITLE_ORES, player, Material.BLUE_STAINED_GLASS_PANE);
        addNavigation(inv, player, "ores");
        player.openInventory(inv);
    }

    public void openDecoShop(Player player) {
        Inventory inv = buildCatalogueGui(DECO, BUY_TITLE_DECO, player, Material.YELLOW_STAINED_GLASS_PANE);
        addNavigation(inv, player, "deco");
        player.openInventory(inv);
    }

    public void openEnchantsShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, BUY_TITLE_ENCHANTS);
        GuiUtil.fillBorder(inv, Material.PURPLE_STAINED_GLASS_PANE, "§5");

        long balance = plugin.getCoinManager().getBalance(player);
        inv.setItem(4, GuiUtil.makeItem(Material.NETHER_STAR,
                "§e§lБаланс: §6" + balance + " монет", List.of("§7Тратьте монеты на зачарования")));

        int slot = 10;
        for (EnchantEntry entry : ENCHANTS) {
            if (slot >= 44) break;
            if (slot % 9 == 0 || slot % 9 == 8) { slot++; continue; }
            inv.setItem(slot, createEnchantBook(entry, balance));
            slot++;
        }

        addNavigation(inv, player, "enchants");
        player.openInventory(inv);
    }

    // ─── Построение GUI каталога ──────────────────────────────────

    private Inventory buildCatalogueGui(List<ShopEntry> catalogue, String title,
                                         Player player, Material borderMat) {
        Inventory inv = Bukkit.createInventory(null, 54, title);
        GuiUtil.fillBorder(inv, borderMat, "§r");

        long balance = plugin.getCoinManager().getBalance(player);
        inv.setItem(4, GuiUtil.makeItem(Material.NETHER_STAR,
                "§e§lБаланс: §6" + balance + " монет", List.of("§7Тратьте монеты из §a/shopsell")));

        int slot = 10;
        for (ShopEntry entry : catalogue) {
            if (slot >= 44) break;
            if (slot % 9 == 0 || slot % 9 == 8) { slot++; continue; }

            List<String> lore = new ArrayList<>(entry.description());
            lore.add("");
            lore.add("§e§lЦена: §6" + entry.price() + " монет");
            lore.add(balance >= entry.price() ? "§a✔ Можно купить" : "§c✘ Недостаточно монет");
            lore.add("§eНажмите для покупки!");

            inv.setItem(slot, GuiUtil.makeItem(entry.material(), entry.displayName(), lore));
            slot++;
        }
        return inv;
    }

    // ─── Навигация ────────────────────────────────────────────────

    private void addNavigation(Inventory inv, Player player, String current) {
        inv.setItem(45, GuiUtil.makeItem(Material.BARRIER, "§c§lЗакрыть", List.of("§7Закрыть магазин")));
        inv.setItem(47, GuiUtil.makeItem(Material.COAL,          "§7§l⚒ Руды",        List.of(current.equals("ores")     ? "§a▶ Текущий раздел" : "§eПерейти")));
        inv.setItem(48, GuiUtil.makeItem(Material.ENCHANTED_BOOK,"§5§l✦ Зачарования", List.of(current.equals("enchants") ? "§a▶ Текущий раздел" : "§eПерейти")));
        inv.setItem(50, GuiUtil.makeItem(Material.GOLD_INGOT,    "§6§l✦ Украшения",   List.of(current.equals("deco")     ? "§a▶ Текущий раздел" : "§eПерейти")));
        inv.setItem(53, GuiUtil.makeItem(Material.NETHER_STAR,
                "§e§lБаланс: §6" + plugin.getCoinManager().getBalance(player) + " монет", List.of("§7Ваши монеты")));
    }

    // ─── Покупка ──────────────────────────────────────────────────

    public boolean handleOrePurchase(Player player, ItemStack clicked) {
        return handleCataloguePurchase(player, clicked, ORES);
    }

    public boolean handleDecoPurchase(Player player, ItemStack clicked) {
        return handleCataloguePurchase(player, clicked, DECO);
    }

    private boolean handleCataloguePurchase(Player player, ItemStack clicked, List<ShopEntry> catalogue) {
        if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return false;
        String name = clicked.getItemMeta().getDisplayName();
        for (ShopEntry entry : catalogue) {
            if (name.equals(entry.displayName())) {
                return attemptPurchase(player, entry.price(),
                        new ItemStack(entry.material(), entry.amount()), entry.displayName());
            }
        }
        return false;
    }

    public boolean handleEnchantPurchase(Player player, ItemStack clicked) {
        if (clicked == null || clicked.getType() != Material.ENCHANTED_BOOK) return false;
        if (!clicked.hasItemMeta()) return false;
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) clicked.getItemMeta();
        for (Map.Entry<Enchantment, Integer> e : meta.getStoredEnchants().entrySet()) {
            for (EnchantEntry entry : ENCHANTS) {
                if (entry.ench().equals(e.getKey()) && entry.level() == e.getValue()) {
                    return attemptPurchase(player, entry.price(),
                            createEnchantBook(entry, plugin.getCoinManager().getBalance(player)),
                            MessageUtil.color("&5" + enchantName(entry.ench()) + " " + roman(entry.level())));
                }
            }
        }
        return false;
    }

    private boolean attemptPurchase(Player player, int price, ItemStack reward, String itemName) {
        if (!plugin.getCoinManager().spendCoins(player, price)) {
            player.sendMessage(MessageUtil.color("&c✘ Недостаточно монет! Нужно: &e" + price));
            return false;
        }
        player.getInventory().addItem(reward).values()
                .forEach(left -> player.getWorld().dropItemNaturally(player.getLocation(), left));
        player.sendMessage(MessageUtil.color(
                "&a✔ Куплено: &f" + itemName + " &aза &e" + price +
                " монет. &7Остаток: &e" + plugin.getCoinManager().getBalance(player)));
        return true;
    }

    // ─── Вспомогательные ──────────────────────────────────────────

    private ItemStack createEnchantBook(EnchantEntry entry, long balance) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(entry.ench(), entry.level(), true);
        meta.setDisplayName(MessageUtil.color("&5&l" + enchantName(entry.ench()) + " " + roman(entry.level())));
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtil.color("&7Уровень: &d" + roman(entry.level())));
        lore.add("");
        lore.add(MessageUtil.color("&e&lЦена: &6" + entry.price() + " монет"));
        lore.add(balance >= entry.price()
                ? MessageUtil.color("&a✔ Можно купить")
                : MessageUtil.color("&c✘ Недостаточно монет"));
        lore.add(MessageUtil.color("&eНажмите для покупки!"));
        meta.setLore(lore);
        book.setItemMeta(meta);
        return book;
    }

    private String enchantName(Enchantment e) {
        Map<String, String> names = Map.ofEntries(
                Map.entry("protection",             "Защита"),
                Map.entry("fire_protection",        "Огнезащита"),
                Map.entry("blast_protection",       "Взрывозащита"),
                Map.entry("projectile_protection",  "Защита от снарядов"),
                Map.entry("feather_falling",        "Плавное падение"),
                Map.entry("thorns",                 "Шипы"),
                Map.entry("respiration",            "Дыхание"),
                Map.entry("aqua_affinity",          "Водное сродство"),
                Map.entry("depth_strider",          "Хождение по дну"),
                Map.entry("frost_walker",           "Морозная поступь"),
                Map.entry("soul_speed",             "Скорость душ"),
                Map.entry("swift_sneak",            "Быстрый шаг"),
                Map.entry("sharpness",              "Острота"),
                Map.entry("smite",                  "Небесная кара"),
                Map.entry("bane_of_arthropods",     "Гибель членистоногих"),
                Map.entry("knockback",              "Отдача"),
                Map.entry("fire_aspect",            "Огненный аспект"),
                Map.entry("looting",                "Добыча"),
                Map.entry("sweeping_edge",          "Широкий замах"),
                Map.entry("efficiency",             "Эффективность"),
                Map.entry("silk_touch",             "Шёлковое касание"),
                Map.entry("fortune",                "Удача"),
                Map.entry("unbreaking",             "Прочность"),
                Map.entry("mending",                "Починка"),
                Map.entry("power",                  "Мощь"),
                Map.entry("punch",                  "Удар"),
                Map.entry("flame",                  "Пламя"),
                Map.entry("infinity",               "Бесконечность"),
                Map.entry("loyalty",                "Верность"),
                Map.entry("impaling",               "Пронзание"),
                Map.entry("riptide",                "Течение"),
                Map.entry("channeling",             "Молниеотвод")
        );
        return names.getOrDefault(e.getKey().getKey(), e.getKey().getKey());
    }

    private String roman(int n) {
        return switch (n) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III";
            case 4 -> "IV"; case 5 -> "V"; default -> String.valueOf(n);
        };
    }

    // ─── Проверка заголовков ──────────────────────────────────────

    public static boolean isOresShop(String t)     { return t.equals(BUY_TITLE_ORES); }
    public static boolean isEnchantsShop(String t) { return t.equals(BUY_TITLE_ENCHANTS); }
    public static boolean isDecoShop(String t)     { return t.equals(BUY_TITLE_DECO); }
}
