package dev.lirox.scatter.utils;

import dev.lirox.scatter.states.Trapped;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ParticleUtils {
    public static Vector lerp(Vector start, Vector end, double t) {
        double x = start.getX() + (end.getX() - start.getX()) * t;
        double y = start.getY() + (end.getY() - start.getY()) * t;
        double z = start.getZ() + (end.getZ() - start.getZ()) * t;
        return new Vector(x, y, z);
    }

    public static void particleChain(Location pos, Vector angle, Vector size, Particle.DustOptions options) {
        float radius = (float) size.getX();
        double verticalOffset = size.getY();
        double baseAngle = Math.toRadians(angle.getY());

        Vector[] square = new Vector[4];

        for (int i = 0; i < 4; i++) {
            double angleOffset = baseAngle + Math.toRadians(i * 90);
            double offsetX = radius * Math.cos(angleOffset);
            double offsetZ = radius * Math.sin(angleOffset);

            double rotatedY = offsetX * Math.sin(baseAngle) + offsetZ * Math.cos(baseAngle) + verticalOffset;
            double rotatedX = offsetX * Math.cos(baseAngle) - offsetZ * Math.sin(baseAngle);

            square[i] = new Vector(rotatedX, rotatedY, offsetZ);
        }

        for (int i = 0; i < 4; i++) {
            Vector start = square[i];
            Vector end = square[(i + 1) % 4];

            for (double t = 0; t <= 1; t += 0.1) {
                Vector point = lerp(start, end, t);
                pos.add(point);
                pos.getWorld().spawnParticle(Particle.DUST, pos, 1, options);
                pos.subtract(point);
            }
        }
    }

    public static void trappedParticles(Player player, int animationTime) {
        float radius = (float) ((animationTime < 20) ? 0.5 + (1 - ((float) animationTime / 20)) : 0.5);
        Location center = player.getLocation();
        Vector angle1 = new Vector(0, animationTime / 5.0, 0);
        Vector angle2 = new Vector(180, -animationTime / 5.0, 0);
        Vector size = new Vector(radius, 1, 0);

        Particle.DustOptions options = new Particle.DustOptions(Color.YELLOW, 1);

        particleChain(center, angle1, size, options);
        particleChain(center, angle2, size, options);
    }

    public static void soulParticles(Location deathPos, boolean online) {
        Location pos = deathPos.clone().add(0, 1, 0);
        if (online) deathPos.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, pos, 1, 0.3, 0.3, 0.3, 0.02);
        deathPos.getWorld().spawnParticle(Particle.DRAGON_BREATH, pos, 1, 0.3, 0.3, 0.3, 0.05);
        deathPos.getWorld().spawnParticle(Particle.END_ROD, pos, 5, 10, 10, 10, 0.05);
    }
}
