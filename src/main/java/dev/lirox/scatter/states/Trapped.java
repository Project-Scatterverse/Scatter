package dev.lirox.scatter.states;

import dev.lirox.scatter.Affected;
import dev.lirox.scatter.utils.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

    public void update() {
        this.timeout--;
        if (this.timeout <= 0) PlayerUtils.release(this.victim);
    }
}
