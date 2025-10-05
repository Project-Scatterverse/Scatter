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
        double radius = size.getX();
        double verticalOffset = size.getY();

        Vector[] square = new Vector[4];
        for (int i = 0; i < 4; i++) {
            double a = Math.toRadians(i * 90);
            square[i] = new Vector(radius * Math.cos(a), verticalOffset, radius * Math.sin(a));
        }

        double pitch = Math.toRadians(angle.getX());
        double yaw = Math.toRadians(angle.getY());
        double roll = Math.toRadians(angle.getZ());
        double cosX = Math.cos(pitch), sinX = Math.sin(pitch);
        double cosY = Math.cos(yaw), sinY = Math.sin(yaw);
        double cosZ = Math.cos(roll), sinZ = Math.sin(roll);

        for (int i = 0; i < 4; i++) {
            Vector start = rotate(square[i], cosX, sinX, cosY, sinY, cosZ, sinZ);
            Vector end = rotate(square[(i + 1) % 4], cosX, sinX, cosY, sinY, cosZ, sinZ);

            for (double t = 0; t <= 1; t += 0.1) {
                double x = start.getX() + (end.getX() - start.getX()) * t;
                double y = start.getY() + (end.getY() - start.getY()) * t;
                double z = start.getZ() + (end.getZ() - start.getZ()) * t;

                pos.add(x, y, z);
                pos.getWorld().spawnParticle(Particle.DUST, pos, 1, options);
                pos.subtract(x, y, z);
            }
        }
    }

    private static Vector rotate(Vector v, double cosX, double sinX, double cosY, double sinY, double cosZ, double sinZ) {
        double x = v.getX(), y = v.getY(), z = v.getZ();

        double nx = x * (cosY * cosZ) + y * (cosZ * sinX * sinY - cosX * sinZ) + z * (cosX * cosZ * sinY + sinX * sinZ);
        double ny = x * (cosY * sinZ) + y * (cosX * cosZ + sinX * sinY * sinZ) + z * (cosX * sinY * sinZ - cosZ * sinX);
        double nz = x * (-sinY) + y * (cosY * sinX) + z * (cosX * cosY);

        return new Vector(nx, ny, nz);
    }


    public static void trappedParticles(Player player, int animationTime) {
        float radius = (float) ((animationTime < 20) ? 0.5 + (1 - ((float) animationTime / 20)) : 0.5);
        Location center = player.getLocation();
        center.add(0,1,0); // TODO: move somewhere else
        Vector angle1 = new Vector(0, animationTime / 2, 30);
        Vector angle2 = new Vector(180, -animationTime / 2, -30);
        Vector size = new Vector(.8, 0, 0);

        Particle.DustOptions options = new Particle.DustOptions(Color.YELLOW, .5f);

        particleChain(center, angle1, size, options);
        particleChain(center, angle2, size, options);
    }

    public static void soulParticles(Location deathPos, boolean online) {
        Location pos = deathPos.clone().add(0, 1, 0); // TODO: move somewhere else
        if (online) deathPos.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, pos, 1, 0.3, 0.3, 0.3, 0.02);
        deathPos.getWorld().spawnParticle(Particle.DRAGON_BREATH, pos, 1, 0.3, 0.3, 0.3, 0.05);
        deathPos.getWorld().spawnParticle(Particle.END_ROD, pos, 5, 10, 10, 10, 0.05);
    }

    public static void taggedParticles(Player player, int timeout) {
        Particle.DustOptions options = new Particle.DustOptions(Color.YELLOW, .5f);
        int count = timeout / 15;
        player.getWorld().spawnParticle(Particle.DUST, player.getLocation(), count, 0.3, 0.6, 0.3, 0, options);
    }
}
