package dev.lirox.scatter.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LocaleConfig {

    private static File file;
    private static FileConfiguration config;
    private static final Map<String, String> messages = new HashMap<>();

    private LocaleConfig() {}

    public static void init(File dataFolder) {
        file = new File(dataFolder, "locale.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reload();
    }

    public static String get(String key) {
        if (!messages.containsKey(key)) return key;
        return messages.get(key);
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        messages.clear();
        for (String key : config.getKeys(true)) {
            messages.put(key, config.getString(key));
        }
    }
}
