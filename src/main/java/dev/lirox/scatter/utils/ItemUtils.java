package dev.lirox.scatter.utils;

import com.google.gson.Gson;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import java.util.List;

import static dev.lirox.scatter.registries.Registry.*;

public class ItemUtils {
    public static Consumer<ItemStack> decrementOrRemove = item -> {
        if (item.getAmount() > 1) item.setAmount(item.getAmount() - 1);
        else item.setAmount(0);
    };


    // Get/Set type
    public static String getType(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        return meta.getPersistentDataContainer().get(TYPE_KEY, PersistentDataType.STRING);
    }

    public static boolean isScatter(ItemStack item) {
        return getType(item) != null;
    }

    public static void setType(ItemStack item, String type) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(TYPE_KEY, PersistentDataType.STRING, type);
        item.setItemMeta(meta);
    }


    // Get/Set properties
    private static final Gson gson = new Gson();
    public static List<String> getProperties(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return List.of();
        String json = meta.getPersistentDataContainer().get(PROP_KEY, PersistentDataType.STRING);
        if (json == null || json.isEmpty()) return List.of();
        return Arrays.asList(gson.fromJson(json, String[].class));
    }

    public static void setProperties(ItemStack item, List<String> properties) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        String json = gson.toJson(properties);
        meta.getPersistentDataContainer().set(PROP_KEY, PersistentDataType.STRING, json);
        item.setItemMeta(meta);
    }


    // Checking for things
    public static boolean isType(ItemStack item, String type) {
        return Objects.equals(getType(item), type);
    }

    public static boolean hasProperty(ItemStack item, String property) {
        return getProperties(item).contains(property);
    }


    // Get items
    public static ItemStack offHand(Player player) {
        return player.getInventory().getItemInOffHand();
    }

    public static ItemStack mainHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }


    public static ItemStack getFirstScatterItem(Player player) {
        ItemStack[] hands = {offHand(player), mainHand(player)};
        for (ItemStack item : hands) {
            if (isScatter(item)) return item;
        }

        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (EquipmentSlot slot : slots) {
            ItemStack item = player.getInventory().getItem(slot);
            if (isScatter(item)) return item;
        }
        return null;
    }


    // Consume
    public static void decrementItem(ItemStack item) {
        if (item != null) decrementOrRemove.accept(item);
    }
}
