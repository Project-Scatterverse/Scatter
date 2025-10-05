package dev.lirox.scatter.states;

import dev.lirox.scatter.Affected;
import dev.lirox.scatter.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Trapped extends Scatterred {
    public int remainingHits;
    public int animationTime;
    public int timeout;

    public Trapped(UUID victim, UUID killer, int remainingHits, int animationTime, int timeout) {
        super(victim, killer);
        this.remainingHits = remainingHits;
        this.animationTime = animationTime;
        this.timeout = timeout;
    }

    public void update(Player player) {
        this.timeout--;
        this.animationTime++;
//        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20, 0, false, false)); doesnt work
//        player.setFallDistance(0); doesnt work
        player.setAllowFlight(true);
        player.setFlying(true); // optional
        if (this.timeout <= 0) PlayerUtils.release(player, true);
    }
}
