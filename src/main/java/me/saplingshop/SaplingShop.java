package me.saplingshop;

import me.saplingshop.commands.ShopBuyCommand;
import me.saplingshop.commands.ShopSellCommand;
import me.saplingshop.listeners.InventoryClickListener;
import me.saplingshop.managers.CoinManager;
import me.saplingshop.managers.ShopBuyManager;
import me.saplingshop.managers.ShopSellManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SaplingShop extends JavaPlugin {

    private CoinManager coinManager;
    private ShopSellManager shopSellManager;
    private ShopBuyManager shopBuyManager;

    @Override
    public void onEnable() {

        this.coinManager    = new CoinManager(this);
        this.shopSellManager = new ShopSellManager(this);
        this.shopBuyManager  = new ShopBuyManager(this);

        getCommand("shopsell").setExecutor(new ShopSellCommand(this));
        getCommand("shopbuy").setExecutor(new ShopBuyCommand(this));

        getServer().getPluginManager().registerEvents(
                new InventoryClickListener(this), this);

        getLogger().info("SaplingShop включён! 1 саженец = 1 монета (Nether Star).");
    }

    @Override
    public void onDisable() {
        if (coinManager != null) coinManager.saveData();
        getLogger().info("SaplingShop отключён.");
    }

    public CoinManager getCoinManager()         { return coinManager; }
    public ShopSellManager getShopSellManager() { return shopSellManager; }
    public ShopBuyManager getShopBuyManager()   { return shopBuyManager; }
}
