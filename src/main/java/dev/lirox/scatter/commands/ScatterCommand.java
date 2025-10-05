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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ScatterCommand implements CommandExecutor {

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

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <0-3>, 0 - None, 1 - Scatter, 2 - Reviver, 3 - Both.");
            return true;
        }

        int value;
        try {
            value = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid number. 0 - None, 1 - Scatter, 2 - Reviver, 3 - Both.");
            return true;
        }

        if (value < 0 || value > 3) {
            player.sendMessage(ChatColor.RED + "Invalid number. 0 - None, 1 - Scatter, 2 - Reviver, 3 - Both.");
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

        ItemUtils.setType(item, Registry.TYPE_SCATTER);
        player.sendMessage(ChatColor.GREEN + "Done.");
        return true;
    }
}
