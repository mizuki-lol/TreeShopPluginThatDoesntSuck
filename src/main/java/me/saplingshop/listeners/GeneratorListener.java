package me.saplingshop.listeners;

import me.saplingshop.SaplingShop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles the lifecycle of generator blocks:
 * - When a tagged generator item is placed, the block's location is remembered.
 * - When that block is broken, the normal drop happens (handled by vanilla),
 *   but we cancel vanilla block removal logic ourselves is NOT needed —
 *   instead we let the block break normally and immediately set the same
 *   block back at that location (instant regeneration), without consuming
 *   another item, and we re-mark that location as a generator.
 *
 * To avoid abuse (silk touch / fortune farming many generators from one),
 * generator blocks always drop exactly one vanilla-style item via natural
 * drop suppression and a manual single drop, ignoring enchantments.
 */
public class GeneratorListener implements Listener {

    private final SaplingShop plugin;
    private final NamespacedKey genKey;

    // Tracks all currently active generator block locations (world;x;y;z -> material name)
    private final Set<String> generatorLocations = new HashSet<>();
    private final java.util.Map<String, String> locationMaterial = new java.util.HashMap<>();

    private final File dataFile;

    public GeneratorListener(SaplingShop plugin) {
        this.plugin = plugin;
        this.genKey = plugin.getGeneratorManager().getGenKey();
        this.dataFile = new File(plugin.getDataFolder(), "generators.yml");
        loadGenerators();
    }

    // ─── Установка генератора ──────────────────────────────────────

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!plugin.getGeneratorManager().isGeneratorItem(item)) return;

        Block block = event.getBlockPlaced();
        String locKey = locKey(block.getLocation());
        String matName = item.getItemMeta().getPersistentDataContainer()
                .get(genKey, PersistentDataType.STRING);

        generatorLocations.add(locKey);
        locationMaterial.put(locKey, matName);
        saveGenerators();

        event.getPlayer().sendMessage("§a✔ Генератор установлен! Ломай блок чтобы получать ресурс.");
    }

    // ─── Ломка генератора ───────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        String locKey = locKey(block.getLocation());

        if (!generatorLocations.contains(locKey)) return; // обычный блок — ничего не делаем

        Player player = event.getPlayer();
        String matName = locationMaterial.get(locKey);
        Material material;
        try {
            material = Material.valueOf(matName);
        } catch (Exception e) {
            // На случай повреждённых данных — снимаем тег и пропускаем как обычный блок
            generatorLocations.remove(locKey);
            locationMaterial.remove(locKey);
            return;
        }

        // Отменяем вид дефолтного дропа (чтобы избежать дюпа через Fortune/Silk Touch
        // и чтобы самим контролировать количество и вид выпавшего ресурса)
        event.setDropItems(false);

        // Выдаём один стандартный ресурс соответствующий типу блока (как настоящая руда)
        ItemStack drop = getNaturalDrop(material);
        block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), drop);

        // Мгновенно восстанавливаем генератор на этом же месте
        block.setType(material);

        // Локация остаётся помеченной как генератор (на случай если plugin reload)
        // Ничего больше менять не нужно — генератор уже отмечен в generatorLocations
    }

    /** Возвращает "природный" дроп для блока-генератора (без анчантов, по аналогии с настоящей рудой) */
    private ItemStack getNaturalDrop(Material oreBlock) {
        return switch (oreBlock) {
            case STONE                -> new ItemStack(Material.STONE, 1);
            case COAL_ORE             -> new ItemStack(Material.COAL, 1);
            case IRON_ORE             -> new ItemStack(Material.RAW_IRON, 1);
            case COPPER_ORE           -> new ItemStack(Material.RAW_COPPER, 1);
            case GOLD_ORE             -> new ItemStack(Material.RAW_GOLD, 1);
            case LAPIS_ORE            -> new ItemStack(Material.LAPIS_LAZULI, 4);
            case REDSTONE_ORE         -> new ItemStack(Material.REDSTONE, 4);
            case NETHER_QUARTZ_ORE    -> new ItemStack(Material.QUARTZ, 1);
            case EMERALD_ORE          -> new ItemStack(Material.EMERALD, 1);
            case DIAMOND_ORE          -> new ItemStack(Material.DIAMOND, 1);
            default                   -> new ItemStack(oreBlock, 1);
        };
    }

    // ─── Сохранение/загрузка отметок генераторов ──────────────────

    private String locKey(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
    }

    public void loadGenerators() {
        if (!dataFile.exists()) return;
        org.bukkit.configuration.file.FileConfiguration cfg =
                org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(dataFile);
        if (!cfg.isConfigurationSection("generators")) return;
        for (String key : cfg.getConfigurationSection("generators").getKeys(false)) {
            String decodedKey = key.replace("__", ";");
            String mat = cfg.getString("generators." + key);
            generatorLocations.add(decodedKey);
            locationMaterial.put(decodedKey, mat);
        }
    }

    public void saveGenerators() {
        org.bukkit.configuration.file.FileConfiguration cfg = new org.bukkit.configuration.file.YamlConfiguration();
        for (String locKey : generatorLocations) {
            String safeKey = locKey.replace(";", "__");
            cfg.set("generators." + safeKey, locationMaterial.get(locKey));
        }
        try {
            plugin.getDataFolder().mkdirs();
            cfg.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
