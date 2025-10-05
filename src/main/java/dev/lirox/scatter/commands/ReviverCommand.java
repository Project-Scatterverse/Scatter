package dev.lirox.scatter.commands;

import dev.lirox.scatter.registries.Registry;
import dev.lirox.scatter.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ReviverCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must be holding an item.");
            return true;
        }

        if (item.getItemMeta() == null) {
            player.sendMessage(ChatColor.RED + "This item cannot have custom model data.");
            return true;
        }

        ItemUtils.setProperties(item, List.of(Registry.PROP_REVIVER));
        player.sendMessage(ChatColor.GREEN + "Done.");
        return true;
    }
}
