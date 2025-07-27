package org.lirox.scatter;

import org.bukkit.Location;

public class Scatterred {
    public final String victim;
    public final String killer;
    public int remainingHits;
    public int state; // 1 - trapped, 2 - ghost/banned
    public int animationTime;
    public int timeout;
    public Location pos;


    public Scatterred(String victim, String killer, int remainingHits, int state, int animationTime, int timeout, Location pos) {
        this.victim = victim;
        this.killer = killer;
        this.remainingHits = remainingHits;
        this.state = state;
        this.animationTime = animationTime;
        this.timeout = timeout;
        this.pos = pos;
    }
}
