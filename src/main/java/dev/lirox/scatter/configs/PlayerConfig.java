package dev.lirox.scatter.configs;

import dev.lirox.scatter.Affected;
import dev.lirox.scatter.states.Ghost;
import dev.lirox.scatter.states.Meowthpiece;
import dev.lirox.scatter.states.Scatterred;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerConfig {

    private static File folder;

    private PlayerConfig() {}

    public static void init(File dataFolder) {
        folder = new File(dataFolder, "players");
        if (!folder.exists()) folder.mkdirs();
    }

    public static void save(UUID uuid) {
        Scatterred s = Affected.get(uuid);
        if (s == null) return;

        File file = new File(folder, uuid + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        cfg.set("type", s.getClass().getSimpleName());
        cfg.set("victim", s.victim.toString());
        cfg.set("killer", s.killer.toString());

        if (s instanceof Ghost ghost) {
            Location loc = ghost.death_pos;
            cfg.set("death_pos.world", loc.getWorld().getName());
            cfg.set("death_pos.x", loc.getX());
            cfg.set("death_pos.y", loc.getY());
            cfg.set("death_pos.z", loc.getZ());

            if (ghost.mount != null) {
                cfg.set("mount", ghost.mount.getUniqueId().toString());
            } else {
                cfg.set("mount", null);
            }
        }

        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(UUID uuid) {
        File file = new File(folder, uuid + ".yml");
        if (!file.exists()) return;

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String type = cfg.getString("type");
        UUID victim = UUID.fromString(cfg.getString("victim"));
        UUID killer = UUID.fromString(cfg.getString("killer"));

        Scatterred s = null;

        if ("Ghost".equals(type)) {
            String world = cfg.getString("death_pos.world");
            double x = cfg.getDouble("death_pos.x");
            double y = cfg.getDouble("death_pos.y");
            double z = cfg.getDouble("death_pos.z");
            Location loc = new Location(Bukkit.getWorld(world), x, y, z);

            Ghost ghost = new Ghost(victim, killer, loc);

            if (cfg.contains("mount")) {
                String mountId = cfg.getString("mount");
                if (mountId != null) {
                    LivingEntity entity = (LivingEntity) Bukkit.getEntity(UUID.fromString(mountId));
                    ghost.mount(entity);
                }
            }
            s = ghost;
        } else if ("Meowthpiece".equals(type)) {
            s = new Meowthpiece(victim, killer);
        }

        if (s != null) Affected.put(uuid, s);
    }

    public static void saveAll() {
        if (!folder.exists()) return;

        for (File file : folder.listFiles()) {
            String name = file.getName().replace(".yml", "");
            try {
                UUID uuid = UUID.fromString(name);
                if (!Affected.get().containsKey(uuid)) file.delete();
            } catch (Exception ignored) {}
        }

        for (UUID uuid : Affected.get().keySet()) {
            save(uuid);
        }
    }

    public static void loadAll() {
        if (!folder.exists()) return;

        for (File file : folder.listFiles()) {
            String name = file.getName().replace(".yml", "");
            try {
                UUID uuid = UUID.fromString(name);
                load(uuid);
            } catch (Exception ignored) {}
        }
    }
}
