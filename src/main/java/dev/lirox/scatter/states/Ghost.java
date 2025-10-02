package dev.lirox.scatter.states;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.UUID;

public class Ghost extends Scatterred {
//    public List<String> properties = List.of();
    public final Location death_pos;
    public LivingEntity mount;

    public Ghost(UUID victim, UUID killer, Location death_pos) {
        super(victim, killer);
        this.death_pos = death_pos;
        this.mount = null;
    }

    public void mount(LivingEntity entity) {
        this.mount = entity;
    }

    public void dismount() {
        this.mount = null;
    }
}
