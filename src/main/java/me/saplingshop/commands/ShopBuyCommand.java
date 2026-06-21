package me.saplingshop.commands;

import me.saplingshop.SaplingShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopBuyCommand implements CommandExecutor {

    private final SaplingShop plugin;

    public ShopBuyCommand(SaplingShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько для игроков!");
            return true;
        }
        if (!player.hasPermission("saplingshop.buy")) {
            player.sendMessage("§cНет прав!");
            return true;
        }
        plugin.getShopBuyManager().openOresShop(player);
        return true;
    }
}
