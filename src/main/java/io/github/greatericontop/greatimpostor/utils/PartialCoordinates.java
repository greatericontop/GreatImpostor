package io.github.greatericontop.greatimpostor.utils;

/*
 * Copyright (C) 2023-present greateric.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty  of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public record PartialCoordinates(double x, double y, double z, double yaw) {

    // Supports old format (x, y, z) or new format to specify yaw (x, y, z, yaw)
    public static PartialCoordinates fromConfigEntry(List<Double> configEntry) {
        if (configEntry.size() == 3) {
            return new PartialCoordinates(configEntry.get(0), configEntry.get(1), configEntry.get(2), 0.0);
        } else if (configEntry.size() == 4) {
            return new PartialCoordinates(configEntry.get(0), configEntry.get(1), configEntry.get(2), configEntry.get(3));
        } else {
            throw new IllegalArgumentException("Config entry must contain 3 or 4 values (x, y, z, optional yaw)");
        }
    }

    public static PartialCoordinates ofLocation(Location loc) {
        return new PartialCoordinates(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw());
    }


    public boolean isClose(PartialCoordinates other) {
        // XZ allows you to click it if you're on top of the block (0.5+0.3=0.8), Y gives some vertical tolerance
        return Math.abs(x - other.x) < 0.8 && Math.abs(y - other.y) < 1.0 && Math.abs(z - other.z) < 0.8;
    }

    public Location teleportLocation(World world) {
        return new Location(world, x, y, z);
    }

}
