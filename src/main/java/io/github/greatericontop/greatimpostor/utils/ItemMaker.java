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
