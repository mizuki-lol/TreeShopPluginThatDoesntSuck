package me.saplingshop.commands;

import me.saplingshop.SaplingShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopGenCommand implements CommandExecutor {

    private final SaplingShop plugin;

    public ShopGenCommand(SaplingShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько для игроков!");
            return true;
        }
        if (!player.hasPermission("saplingshop.gen")) {
            player.sendMessage("§cНет прав!");
            return true;
        }
        plugin.getGeneratorManager().openShop(player);
        return true;
    }
}
