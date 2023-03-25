package io.github.greatericontop.greatimpostor.utils;

import org.bukkit.Location;
import org.bukkit.World;

public record PartialCoordinates(double x, double y, double z) {

    public static PartialCoordinates ofLocation(Location loc) {
        return new PartialCoordinates(loc.getX(), loc.getY(), loc.getZ());
    }

    public boolean isClose(PartialCoordinates other) {
        // XZ allows you to click it if you're on top of the block (0.5+0.3=0.8), Y gives some vertical tolerance
        return Math.abs(x - other.x) < 0.8 && Math.abs(y - other.y) < 1.0 && Math.abs(z - other.z) < 0.8;
    }

    public Location teleportLocation(World world) {
        return new Location(world, x, y, z);
    }

}
