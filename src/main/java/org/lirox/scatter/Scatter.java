package org.lirox.scatter;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;
import org.lirox.scatter.commands.ScatterCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class Scatter extends JavaPlugin implements Listener {

    public static ConfigManager configManager;
    private final Random rand = new Random();
    public static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = JavaPlugin.getPlugin(this.getClass());
        getServer().getPluginManager().registerEvents(new Events(this), this);
        getCommand("scatter").setExecutor(new ScatterCommand());
        saveDefaultConfig();
        configManager = new ConfigManager(getConfig());
        spawnParticlesLoop();
    }

    @Override
    public void onDisable() {
        configManager.save();
    }

    private void spawnParticlesLoop() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Map.Entry<String, Scatterred> entry : configManager.scatteredPlayers.entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                Scatterred scatterred = entry.getValue();
                Location deathPos = scatterred.pos;

                if (scatterred.state == 2) {
                    if (player != null) {
                        deathPos.getWorld().spawnParticle(Particle.END_ROD, deathPos, 5, 10, 10, 10, 0.05);
                        player.setExp(rand.nextFloat());
                    }
                    deathPos.getWorld().spawnParticle(Particle.DRAGON_BREATH, deathPos, 1, 0.3, 0.3, 0.3, 0.05);
                    deathPos.getWorld().spawnParticle(Particle.TOTEM, deathPos, 1, 0.3, 0.3, 0.3, 0.02);
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

                    for (int i = 0; i < 4; i++) {
                        double angle = Math.toRadians(time / 5.0 + i * 90);
                        double offsetX = radius * Math.cos(angle);
                        double offsetZ = radius * Math.sin(angle);

                        double rotatedY = offsetX * Math.sin(Math.toRadians(45)) + offsetZ * Math.cos(Math.toRadians(45)) + 1;
                        double rotatedX = offsetX * Math.cos(Math.toRadians(45)) - offsetZ * Math.sin(Math.toRadians(45));

                        center.add(rotatedX, rotatedY, offsetZ);
                        player.getWorld().spawnParticle(Particle.REDSTONE, center, 1, new Particle.DustOptions(Color.YELLOW, 1));
                        center.subtract(rotatedX, rotatedY, offsetZ);

                        offsetX = radius * Math.cos(-angle);
                        offsetZ = radius * Math.sin(-angle);

                        rotatedY = offsetX * Math.sin(Math.toRadians(-45)) + offsetZ * Math.cos(Math.toRadians(-45)) + 1;
                        rotatedX = offsetX * Math.cos(Math.toRadians(-45)) - offsetZ * Math.sin(Math.toRadians(-45));

                        center.add(rotatedX, rotatedY, offsetZ);
                        player.getWorld().spawnParticle(Particle.REDSTONE, center, 1, new Particle.DustOptions(Color.YELLOW, 1));
                        center.subtract(rotatedX, rotatedY, offsetZ);
                    }
                }
            }
        }, 0L, 1L);
    }
}
