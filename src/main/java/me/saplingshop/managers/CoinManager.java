package me.saplingshop.managers;

import me.saplingshop.SaplingShop;
import me.saplingshop.utils.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CoinManager {

    private final SaplingShop plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;
    private final Map<UUID, Long> balances = new HashMap<>();

    public CoinManager(SaplingShop plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "coins.yml");
        loadData();
    }

    // ─── Баланс ───────────────────────────────────────────────────

    public long getBalance(Player player) {
        return balances.getOrDefault(player.getUniqueId(), 0L);
    }

    public void addCoins(Player player, long amount) {
        UUID id = player.getUniqueId();
        balances.put(id, balances.getOrDefault(id, 0L) + amount);
    }

    /** Возвращает true если монет хватило и они были списаны */
    public boolean spendCoins(Player player, long amount) {
        UUID id = player.getUniqueId();
        long balance = balances.getOrDefault(id, 0L);
        if (balance < amount) return false;
        balances.put(id, balance - amount);
        return true;
    }

    // ─── Сохранение/загрузка ──────────────────────────────────────

    public void loadData() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try { dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        if (dataConfig.isConfigurationSection("balances")) {
            for (String key : dataConfig.getConfigurationSection("balances").getKeys(false)) {
                try {
                    balances.put(UUID.fromString(key), dataConfig.getLong("balances." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void saveData() {
        balances.forEach((uuid, coins) ->
                dataConfig.set("balances." + uuid, coins));
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
