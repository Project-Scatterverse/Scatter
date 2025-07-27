package org.lirox.scatter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.lirox.scatter.Scatter.configManager;

public class PlayerUtils {
    // Visibility
    public static void setVisibilityToPlayer(Player player, Player seeker, boolean visibility) {
        if (visibility) seeker.showPlayer(Scatter.plugin, player);
        else seeker.hidePlayer(Scatter.plugin, player);
    }

    public static void setVisibilityToAllPlayers(Player player, boolean visibility) {
        for (Player seeker : Bukkit.getOnlinePlayers()) {
            setVisibilityToPlayer(player, seeker, visibility);
        }
    }


    // State checking
    public static boolean isScattered(Player player) {
        return isScatterAffected(player) &&  configManager.scatteredPlayers.get(player.getName()).state == 2;
    }

    public static boolean isTrapped(Player player) {
        return isScatterAffected(player) && configManager.scatteredPlayers.get(player.getName()).state == 1;
    }

    public static boolean isScatterAffected(Player player) {
        return configManager.scatteredPlayers.containsKey(player.getName());
    }


    // Drop items
    public static void dropItems(ItemStack[] items, Location location) {
        for (ItemStack item : items) {
            if (item != null) location.getWorld().dropItemNaturally(location, item);
        }
    }

    public static void dropInventory(Player player) {
        dropItems(player.getInventory().getContents(), player.getLocation());
        player.getInventory().clear();
    }

    public static void dropEnderChest(Player player) {
        dropItems(player.getEnderChest().getContents(), player.getLocation());
        player.getEnderChest().clear();
    }
}
