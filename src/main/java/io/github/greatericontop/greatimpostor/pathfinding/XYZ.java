package io.github.greatericontop.greatimpostor.pathfinding;

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
import org.bukkit.Material;
import org.bukkit.World;

public record XYZ(int x, int y, int z) {

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getZ() {
        return z;
    }

    public XYZ add(int x, int z) {
        return new XYZ(this.x + x, this.y, this.z + z);
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    public boolean isEmpty(World world) {
        Material mat = world.getBlockAt(x, y, z).getType();
        return !(mat.isSolid() && mat.isOccluding());
    }


    public boolean equals(Object other) {
        if (other instanceof XYZ o) {
            return this.x == o.x && this.y == o.y && this.z == o.z;
        }
        return false;
    }

    // Collisions should be unlikely if coordinates are within ~1000x1000, and y is always a constant
    public int hashCode() {
        return x + z*1_000 + y*1_000_000;
    }

}
