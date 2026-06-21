package me.saplingshop;

import me.saplingshop.commands.ShopBuyCommand;
import me.saplingshop.commands.ShopGenCommand;
import me.saplingshop.commands.ShopSellCommand;
import me.saplingshop.listeners.GeneratorListener;
import me.saplingshop.listeners.InventoryClickListener;
import me.saplingshop.managers.CoinManager;
import me.saplingshop.managers.GeneratorManager;
import me.saplingshop.managers.ShopBuyManager;
import me.saplingshop.managers.ShopSellManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SaplingShop extends JavaPlugin {

    private CoinManager coinManager;
    private ShopSellManager shopSellManager;
    private ShopBuyManager shopBuyManager;
    private GeneratorManager generatorManager;
    private GeneratorListener generatorListener;

    @Override
    public void onEnable() {

        this.coinManager      = new CoinManager(this);
        this.shopSellManager  = new ShopSellManager(this);
        this.shopBuyManager   = new ShopBuyManager(this);
        this.generatorManager = new GeneratorManager(this);

        getCommand("shopsell").setExecutor(new ShopSellCommand(this));
        getCommand("shopbuy").setExecutor(new ShopBuyCommand(this));
        getCommand("shopgen").setExecutor(new ShopGenCommand(this));

        this.generatorListener = new GeneratorListener(this);
        getServer().getPluginManager().registerEvents(generatorListener, this);
        getServer().getPluginManager().registerEvents(
                new InventoryClickListener(this), this);

        getLogger().info("SaplingShop включён! 1 саженец = 1 монета (Nether Star).");
    }

    @Override
    public void onDisable() {
        if (coinManager != null) coinManager.saveData();
        if (generatorListener != null) generatorListener.saveGenerators();
        getLogger().info("SaplingShop отключён.");
    }

    public CoinManager getCoinManager()           { return coinManager; }
    public ShopSellManager getShopSellManager()   { return shopSellManager; }
    public ShopBuyManager getShopBuyManager()     { return shopBuyManager; }
    public GeneratorManager getGeneratorManager() { return generatorManager; }
}
