package org.lirox.scatter.commands;

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
import org.lirox.scatter.PlayerUtils;

import static org.lirox.scatter.Scatter.SCATTER_KEY;
import static org.lirox.scatter.Scatter.configManager;

public class GhostCommand implements CommandExecutor {

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

        if (PlayerUtils.isScattered(player)) PlayerUtils.revive(player);
        else PlayerUtils.scatter(player, player.getName());

        player.sendMessage(ChatColor.GREEN + "Done.");
        return true;
    }
} // easiest command of my life
