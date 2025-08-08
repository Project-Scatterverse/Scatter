package org.lirox.scatter;

import com.google.j2objc.annotations.WeakOuter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import static org.lirox.scatter.Events.hitsTillFinalize;
import static org.lirox.scatter.Scatter.configManager;
import static org.lirox.scatter.Scatter.noMobPushTeam;

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

    // Scattering and reviving
    public static void scatter(Player player, String killer) {
        configManager.scatteredPlayers.put(player.getName(), new Scatterred(player.getName(), killer, 0, 2, 0, 0, player.getLocation()));
        PlayerUtils.setVisibilityToAllPlayers(player, false);
        player.setGameMode(GameMode.ADVENTURE);
        player.setCanPickupItems(false);
        noMobPushTeam.addPlayer(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        player.setHealth(1);
        player.setSaturation(0);
    }

    public static void scatter(Player player, Player killer) {
        scatter(player, killer.getName());
    }

    public static void revive(Player player) {
        configManager.scatteredPlayers.remove(player.getName());
        PlayerUtils.setVisibilityToAllPlayers(player, true);
        player.setGameMode(GameMode.SURVIVAL);
        player.setCanPickupItems(true);
        noMobPushTeam.removePlayer(player);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.setHealth(10);
        player.setSaturation(10);
    }

    // Trapping and releasing
    public static void trap(Player player, String killer) {
        configManager.scatteredPlayers.put(player.getName(), new Scatterred(player.getName(), killer, hitsTillFinalize, 1, 0, 2400, player.getLocation()));
    }

    public static void trap(Player player, Player killer) {
        trap(player, killer.getName());
    }

    public static void release(Player player) {
        configManager.scatteredPlayers.remove(player.getName());
    }
}
