package io.github.greatericontop.greatimpostor.utils;

import org.bukkit.Location;
import org.bukkit.World;

public record PartialCoordinates(double x, double y, double z) {

    public static PartialCoordinates ofLocation(Location loc) {
        return new PartialCoordinates(loc.getX(), loc.getY(), loc.getZ());
    }

    public boolean isClose(PartialCoordinates other) {
        return Math.abs(x - other.x) < 1.25 && Math.abs(y - other.y) < 1.0 && Math.abs(z - other.z) < 1.25;
    }

    public Location teleportLocation(World world) {
        return new Location(world, x, y, z);
    }

}
