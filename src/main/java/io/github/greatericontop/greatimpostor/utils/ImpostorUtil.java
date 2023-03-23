package io.github.greatericontop.greatimpostor.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ImpostorUtil {
    public static final NamespacedKey DEAD_BODY_KEY = new NamespacedKey("greatimpostor", "dead_body");
    public static final NamespacedKey REPORT_KEY = new NamespacedKey("greatimpostor", "report");

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

}
