package dev.lirox.scatter.events;

import dev.lirox.scatter.Affected;
import dev.lirox.scatter.states.Ghost;
import dev.lirox.scatter.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import static dev.lirox.scatter.registries.Registry.blockedControl;

public class GhostEvents implements Listener {
    @EventHandler
    public void onPlayerDoSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking() && PlayerUtils.isState(player, Ghost.class)) PlayerUtils.getState(player, Ghost.class).dismount();
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        LivingEntity mob = (LivingEntity) event.getRightClicked();
        if (!PlayerUtils.isState(player, Ghost.class) || blockedControl.contains(mob.getType())) return;
        PlayerUtils.getState(player, Ghost.class).mount(mob);
        player.teleport(mob.getLocation());
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!PlayerUtils.isState(player, Ghost.class)) return;
        Ghost ghost = PlayerUtils.getState(player, Ghost.class);
        if (ghost.mount == null) return;
        Location to = event.getTo();
        ghost.mount.teleport(to);
        ghost.mount.setRotation(to.getYaw(), to.getPitch());
    }
}
