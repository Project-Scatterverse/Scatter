package dev.lirox.scatter;

import dev.lirox.scatter.commands.GhostCommand;
import dev.lirox.scatter.commands.ScatterCommand;
import dev.lirox.scatter.commands.StateCommand;
import dev.lirox.scatter.commands.TrapCommand;
import dev.lirox.scatter.configs.LocaleConfig;
import dev.lirox.scatter.configs.PlayerConfig;
import dev.lirox.scatter.events.GhostEvents;
import dev.lirox.scatter.events.MainEvents;
import dev.lirox.scatter.events.SilenceEvents;
import dev.lirox.scatter.states.*;
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
        saveDefaultConfig();
        PlayerConfig.init(getDataFolder());
        PlayerConfig.loadAll();
        LocaleConfig.init(getDataFolder());
        spawnParticlesLoop();

        getServer().getPluginManager().registerEvents(new GhostEvents(), this);
        getServer().getPluginManager().registerEvents(new SilenceEvents(), this);
        getServer().getPluginManager().registerEvents(new MainEvents(), this);
        getCommand("scatter").setExecutor(new ScatterCommand());
        getCommand("ghost").setExecutor(new GhostCommand());
        getCommand("trap").setExecutor(new TrapCommand());
        getCommand("state").setExecutor(new StateCommand());

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
                Scatterred state = entry.getValue();
                Object obj = PlayerUtils.getPlayerData(state.victim);
                Player player = obj instanceof Player p ? p : null;

                if (player != null) state.update(player);

                switch (state) {
                    case Trapped trapped -> {
                        if (player != null) ParticleUtils.trappedParticles(player, trapped.animationTime);
                    }
                    case Tagged tagged -> {
                        if (player != null) ParticleUtils.taggedParticles(player, tagged.timeout);
                    }
                    case Ghost ghost -> {
                        if (player != null) {
                            if (ghost.mount != null) ghost.mount.teleport(player);
                            player.setExp(rand.nextFloat());
                            ParticleUtils.soulParticles(ghost.death_pos, true);
                        } else {
                            ParticleUtils.soulParticles(ghost.death_pos, false);
                        }
                    }
                    case Meowthpiece meowthpiece -> {
                        if (player != null) PlayerUtils.setVisibilityToAllPlayersCornerVision(player, true, 30);
                    }
                    default -> {
                    }
                }
            }
        }, 0L, 1L);
    }
}
