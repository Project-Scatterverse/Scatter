package dev.lirox.scatter;

import dev.lirox.scatter.states.Scatterred;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Affected {
    public static final Map<UUID, Scatterred> map = new HashMap<>();

    private Affected() {}

    public static Map<UUID, Scatterred> get() {
        return map;
    }

    public static void put(UUID key, Scatterred value) {
        map.put(key, value);
    }

    public static void remove(UUID key) {
        map.remove(key);
    }

    public static Scatterred get(UUID key) {
        return map.get(key);
    }
}
