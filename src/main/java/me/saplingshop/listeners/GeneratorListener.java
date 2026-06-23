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
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Логика генераторов:
 * - Игрок ставит блок-генератор (отмечен PersistentDataContainer тегом).
 * - При ломке: блок полностью удаляется (становится воздухом), падает
 *   обычный ресурс (без бонусов от зачарований), а через 1 секунду (20 тиков)
 *   на этом же месте снова появляется блок-генератор того же типа.
 */
public class GeneratorListener implements Listener {

    private static final long RESPAWN_DELAY_TICKS = 20L; // 1 секунда

    private final SaplingShop plugin;
    private final NamespacedKey genKey;

    // Координаты всех активных точек-генераторов -> материал блока
    private final Set<String> generatorLocations = new HashSet<>();
    private final Map<String, String> locationMaterial = new HashMap<>();

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
        Location loc = block.getLocation();
        String locKey = locKey(loc);

        if (!generatorLocations.contains(locKey)) return; // обычный блок — не наш генератор

        String matName = locationMaterial.get(locKey);
        Material material;
        try {
            material = Material.valueOf(matName);
        } catch (Exception e) {
            // повреждённые данные — снимаем метку и пропускаем как обычный блок
            generatorLocations.remove(locKey);
            locationMaterial.remove(locKey);
            return;
        }

        // Отменяем стандартный дроп (чтобы не зависело от Fortune/Silk Touch)
        event.setDropItems(false);

        // Блок полностью удаляется — становится воздухом
        block.setType(Material.AIR);

        // Особая логика для генератора листвы: шанс 5% на случайный саженец,
        // иначе дроп вообще без предмета (как настоящая листва)
        if (material == Material.OAK_LEAVES) {
            if (java.util.concurrent.ThreadLocalRandom.current().nextInt(100) < 30) {
                ItemStack sapling = new ItemStack(randomSapling());
                block.getWorld().dropItemNaturally(loc.clone().add(0.5, 0.5, 0.5), sapling);
            }
        } else {
            // Обычные генераторы руд — выдаём ресурс соответствующий типу блока
            ItemStack drop = getNaturalDrop(material);
            block.getWorld().dropItemNaturally(loc.clone().add(0.5, 0.5, 0.5), drop);
        }

        // Планируем восстановление блока через 1 секунду на этом же месте
        final Material finalMaterial = material;
        new BukkitRunnable() {
            @Override
            public void run() {
                // Восстанавливаем только если место всё ещё воздух (на случай если игрок
                // успел поставить туда что-то своё за эту секунду)
                if (block.getType() == Material.AIR) {
                    block.setType(finalMaterial);
                }
                // Локация остаётся помеченной как генератор для следующей ломки
            }
        }.runTaskLater(plugin, RESPAWN_DELAY_TICKS);
    }

    /** Возвращает случайный вид саженца (для генератора листвы) */
    private Material randomSapling() {
        Material[] saplings = {
                Material.OAK_SAPLING,
                Material.SPRUCE_SAPLING,
                Material.BIRCH_SAPLING,
                Material.JUNGLE_SAPLING,
                Material.ACACIA_SAPLING,
                Material.DARK_OAK_SAPLING,
                Material.CHERRY_SAPLING
        };
        return saplings[java.util.concurrent.ThreadLocalRandom.current().nextInt(saplings.length)];
    }

    /** Возвращает "природный" дроп для блока-генератора (без анчантов, аналогично настоящей руде) */
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
