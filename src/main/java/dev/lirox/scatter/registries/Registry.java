package dev.lirox.scatter.registries;

import dev.lirox.scatter.Scatter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.scoreboard.Team;

import java.util.List;

import static dev.lirox.scatter.Scatter.plugin;

public class Registry {
    public static NamespacedKey SCATTER_KEY = new NamespacedKey(plugin, "scatter");
    public static Team noMobPushTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("noMobPush");
    public static List<EntityType> blockedControl = List.of(EntityType.WITHER, EntityType.CREEPER, EntityType.PLAYER, EntityType.SHULKER,
            EntityType.RAVAGER, EntityType.HOGLIN, EntityType.ZOGLIN, EntityType.PIGLIN_BRUTE,
            EntityType.VINDICATOR, EntityType.ELDER_GUARDIAN, EntityType.VILLAGER,
            EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.CAT,
            EntityType.PARROT, EntityType.WOLF, EntityType.EVOKER);
    public final static int hitsTillFinalize = 3;
    public float max_final_hp_mul = .5f;
}
