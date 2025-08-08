package org.lirox.scatter.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lirox.scatter.PlayerUtils;

public class TrapCommand implements CommandExecutor {

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

        if (PlayerUtils.isScattered(player)) {
            player.sendMessage(ChatColor.RED + "Nuh uh.");
            return true;
        }

        if (PlayerUtils.isTrapped(player)) PlayerUtils.release(player);
        else PlayerUtils.trap(player, player.getName());

        player.sendMessage(ChatColor.GREEN + "Done.");
        return true;
    }
} // easiest command of my life
