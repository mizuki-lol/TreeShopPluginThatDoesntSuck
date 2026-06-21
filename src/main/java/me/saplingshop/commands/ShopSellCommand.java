package me.saplingshop.commands;

import me.saplingshop.SaplingShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopSellCommand implements CommandExecutor {

    private final SaplingShop plugin;

    public ShopSellCommand(SaplingShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько для игроков!");
            return true;
        }
        if (!player.hasPermission("saplingshop.sell")) {
            player.sendMessage("§cНет прав!");
            return true;
        }
        plugin.getShopSellManager().openShop(player);
        return true;
    }
}
