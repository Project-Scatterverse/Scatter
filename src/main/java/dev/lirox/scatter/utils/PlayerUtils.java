package dev.lirox.scatter.utils;

import dev.lirox.scatter.Affected;
import dev.lirox.scatter.states.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import dev.lirox.scatter.registries.Registry;
import org.bukkit.util.Vector;

import java.util.UUID;

public class PlayerUtils {
    public static Object getPlayerData(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        return (p != null) ? p : Bukkit.getOfflinePlayer(uuid);
    }

    public static Object getPlayerData(String name) {
        Player p = Bukkit.getPlayer(name);
        return (p != null) ? p : Bukkit.getOfflinePlayer(name);
    }

    // Visibility
    public static void setVisibilityToPlayer(Player player, Player seeker, boolean visibility) {
        if (visibility) seeker.showPlayer(Registry.plugin, player);
        else seeker.hidePlayer(Registry.plugin, player);
    }

    public static void setVisibilityToAllPlayers(Player player, boolean visibility) {
        for (Player seeker : Bukkit.getOnlinePlayers()) {
            setVisibilityToPlayer(player, seeker, visibility);
        }
    }

    public static void setVisibilityToAllPlayersCornerVision(Player player, boolean visibility, int angle) {
        Vector dir = player.getLocation().getDirection().normalize();
        double cosAngle = Math.cos(Math.toRadians(angle));

        for (Player other : player.getWorld().getPlayers()) {
            if (other == player) continue;
            if (player.getLocation().distanceSquared(other.getLocation()) > 250 * 250) continue;
            if (isState(other, Meowthpiece.class)) {
                setVisibilityToPlayer(other, player, true);
                continue;
            }

            boolean inVision = dir.dot(other.getLocation().toVector().subtract(player.getLocation().toVector()).normalize()) >= cosAngle;

            setVisibilityToPlayer(other, player, visibility && !inVision);
        }
    }



    // State checking
    public static boolean isState(UUID uuid, Class<? extends Scatterred> type) {
        Scatterred s = Affected.get(uuid);
        if (type == null) return s == null;
        return type.isInstance(s);
    }


    public static boolean isState(Player player, Class<? extends Scatterred> type) {
        return isState(player.getUniqueId(), type);
    }

    public static <T extends Scatterred> T getState(UUID uuid, Class<T> type) {
        Scatterred s = Affected.get(uuid);
        if (type.isInstance(s)) return type.cast(s);
        return null;
    }

    public static <T extends Scatterred> T getState(Player player, Class<T> type) {
        return getState(player.getUniqueId(), type);
    }

    public static boolean isRegular(UUID uuid) {
        return isState(uuid, null) || isState(uuid, Tagged.class);
    }

    public static boolean isRegular(Player player) {
        return isRegular(player.getUniqueId());
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
    public static void prepareRegular(Player player, boolean setHpSaturation) {
        player.setAllowFlight(false);
        player.setFlying(false);
        PlayerUtils.setVisibilityToAllPlayers(player, true);
        player.setGameMode(GameMode.SURVIVAL);
        player.setCanPickupItems(true);
        Registry.noMobPushTeam.removePlayer(player);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        if (setHpSaturation) {
            player.setHealth(10);
            player.setSaturation(10);
            player.setFoodLevel(10);
        }
    }

    public static void prepareGhost(Player player, boolean setHpSaturation) {
        PlayerUtils.setVisibilityToAllPlayers(player, false);
        player.setGameMode(GameMode.ADVENTURE);
        player.setCanPickupItems(false);
        Registry.noMobPushTeam.addPlayer(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        if (setHpSaturation) {
            player.setHealth(1);
            player.setSaturation(0);
            player.setFoodLevel(0);
        }
    }

    public static void prepareMeowthpiece(Player player, boolean setHpSaturation) {
        prepareRegular(player, false);
        PlayerUtils.setVisibilityToAllPlayers(player, false);
        if (setHpSaturation) {
            player.setHealth(20);
            player.setSaturation(20);
            player.setFoodLevel(20);
        }
    }

    public static void scatter(UUID victim, UUID killer, boolean kick) {
        Object obj = getPlayerData(victim);
        if (obj instanceof Player player) {
            Affected.put(victim, new Ghost(victim, killer, player.getLocation()));
            prepareGhost(player, true);
            if (kick) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 50);

                PlayerUtils.dropInventory(player);
                PlayerUtils.dropEnderChest(player);

                player.kick(TextUtils.locale("scatter.kick"));
            }
        } else if (obj instanceof OfflinePlayer player) {
            Affected.put(victim, new Ghost(victim, killer, player.getLocation()));
        }
    }

    public static void scatter(UUID uuid, boolean kick) {
        scatter(uuid, uuid, kick);
    }

    public static void scatter(Player victim, boolean kick) {
        scatter(victim.getUniqueId(), victim.getUniqueId(), kick);
    }

    public static void revive(UUID uuid) {
        revive(uuid, false);
    }
    public static void revive(UUID uuid, boolean teleport) {
        Object obj = getPlayerData(uuid);
        if (!(obj instanceof Player player && Affected.get(uuid) instanceof Ghost ghost)) return;
        prepareRegular(player, true);
        if (teleport) player.teleport(ghost.death_pos);
        Affected.remove(uuid);
    }
    public static void revive(Player victim) {
        revive(victim.getUniqueId(), false);
    }

    public static void revive(Player victim, boolean teleport) {
        revive(victim.getUniqueId(), teleport);
    }

    // Trapping and releasing
    public static void trap(UUID victim, UUID killer) {
        if (!(getPlayerData(victim) instanceof Player player)) return;
        if (!isState(victim, null)) return;
        Affected.put(victim, new Trapped(victim, killer, 3, 0, 2400));
    }

    public static void trap(Player victim, Player killer) {
        trap(victim.getUniqueId(), killer.getUniqueId());
    }

    public static void trap(Player victim, UUID killer) {
        trap(victim.getUniqueId(), killer);
    }

    public static void trap(UUID victim, Player killer) {
        trap(victim, killer.getUniqueId());
    }

    public static void trap(Player victim) {
        trap(victim.getUniqueId(), victim.getUniqueId());
    }

    public static void trap(UUID uuid) {
        trap(uuid, uuid);
    }

    public static boolean release(UUID uuid) {
        if (!isState(uuid, Trapped.class)) return false;
        Affected.remove(uuid);
        return true;
    }

    public static void release(Player victim, boolean prepare) {
        if (release(victim.getUniqueId())) {
            if (prepare) prepareRegular(victim, true);
        }
    }

    public static void release(Player victim) {
        release(victim.getUniqueId());
    }

    public static void meowthpiecefy(UUID uuid) {
        Affected.put(uuid, new Meowthpiece(uuid, uuid));
    }

    public static void meowthpiecefy(Player victim) {
        prepareMeowthpiece(victim, true);
        meowthpiecefy(victim.getUniqueId());
    }

    public static boolean demeowthpiecefy(UUID uuid) {
        if (isState(uuid, Meowthpiece.class)) {
            Affected.remove(uuid);
            return true;
        }
        return false;
    }

    public static void demeowthpiecefy(Player victim) {
        if (demeowthpiecefy(victim.getUniqueId())) {
            prepareRegular(victim, true);
        }
    }

    public static void tag(UUID uuid) {
        if (isRegular(uuid)) Affected.put(uuid, new Tagged(uuid, uuid, 200));
    }

    public static void tag(Player victim) {
        tag(victim.getUniqueId());
    }
}
