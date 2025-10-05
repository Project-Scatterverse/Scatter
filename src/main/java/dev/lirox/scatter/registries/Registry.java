package dev.lirox.scatter.registries;

import dev.lirox.scatter.Scatter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class Registry {
    public static final Plugin plugin = JavaPlugin.getPlugin(Scatter.class);
    public static NamespacedKey TYPE_KEY = new NamespacedKey(plugin, "type");
    public static NamespacedKey PROP_KEY = new NamespacedKey(plugin, "properties");
    public static NamespacedKey ANIM_KEY = new NamespacedKey(plugin, "animation");
    public static Team noMobPushTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("noMobPush");
    public static List<EntityType> blockedControl = List.of(EntityType.WITHER, EntityType.CREEPER, EntityType.PLAYER, EntityType.SHULKER,
            EntityType.RAVAGER, EntityType.HOGLIN, EntityType.ZOGLIN, EntityType.PIGLIN_BRUTE,
            EntityType.VINDICATOR, EntityType.ELDER_GUARDIAN, EntityType.VILLAGER,
            EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.CAT,
            EntityType.PARROT, EntityType.WOLF, EntityType.EVOKER);
    public final static int hitsTillFinalize = 3;
    public float max_final_hp_mul = .5f;

    public static String TYPE_SCATTER = "scatter";
    public static String TYPE_BORDER_TRAVELLER = "border_traveller";
    public static String TYPE_VOID_BURY = "void_bury";

    public static String ANIM_IMMEDIATE = "immediate";
    public static String ANIM_CHAINED = "chained";

    public static String PROP_REVIVER = "reviver";
}
