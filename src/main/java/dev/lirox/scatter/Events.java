package dev.lirox.scatter;
// TODO: remove completely
import dev.lirox.scatter.states.Scatterred;
import dev.lirox.scatter.utils.ItemUtils;
import dev.lirox.scatter.utils.PlayerUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;

import java.util.Map;

import static dev.lirox.scatter.Scatter.*;

public class Events implements Listener {
    private final Plugin plugin;


//    public ArrayList<Material> offhand_binding_curse = new ArrayList<>();

    public Events(Plugin plugin) {
        this.plugin = plugin;
//        offhand_binding_curse.add(Material.TOTEM_OF_UNDYING);
//        offhand_binding_curse.add(Material.SHIELD);
    }




//
//    @EventHandler
//    public void onEntityResurrect(EntityResurrectEvent event) {
//        if (event.getEntity() instanceof Player victim) {
//            if (hasScatterOffHand(victim) && victim.getInventory().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING)) {
//                victim.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
//                finalizeTrap(victim);
//            }
//        }
//    }




    // -------------------- Ghost movement, interaction, messages, etc







//    // -------------------------------------- Binding Curse
//    @EventHandler
//    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
//        ItemStack offHand = event.getOffHandItem();
//        if (Scatter.isScatter(offHand) && offHand.containsEnchantment(Enchantment.BINDING_CURSE) && offhand_binding_curse.contains(offHand.getType()) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler
//    public void onInventoryClick(InventoryClickEvent event) {
//        if (event.getWhoClicked() instanceof Player player) {
//            ItemStack offHand = player.getInventory().getItemInOffHand();
//            if (Scatter.isScatter(offHand) && offHand.containsEnchantment(Enchantment.BINDING_CURSE) && event.getSlot() == 40 && !player.getGameMode().equals(GameMode.CREATIVE)) {
//                event.setCancelled(true);
//            }
//        }
//    }
//
//    @EventHandler
//    public void onItemDrop(PlayerDropItemEvent event) {
//        ItemStack item = event.getItemDrop().getItemStack();
//        if (Scatter.isScatter(item) && item.containsEnchantment(Enchantment.BINDING_CURSE) && offhand_binding_curse.contains(item.getType()) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
//            event.setCancelled(true);
//        }
//    } idk waht to do with this
}
