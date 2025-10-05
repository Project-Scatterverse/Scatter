package dev.lirox.scatter.states;

import dev.lirox.scatter.utils.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Tagged extends Scatterred {
    public int timeout;

    public Tagged(UUID victim, UUID killer, int timeout) {
        super(victim, killer);
        this.timeout = timeout;
    }

    public void update(Player player) {
        this.timeout--;
        if (this.timeout <= 0) PlayerUtils.release(player, true);
    }
}
