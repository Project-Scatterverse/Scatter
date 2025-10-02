package dev.lirox.scatter.commands;

import dev.lirox.scatter.states.Ghost;
import dev.lirox.scatter.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        if (PlayerUtils.isState(player, Ghost.class)) PlayerUtils.revive(player, false);
        else PlayerUtils.scatter(player, false);

        player.sendMessage(ChatColor.GREEN + "Done.");
        return true;
    }
} // easiest command of my life
