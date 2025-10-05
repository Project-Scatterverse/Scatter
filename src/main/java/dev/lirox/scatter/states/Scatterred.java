package dev.lirox.scatter.states;

import dev.lirox.scatter.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import java.util.UUID;

public abstract class Scatterred {
    public final UUID victim;
    public final UUID killer;

    public Scatterred(UUID victim, UUID killer) {
        this.victim = victim;
        this.killer = killer;
    }

    public void update(Player player) {

    }

    public Object getVictim() {
        return PlayerUtils.getPlayerData(this.victim);
    }

    public Object getKiller() {
        return PlayerUtils.getPlayerData(this.killer);
    }
}
