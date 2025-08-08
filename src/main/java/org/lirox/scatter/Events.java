package org.lirox.scatter;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import static org.lirox.scatter.Scatter.*;

public class Events implements Listener {
    private final Plugin plugin;

    public final static int hitsTillFinalize = 3;
    public float max_final_hp_mul = .5f;

//    public ArrayList<Material> offhand_binding_curse = new ArrayList<>();

    public Events(Plugin plugin) {
        this.plugin = plugin;
//        offhand_binding_curse.add(Material.TOTEM_OF_UNDYING);
//        offhand_binding_curse.add(Material.SHIELD);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        // when player drop item check if item si reviver and scattered player is nearby and is online (if ghosts enabled) then revive it
        Player player = event.getPlayer();
        if (PlayerUtils.isScattered(player)) event.setCancelled(true);

        Item droppedItem = event.getItemDrop();
        if (!ItemUtils.isReviver(droppedItem.getItemStack())) return;

        for (Map.Entry<String, Scatterred> entry : configManager.scatteredPlayers.entrySet()) {
            String victimName = entry.getKey();
            Location deathPos = entry.getValue().pos;
            Location deathLocation = new Location(player.getWorld(), deathPos.getX(), deathPos.getY(), deathPos.getZ());

            if (deathLocation.distance(player.getLocation()) <= 5) {
                Player victim = Bukkit.getPlayer(victimName);
                if (victim == null) {
                    player.sendMessage(Component.text("<> This player is offline. Go wake them up or smth.")); // TODO: Move in TextManager
                    return;
                }

                PlayerUtils.revive(victim);

                victim.teleport(deathLocation);
                victim.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);
                victim.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_RESONATE, 1, 1);
                victim.sendMessage(Component.text("<> You have been revived by " + player.getName())); // TODO: Move in TextManager
                droppedItem.remove();

                break;
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // when player dies check if killer has scatter then add victim to scattered with trapped state, restore health, if final hits (constant) are <= 0 then finalize trap
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;
        if (!ItemUtils.hasScatter(killer)) return;

        event.setCancelled(true);

        PlayerUtils.trap(victim, killer);

        victim.setHealth(victim.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

        if (hitsTillFinalize <= 0) finalizeTrap(victim);
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


    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && PlayerUtils.isScatterAffected(damager)) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getEntity() instanceof Player victim)) return;

        if (PlayerUtils.isScattered(victim)) {
            event.setCancelled(true);
            return;
        }

        if (!PlayerUtils.isTrapped(victim)) return;

        Scatterred scatterred = configManager.scatteredPlayers.get(victim.getName());
        event.setCancelled(true);

        if (!(event.getDamager() instanceof Player damager)) return;

        if (damager.getName().equals(scatterred.killer) && damager.getInventory().getItemInMainHand().getType().isAir()) {
            PlayerUtils.release(victim);
            return;
        }

        if (ItemUtils.hasScatter(damager)) {
            scatterred.remainingHits--;
            victim.damage(1);
            victim.setHealth(1 + (victim.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() - 1) * ((double) scatterred.remainingHits / hitsTillFinalize));
            damager.sendMessage(Component.text("<> " + (hitsTillFinalize - scatterred.remainingHits) + "/" + hitsTillFinalize)); // TODO: Move in TextManager
            damager.getWorld().playSound(damager.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1);
            if (scatterred.remainingHits <= 0) finalizeTrap(victim);
        }
    }

    private void finalizeTrap(Player victim) {
        PlayerUtils.scatter(victim, configManager.scatteredPlayers.get(victim.getName()).killer);

        victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        victim.getWorld().spawnParticle(Particle.TOTEM, victim.getLocation(), 50);

        PlayerUtils.dropInventory(victim);
        PlayerUtils.dropEnderChest(victim);

        victim.kick(Component.text("There is no way back... Or is it?")); // TODO: Move in TextManager
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player victim && PlayerUtils.isScatterAffected(victim)) {
            event.setCancelled(true);
        }
    }

    // -------------------- Ghost movement, interaction, messages, etc

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (PlayerUtils.isTrapped(player)) {

            Location from = event.getFrom();
            Location to = event.getTo();

            Location loc = from.clone();
            loc.setPitch(to.getPitch());
            loc.setYaw(to.getYaw());

            if (loc.getY() < to.getY()) loc.setY(to.getY());

            player.teleport(loc);
        } else if (configManager.controlledMobs.containsKey(event.getPlayer().getName())) { // TODO: Move in PlayerUtils : isMounted
            Entity mob = configManager.controlledMobs.get(player.getName()); // TODO: Move in PlayerUtils : getMount
            Location to = event.getTo();
            mob.teleport(to);
            mob.setRotation(to.getYaw(), to.getPitch());
        }
    }



    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (PlayerUtils.isScatterAffected(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!PlayerUtils.isScatterAffected(player)) return;

        event.joinMessage(null);
        player.setGameMode(GameMode.ADVENTURE);
        PlayerUtils.setVisibilityToAllPlayers(player, false);
        player.setCanPickupItems(false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!PlayerUtils.isScatterAffected(player)) return;

        if (PlayerUtils.isScattered(player)) {
            event.quitMessage(null);
            configManager.controlledMobs.remove(player.getName());
        }  // TODO: Move in PlayerUtils : dismount
        if (PlayerUtils.isTrapped(player)) finalizeTrap(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        if (PlayerUtils.isScattered(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        if (PlayerUtils.isScattered(event.getPlayer())) event.message(null);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (PlayerUtils.isScattered(event.getPlayer()) && !event.getPlayer().isOp()) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        LivingEntity mob = (LivingEntity) event.getRightClicked();
        if (PlayerUtils.isScattered(player) && !blockedControl.contains(mob.getType())) {
            configManager.controlledMobs.put(player.getName(), mob);
            player.teleport(mob.getLocation());
            event.setCancelled(true);
        }
    } // TODO: Move in PlayerUtils : mount

    @EventHandler
    public void onPlayerDoSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking() && PlayerUtils.isScattered(player)) {
            configManager.controlledMobs.remove(player.getName());
        }
    } // TODO: Move in PlayerUtils : dismount

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player player && PlayerUtils.isScattered(player)) event.setCancelled(true);
    }

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
