package org.lirox.scatter;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.lirox.scatter.commands.GhostCommand;
import org.lirox.scatter.commands.ScatterCommand;
import org.lirox.scatter.commands.TrapCommand;

import java.util.*;

public final class Scatter extends JavaPlugin implements Listener {

    public static ConfigManager configManager;
    private final Random rand = new Random();
    public static Scatter plugin;
    public static NamespacedKey SCATTER_KEY;
    public static Team noMobPushTeam;
    public static List<EntityType> blockedControl;

    @Override
    public void onEnable() {
        blockedControl = List.of(EntityType.WITHER, EntityType.CREEPER, EntityType.PLAYER, EntityType.SHULKER,
                                 EntityType.RAVAGER, EntityType.HOGLIN, EntityType.ZOGLIN, EntityType.PIGLIN_BRUTE,
                                 EntityType.VINDICATOR, EntityType.ELDER_GUARDIAN, EntityType.VILLAGER,
                                 EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.CAT,
                                 EntityType.PARROT, EntityType.WOLF, EntityType.EVOKER);

        getServer().getPluginManager().registerEvents(new Events(this), this);
        getCommand("scatter").setExecutor(new ScatterCommand());
        getCommand("trap").setExecutor(new TrapCommand());
        getCommand("ghost").setExecutor(new GhostCommand());

        plugin = getPlugin(this.getClass());
        SCATTER_KEY = new NamespacedKey(plugin, "scatter");

        saveDefaultConfig();
        configManager = new ConfigManager(getConfig());
        configManager.load();
        spawnParticlesLoop();


        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        noMobPushTeam = scoreboard.getTeam("noMobPush");
        if (noMobPushTeam == null) {
            noMobPushTeam = scoreboard.registerNewTeam("noMobPush");
            noMobPushTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
    }

    @Override
    public void onDisable() {
        configManager.save();
    }

    private void spawnParticlesLoop() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Map.Entry<String, Entity> entry : configManager.controlledMobs.entrySet()) {
                entry.getValue().setVelocity(entry.getValue().getVelocity().zero());
            } // TODO: move somewhere else
            for (Map.Entry<String, Scatterred> entry : configManager.scatteredPlayers.entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                Scatterred scatterred = entry.getValue();
                Location deathPos = scatterred.pos;

                if (scatterred.state == 2) {
                    Location pos = deathPos.clone().add(0, 1, 0);
                    if (player != null) {
                        deathPos.getWorld().spawnParticle(Particle.TOTEM, pos, 1, 0.3, 0.3, 0.3, 0.02);
                        player.setExp(rand.nextFloat());
                    }
                    deathPos.getWorld().spawnParticle(Particle.DRAGON_BREATH, pos, 1, 0.3, 0.3, 0.3, 0.05);
                    deathPos.getWorld().spawnParticle(Particle.END_ROD, pos, 5, 10, 10, 10, 0.05);
                } else if (scatterred.state == 1) {
                    if (player == null) continue;

                    scatterred.animationTime++;
                    scatterred.timeout--;
                    if (scatterred.timeout <= 0) {
                        configManager.scatteredPlayers.remove(scatterred.victim);
                        continue;
                    }

                    int time = scatterred.animationTime;
                    float radius = (float) ((time < 20) ? 0.5 + (1 - ((float) time / 20)) : 0.5);
                    Location center = player.getLocation();
                    Vector angle1 = new Vector(0, time / 5.0, 0);
                    Vector angle2 = new Vector(180, -time / 5.0, 0);
                    Vector size = new Vector(radius, 1, 0);

                    Particle.DustOptions options = new Particle.DustOptions(Color.YELLOW, 1);

                    particleChain(center, angle1, size, options);
                    particleChain(center, angle2, size, options);

                }
            }
        }, 0L, 1L);
    }

    public Vector lerp(Vector start, Vector end, double t) {
        double x = start.getX() + (end.getX() - start.getX()) * t;
        double y = start.getY() + (end.getY() - start.getY()) * t;
        double z = start.getZ() + (end.getZ() - start.getZ()) * t;
        return new Vector(x, y, z);
    }

    private void particleChain(Location pos, Vector angle, Vector size, Particle.DustOptions options) {
        float radius = (float) size.getX();
        double verticalOffset = size.getY();
        double baseAngle = Math.toRadians(angle.getY());

        Vector[] square = new Vector[4];

        for (int i = 0; i < 4; i++) {
            double angleOffset = baseAngle + Math.toRadians(i * 90);
            double offsetX = radius * Math.cos(angleOffset);
            double offsetZ = radius * Math.sin(angleOffset);

            double rotatedY = offsetX * Math.sin(baseAngle) + offsetZ * Math.cos(baseAngle) + verticalOffset;
            double rotatedX = offsetX * Math.cos(baseAngle) - offsetZ * Math.sin(baseAngle);

            square[i] = new Vector(rotatedX, rotatedY, offsetZ);
        }

        for (int i = 0; i < 4; i++) {
            Vector start = square[i];
            Vector end = square[(i + 1) % 4];

            for (double t = 0; t <= 1; t += 0.1) {
                Vector point = lerp(start, end, t);
                pos.add(point);
                pos.getWorld().spawnParticle(Particle.REDSTONE, pos, 1, options);
                pos.subtract(point);
            }
        }
    }

}
