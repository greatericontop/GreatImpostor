package io.github.greatericontop.greatimpostor.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class ItemMaker {

    public static ItemStack createStack(Material mat, int amount, String name, @Nullable String... lore) {
        ItemStack stack = new ItemStack(mat, amount);
        stack.getItemMeta().displayName(Component.text(name));
        if (lore != null && lore.length > 0) {
            stack.getItemMeta().setLore(java.util.Arrays.asList(lore));
        }
        return stack;
    }

}
