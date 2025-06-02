package io.github.greatericontop.greatimpostor.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import javax.annotation.Nullable;

public class ItemMaker {

    public static ItemStack createStack(Material mat, int amount, String name, @Nullable String... lore) {
        ItemStack stack = new ItemStack(mat, amount);
        ItemMeta im = stack.getItemMeta();
        im.displayName(Component.text(name));
        if (lore != null && lore.length > 0) {
            im.setLore(java.util.Arrays.asList(lore));
        }
        stack.setItemMeta(im);
        return stack;
    }

    public static ItemStack createLeatherArmor(Material mat, int color, String name) {
        ItemStack stack = new ItemStack(mat, 1);
        LeatherArmorMeta im = (LeatherArmorMeta) stack.getItemMeta();
        im.displayName(Component.text(name));
        im.setColor(Color.fromRGB(color));
        im.setUnbreakable(true);
        im.addEnchant(Enchantment.BINDING_CURSE, 10, true);
        stack.setItemMeta(im);
        return stack;
    }

}
