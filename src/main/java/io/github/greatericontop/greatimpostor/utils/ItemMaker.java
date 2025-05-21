package io.github.greatericontop.greatimpostor.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

}
