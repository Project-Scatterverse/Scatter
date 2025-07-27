package org.lirox.scatter;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;

public class ItemUtils {
    public static Consumer<ItemStack> decrementOrRemove = item -> {
        if (item.getAmount() > 1) item.setAmount(item.getAmount() - 1);
        else item.setAmount(0);
    };


    // Scatter
    public static boolean isScatter(ItemStack item) {
        return item != null && item.getType() != Material.AIR &&
                item.getItemMeta() != null &&
                item.getItemMeta().hasCustomModelData() &&
                (item.getItemMeta().getCustomModelData() == 1 || item.getItemMeta().getCustomModelData() == 3);
    }

    public static boolean hasScatterOffHand(Player player) {
        return isScatter(player.getInventory().getItemInOffHand());
    }

    public static boolean hasScatterMainHand(Player player) {
        return isScatter(player.getInventory().getItemInMainHand());
    }

    public static boolean hasScatter(Player player) {
        return (hasScatterMainHand(player) || hasScatterOffHand(player)) ||
                isScatter(player.getInventory().getItem(EquipmentSlot.HEAD)) ||
                isScatter(player.getInventory().getItem(EquipmentSlot.CHEST)) ||
                isScatter(player.getInventory().getItem(EquipmentSlot.LEGS)) ||
                isScatter(player.getInventory().getItem(EquipmentSlot.FEET));
    }


    // Reviver
    public static boolean isReviver(ItemStack item) {
        return item != null && item.getType() != Material.AIR &&
                item.getItemMeta() != null &&
                item.getItemMeta().hasCustomModelData() &&
                (item.getItemMeta().getCustomModelData() == 2 || item.getItemMeta().getCustomModelData() == 3);
    }

    public static boolean hasReviverOffHand(Player player) {
        return isReviver(player.getInventory().getItemInOffHand());
    }

    public static boolean hasReviverMainHand(Player player) {
        return isReviver(player.getInventory().getItemInMainHand());
    }

    public static boolean hasReviver(Player player) {
        return (hasReviverMainHand(player) || hasReviverOffHand(player));
    }


    // Consume
    public static void removeOneScatter(Player player) {
        if (hasScatterMainHand(player)) {
            decrementOrRemove.accept(player.getInventory().getItemInMainHand());
            return;
        }

        if (hasScatterOffHand(player)) {
            decrementOrRemove.accept(player.getInventory().getItemInOffHand());
            return;
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = player.getInventory().getItem(slot);
            if (isScatter(item)) {
                decrementOrRemove.accept(item);
                return;
            }
        }
    }
}
