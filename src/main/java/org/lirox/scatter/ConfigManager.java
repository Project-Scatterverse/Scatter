package org.lirox.scatter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final FileConfiguration config;
    public final Map<String, Scatterred> scatteredPlayers = new HashMap<>();

    public ConfigManager(FileConfiguration config) {
        this.config = config;
    }

    public void load() {
        if (config.isConfigurationSection("players")) {
            for (String playerName : config.getConfigurationSection("players").getKeys(false)) {
                double x = config.getDouble("players." + playerName + ".death_pos.x", 0);
                double y = config.getDouble("players." + playerName + ".death_pos.y", 0);
                double z = config.getDouble("players." + playerName + ".death_pos.z", 0);
                String worldName = config.getString("players." + playerName + ".death_world", "");

                if (Bukkit.getWorld(worldName) != null && (x != 0 || y != 0 || z != 0)) {
                    scatteredPlayers.put(playerName, new Scatterred(playerName, config.getString("players." + playerName + ".killer", null), 0, 2, 0, 0, new Location(Bukkit.getWorld(worldName), x, y, z)));
                }
            }
        }
    }

    public void save() {
        for (Map.Entry<String, Scatterred> entry : scatteredPlayers.entrySet()) {
            String playerName = entry.getKey();
            config.set("players." + playerName + ".killer", entry.getValue().killer);

            Location deathPos = entry.getValue().pos;
            if (deathPos != null) {
                config.set("players." + playerName + ".death_pos.x", deathPos.getX());
                config.set("players." + playerName + ".death_pos.y", deathPos.getY());
                config.set("players." + playerName + ".death_pos.z", deathPos.getZ());
                config.set("players." + playerName + ".death_world", deathPos.getWorld().getName());
            }
        }
    }
}
