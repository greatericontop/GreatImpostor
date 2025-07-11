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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.VoxelShape;

public class ImpostorUtil {
    public static final NamespacedKey DEAD_BODY_KEY = new NamespacedKey("greatimpostor", "dead_body");
    public static final NamespacedKey REPORT_KEY = new NamespacedKey("greatimpostor", "report");
    public static final NamespacedKey FAKE_PLAYER_KEY = new NamespacedKey("greatimpostor", "fake_player");

    public static boolean checkOrthoInvSlots(int a, int b) {
        if (a != b+1 && a != b-1 && a != b+9 && a != b-9)  return false;
        if (a == b+1 && b % 9 == 8)  return false; // going to the right would mean crossing a row
        if (a == b-1 && b % 9 == 0)  return false; // going to the left would mean crossing a row
        return true;
    }

    public static ItemStack reportItemStack() {
        ItemStack report = new ItemStack(Material.GOAT_HORN, 1);
        ItemMeta im = report.getItemMeta();
        im.setDisplayName("§cReport Body §e§lRIGHT CLICK");
        im.getPersistentDataContainer().set(REPORT_KEY, PersistentDataType.INTEGER, 1);
        report.setItemMeta(im);
        return report;
    }

    public static ItemStack commsSabotageTaskDisplayItemStack() {
        ItemStack stack = new ItemStack(Material.REDSTONE, 1);
        ItemMeta im = stack.getItemMeta();
        im.setDisplayName("§c*");
        stack.setItemMeta(im);
        return stack;
    }

    /*
     * For spawning particles.
     * This will start at the current location and go downwards until a collision box (i.e., a block) is found.
     * This searches downwards 1 block at a time and always returns a y-value of `n.1` where n is an integer.
     */
    public static Location forceLocationDownwards(Location loc) {
        int blockX = loc.getBlockX();
        int blockY = loc.getBlockY();
        int blockZ = loc.getBlockZ();
        for (int y = blockY; y >= loc.getWorld().getMinHeight(); y--) {
            VoxelShape currentBlockCollisionShape = new Location(loc.getWorld(), blockX, y, blockZ).getBlock().getCollisionShape();
            BoundingBox bb = new BoundingBox(0, 0.1, 0, 1, 0.101, 1); // voxels are 0,0,0 to 1,1,1
            if (currentBlockCollisionShape.overlaps(bb)) {
                return new Location(loc.getWorld(), loc.getX(), y+1.1, loc.getZ()); // previous good location
            }
        }
        return new Location(loc.getWorld(), loc.getX(), loc.getWorld().getMinHeight()+0.1, loc.getZ());
    }

}
