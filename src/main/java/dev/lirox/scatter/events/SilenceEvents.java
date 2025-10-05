package dev.lirox.scatter.events;

import dev.lirox.scatter.states.Ghost;
import dev.lirox.scatter.states.Trapped;
import dev.lirox.scatter.utils.PlayerUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SilenceEvents implements Listener {
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        if (PlayerUtils.isState(event.getPlayer(), Ghost.class)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        if (PlayerUtils.isState(event.getPlayer(), Ghost.class)) event.message(null);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (PlayerUtils.isState(event.getPlayer(), Ghost.class) && !event.getPlayer().isOp()) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player player && !PlayerUtils.isState(player, null)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (PlayerUtils.isState(event.getPlayer(), Ghost.class) || PlayerUtils.isState(event.getPlayer(), Trapped.class)) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!PlayerUtils.isState(player, Trapped.class)) return;
        Location from = event.getFrom();
        Location to = event.getTo();
        Location loc = from.clone();
        loc.setPitch(to.getPitch());
        loc.setYaw(to.getYaw());
        player.teleport(loc);
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player victim && (PlayerUtils.isState(victim, Ghost.class) || PlayerUtils.isState(victim, Trapped.class))) event.setCancelled(true);
    }
}
