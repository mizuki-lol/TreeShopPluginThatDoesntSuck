package me.saplingshop.managers;

import me.saplingshop.SaplingShop;
import me.saplingshop.utils.GuiUtil;
import me.saplingshop.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class GeneratorManager {

    public static final String GEN_TITLE = "§c§l⚙ Магазин — Генераторы";

    // PDC key used to mark an item/block as a generator and which ore it produces
    private NamespacedKey genKey;

    public record GenEntry(Material material, String displayName, int price, List<String> description) {}

    private List<GenEntry> generators;

    private final SaplingShop plugin;

    public GeneratorManager(SaplingShop plugin) {
        this.plugin = plugin;
        this.genKey = new NamespacedKey(plugin, "generator_ore");
        buildCatalogue();
    }

    private void buildCatalogue() {
        generators = List.of(
                new GenEntry(Material.STONE,       "§7Генератор камня",      30,  List.of("§7Ломаешь — падает камень", "§7На месте появляется новый блок")),
                new GenEntry(Material.COAL_ORE,    "§8Генератор угля",       40,  List.of("§7Ломаешь — падает уголь", "§7На месте появляется новый блок")),
                new GenEntry(Material.IRON_ORE,    "§fГенератор железа",     60,  List.of("§7Ломаешь — падает железная руда", "§7На месте появляется новый блок")),
                new GenEntry(Material.COPPER_ORE,  "§cГенератор меди",       50,  List.of("§7Ломаешь — падает медная руда", "§7На месте появляется новый блок")),
                new GenEntry(Material.GOLD_ORE,    "§6Генератор золота",     80,  List.of("§7Ломаешь — падает золотая руда", "§7На месте появляется новый блок")),
                new GenEntry(Material.LAPIS_ORE,   "§1Генератор лазурита",   70,  List.of("§7Ломаешь — падает лазурит", "§7На месте появляется новый блок")),
                new GenEntry(Material.REDSTONE_ORE,"§cГенератор редстоуна",  70,  List.of("§7Ломаешь — падает редстоун", "§7На месте появляется новый блок")),
                new GenEntry(Material.NETHER_QUARTZ_ORE, "§fГенератор кварца", 60, List.of("§7Ломаешь — падает кварц", "§7На месте появляется новый блок")),
                new GenEntry(Material.EMERALD_ORE, "§aГенератор изумруда",   150, List.of("§7Ломаешь — падает изумруд", "§7На месте появляется новый блок")),
                new GenEntry(Material.DIAMOND_ORE, "§bГенератор алмаза",    250, List.of("§7Ломаешь — падает алмаз", "§7На месте появляется новый блок")),
                new GenEntry(Material.OAK_LEAVES,  "§2Генератор листвы",     35,  List.of("§730% шанс получить саженец", "§7На месте появляется новый блок"))
        );
    }

    // ─── GUI ────────────────────────────────────────────────────

    public void openShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, GEN_TITLE);
        GuiUtil.fillBorder(inv, Material.RED_STAINED_GLASS_PANE, "§c");

        long balance = plugin.getCoinManager().getBalance(player);
        inv.setItem(4, GuiUtil.makeItem(Material.NETHER_STAR,
                "§e§lБаланс: §6" + balance + " монет", List.of("§7Тратьте монеты из §a/shopsell")));

        int slot = 10;
        for (GenEntry entry : generators) {
            if (slot >= 44) break;
            if (slot % 9 == 0 || slot % 9 == 8) { slot++; continue; }

            List<String> lore = new ArrayList<>(entry.description());
            lore.add("");
            lore.add("§e§lЦена: §6" + entry.price() + " монет");
            lore.add(balance >= entry.price() ? "§a✔ Можно купить" : "§c✘ Недостаточно монет");
            lore.add("§eНажмите для покупки!");

            inv.setItem(slot, createGeneratorItem(entry, lore));
            slot++;
        }

        inv.setItem(45, GuiUtil.makeItem(Material.BARRIER, "§c§lЗакрыть", List.of("§7Закрыть магазин")));
        inv.setItem(53, GuiUtil.makeItem(Material.NETHER_STAR,
                "§e§lБаланс: §6" + balance + " монет", List.of("§7Ваши монеты")));

        player.openInventory(inv);
    }

    public boolean isGenShop(String title) {
        return title.equals(GEN_TITLE);
    }

    // ─── Покупка ────────────────────────────────────────────────

    public boolean handlePurchase(Player player, ItemStack clicked) {
        if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return false;
        String name = clicked.getItemMeta().getDisplayName();

        for (GenEntry entry : generators) {
            if (name.equals(entry.displayName())) {
                if (!plugin.getCoinManager().spendCoins(player, entry.price())) {
                    player.sendMessage(MessageUtil.color("&c✘ Недостаточно монет! Нужно: &e" + entry.price()));
                    return false;
                }
                ItemStack reward = createGeneratorItem(entry, List.of(
                        MessageUtil.color("&7Поставь блок в мире"),
                        MessageUtil.color("&7Ломай — получай ресурс"),
                        MessageUtil.color("&7Генератор восстановится сам!")
                ));
                player.getInventory().addItem(reward).values()
                        .forEach(left -> player.getWorld().dropItemNaturally(player.getLocation(), left));
                player.sendMessage(MessageUtil.color(
                        "&a✔ Куплено: &f" + entry.displayName() + " &aза &e" + entry.price() +
                        " монет. &7Остаток: &e" + plugin.getCoinManager().getBalance(player)));
                return true;
            }
        }
        return false;
    }

    // ─── Создание предмета-генератора с тегом ────────────────────

    private ItemStack createGeneratorItem(GenEntry entry, List<String> lore) {
        ItemStack item = new ItemStack(entry.material());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(entry.displayName());
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(genKey, PersistentDataType.STRING, entry.material().name());
        item.setItemMeta(meta);
        return item;
    }

    // ─── Проверки для листенера ───────────────────────────────────

    /** Проверяет, является ли ItemStack предметом-генератором (до установки в мир) */
    public boolean isGeneratorItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(genKey, PersistentDataType.STRING);
    }

    public NamespacedKey getGenKey() {
        return genKey;
    }

    /** Создаёт "пустой" предмет-генератор того же материала, что и блок (для возврата в мир после ломки) */
    public ItemStack createPlacedMarkerItem(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(genKey, PersistentDataType.STRING, material.name());
        item.setItemMeta(meta);
        return item;
    }
}
