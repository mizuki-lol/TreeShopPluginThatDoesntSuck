package me.saplingshop.listeners;

import me.saplingshop.SaplingShop;
import me.saplingshop.managers.ShopBuyManager;
import me.saplingshop.managers.ShopSellManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private final SaplingShop plugin;

    public InventoryClickListener(SaplingShop plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        Inventory inv = event.getInventory();
        ItemStack clicked = event.getCurrentItem();
        int slot = event.getRawSlot();

        // ── МАГАЗИН ПРОДАЖИ ──────────────────────────────────────
        if (title.equals(ShopSellManager.SELL_TITLE)) {

            boolean isSellSlot = false;
            for (int s : ShopSellManager.SELL_SLOTS) {
                if (s == slot) { isSellSlot = true; break; }
            }
            if (isSellSlot) return;

            event.setCancelled(true);
            if (clicked == null || clicked.getType() == Material.AIR) return;

            switch (slot) {
                case 40 -> {
                    long earned = plugin.getShopSellManager().sellAllFromInventory(player);
                    if (earned > 0) {
                        player.sendMessage("§a✔ Продано §e" + earned + " саженцев§a! Получено §e" + earned + " монет.");
                        player.sendMessage("§7Баланс: §e" + plugin.getCoinManager().getBalance(player) + " монет");
                    } else {
                        player.sendMessage("§cВ инвентаре нет саженцев!");
                    }
                    plugin.getShopSellManager().openShop(player);
                }
                case 49 -> {
                    long earned = plugin.getShopSellManager().sellFromGuiSlots(inv, player);
                    if (earned > 0) {
                        player.sendMessage("§a✔ Продано §e" + earned + " саженцев§a! Получено §e" + earned + " монет.");
                        player.sendMessage("§7Баланс: §e" + plugin.getCoinManager().getBalance(player) + " монет");
                    } else {
                        player.sendMessage("§cВ слотах нет саженцев!");
                    }
                    plugin.getShopSellManager().openShop(player);
                }
                case 45 -> player.closeInventory();
            }
            return;
        }

        // ── МАГАЗИН ПОКУПКИ — РУДЫ ───────────────────────────────
        if (ShopBuyManager.isOresShop(title)) {
            event.setCancelled(true);
            if (clicked == null || clicked.getType() == Material.AIR) return;
            if (slot == 45) { player.closeInventory(); return; }
            if (slot == 47) { plugin.getShopBuyManager().openOresShop(player); return; }
            if (slot == 48) { plugin.getShopBuyManager().openEnchantsShop(player); return; }
            if (slot == 50) { plugin.getShopBuyManager().openDecoShop(player); return; }
            if (plugin.getShopBuyManager().handleOrePurchase(player, clicked))
                plugin.getShopBuyManager().openOresShop(player);
            return;
        }

        // ── МАГАЗИН ПОКУПКИ — ЗАЧАРОВАНИЯ ────────────────────────
        if (ShopBuyManager.isEnchantsShop(title)) {
            event.setCancelled(true);
            if (clicked == null || clicked.getType() == Material.AIR) return;
            if (slot == 45) { player.closeInventory(); return; }
            if (slot == 47) { plugin.getShopBuyManager().openOresShop(player); return; }
            if (slot == 48) { plugin.getShopBuyManager().openEnchantsShop(player); return; }
            if (slot == 50) { plugin.getShopBuyManager().openDecoShop(player); return; }
            if (plugin.getShopBuyManager().handleEnchantPurchase(player, clicked))
                plugin.getShopBuyManager().openEnchantsShop(player);
            return;
        }

        // ── МАГАЗИН ПОКУПКИ — ОТДЕЛКИ БРОНИ ───────────────────────
        if (ShopBuyManager.isDecoShop(title)) {
            event.setCancelled(true);
            if (clicked == null || clicked.getType() == Material.AIR) return;
            if (slot == 45) { player.closeInventory(); return; }
            if (slot == 47) { plugin.getShopBuyManager().openOresShop(player); return; }
            if (slot == 48) { plugin.getShopBuyManager().openEnchantsShop(player); return; }
            if (slot == 50) { plugin.getShopBuyManager().openDecoShop(player); return; }
            if (plugin.getShopBuyManager().handleDecoPurchase(player, clicked))
                plugin.getShopBuyManager().openDecoShop(player);
            return;
        }

        // ── МАГАЗИН ГЕНЕРАТОРОВ ───────────────────────────────────
        if (plugin.getGeneratorManager().isGenShop(title)) {
            event.setCancelled(true);
            if (clicked == null || clicked.getType() == Material.AIR) return;
            if (slot == 45) { player.closeInventory(); return; }
            if (plugin.getGeneratorManager().handlePurchase(player, clicked))
                plugin.getGeneratorManager().openShop(player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        String title = event.getView().getTitle();
        if (title.equals(ShopSellManager.SELL_TITLE)
                || ShopBuyManager.isOresShop(title)
                || ShopBuyManager.isEnchantsShop(title)
                || ShopBuyManager.isDecoShop(title)
                || plugin.getGeneratorManager().isGenShop(title)) {
            plugin.getCoinManager().saveData();
        }
    }
}
