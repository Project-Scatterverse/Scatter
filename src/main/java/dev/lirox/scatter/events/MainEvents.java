package dev.lirox.scatter.events;

import dev.lirox.scatter.Affected;
import dev.lirox.scatter.registries.Registry;
import dev.lirox.scatter.states.Ghost;
import dev.lirox.scatter.states.Meowthpiece;
import dev.lirox.scatter.states.Scatterred;
import dev.lirox.scatter.states.Trapped;
import dev.lirox.scatter.utils.ItemUtils;
import dev.lirox.scatter.utils.PlayerUtils;
import dev.lirox.scatter.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static dev.lirox.scatter.registries.Registry.hitsTillFinalize;

public class MainEvents implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (PlayerUtils.isState(player, Ghost.class)) {
            event.quitMessage(null);
            PlayerUtils.getState(player, Ghost.class).dismount();
        } else if (PlayerUtils.isState(player, Trapped.class)) PlayerUtils.scatter(event.getPlayer(), true);
        else if (PlayerUtils.isState(player, Meowthpiece.class)) event.quitMessage(null);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (PlayerUtils.isState(player, Ghost.class)) {
            event.joinMessage(null);
            PlayerUtils.prepareGhost(player, true);
        } else if (PlayerUtils.isState(player, Meowthpiece.class)) {
            event.joinMessage(null);
            PlayerUtils.prepareMeowthpiece(player, false);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (PlayerUtils.isState(player, Ghost.class)) return;

        Item droppedItem = event.getItemDrop();
        if (!ItemUtils.hasProperty(droppedItem.getItemStack(), Registry.PROP_REVIVER)) return;

        for (Map.Entry<UUID, Scatterred> entry : Affected.map.entrySet()) {
            if (!(entry.getValue() instanceof Ghost ghost)) continue;
            Location deathPos = ghost.death_pos;
            Location deathLocation = new Location(player.getWorld(), deathPos.getX(), deathPos.getY(), deathPos.getZ());

            if (deathLocation.distance(player.getLocation()) > 5) continue;

            Object obj = PlayerUtils.getPlayerData(entry.getKey());
            if (!(obj instanceof Player victim)) {
                player.sendMessage(TextUtils.locale("revive.offline"));
                return;
            }

            PlayerUtils.revive(victim, true);

            victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);
            victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_BELL_RESONATE, 1, 1);
            victim.sendMessage(TextUtils.locale("revive.revived", Map.of("player", player.getName())));
            droppedItem.remove();

            return;
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;
        if (ItemUtils.getFirstScatterItem(killer) == null) return;

        event.setCancelled(true);

        PlayerUtils.trap(victim, killer);

        victim.setHealth(victim.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

        if (hitsTillFinalize <= 0) PlayerUtils.scatter(victim, true);
    }

    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager && (PlayerUtils.isState(damager, Ghost.class) || PlayerUtils.isState(damager, Trapped.class))) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getEntity() instanceof Player victim)) return;

        if (PlayerUtils.isState(victim, Ghost.class)) {
            event.setCancelled(true);
            return;
        }

        PlayerUtils.tag(victim);

        if (!PlayerUtils.isState(victim, Trapped.class)) return;

        Trapped trapped = PlayerUtils.getState(victim, Trapped.class);

        if (!(event.getDamager() instanceof Player damager)) return;

        if (damager.getUniqueId().equals(trapped.killer) && ItemUtils.mainHand(damager).getType().isAir()) {
            PlayerUtils.release(victim);
            return;
        }

        if (ItemUtils.getFirstScatterItem(damager) instanceof ItemStack item) {
            trapped.remainingHits--;
            victim.damage(1);
            victim.setHealth(1 + (victim.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() - 1) * ((double) trapped.remainingHits / hitsTillFinalize));
            damager.sendMessage(TextUtils.locale("scatter.hit", Map.of("hits", String.valueOf(Registry.hitsTillFinalize - trapped.remainingHits), "total", String.valueOf(hitsTillFinalize))));
            damager.getWorld().playSound(damager.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1);
            if (trapped.remainingHits <= 0) {
                PlayerUtils.scatter(victim, true);
                ItemUtils.decrementItem(item);
            }
        }
    }
}
