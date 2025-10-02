package dev.lirox.scatter.utils;

import dev.lirox.scatter.configs.LocaleConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Map;

public class TextUtils {
    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static Component locale(String key, Map<String, String> placeholders) {
        return process(LocaleConfig.get(key), placeholders);
    }

    public static Component locale(String key) {
        return process(LocaleConfig.get(key), Map.of());
    }

    public static Component process(String key, Map<String, String> placeholders) {
        String raw = LocaleConfig.get(key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            raw = raw.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return mm.deserialize(raw);
    }
}
