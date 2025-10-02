package dev.lirox.scatter;

import dev.lirox.scatter.commands.GhostCommand;
import dev.lirox.scatter.commands.ScatterCommand;
import dev.lirox.scatter.commands.TrapCommand;
import dev.lirox.scatter.configs.LocaleConfig;
import dev.lirox.scatter.configs.PlayerConfig;
import dev.lirox.scatter.states.Ghost;
import dev.lirox.scatter.states.Scatterred;
import dev.lirox.scatter.states.Trapped;
import dev.lirox.scatter.utils.ParticleUtils;
import dev.lirox.scatter.utils.PlayerUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;

import static dev.lirox.scatter.registries.Registry.noMobPushTeam;

public final class Scatter extends JavaPlugin implements Listener {

    public static Scatter plugin;
    private final Random rand = new Random();


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(this), this);
        getCommand("scatter").setExecutor(new ScatterCommand());
        getCommand("ghost").setExecutor(new GhostCommand());
        getCommand("trap").setExecutor(new TrapCommand());

        plugin = getPlugin(this.getClass());

        saveDefaultConfig();
        PlayerConfig.init(plugin.getDataFolder());
        PlayerConfig.loadAll();
        LocaleConfig.init(plugin.getDataFolder());
        spawnParticlesLoop();

        if (noMobPushTeam == null) {
            noMobPushTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("noMobPush");
            noMobPushTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
    }

    @Override
    public void onDisable() {
        PlayerConfig.saveAll();
    }

    private void spawnParticlesLoop() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Map.Entry<UUID, Scatterred> entry : Affected.map.entrySet()) {
                entry.getValue().update();
                if (entry.getValue() instanceof Trapped trapped) {
                    Object obj = PlayerUtils.getPlayerData(trapped.victim);
                    if (!(obj instanceof Player player)) continue;
                    ParticleUtils.trappedParticles(player, trapped.animationTime);
                } else if (entry.getValue() instanceof Ghost ghost) {
                    Object obj = PlayerUtils.getPlayerData(ghost.victim);
                    if (!(obj instanceof Player player)) {
                        ParticleUtils.soulParticles(ghost.death_pos, false);
                        continue;
                    }
                    if (ghost.mount != null) ghost.mount.teleport(player);
                    player.setExp(rand.nextFloat()); // TODO: move somewhere else
                    ParticleUtils.soulParticles(ghost.death_pos, true);
                }
            }
        }, 0L, 1L);
    }



}
