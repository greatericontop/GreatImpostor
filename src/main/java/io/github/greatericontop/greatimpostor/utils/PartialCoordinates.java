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
