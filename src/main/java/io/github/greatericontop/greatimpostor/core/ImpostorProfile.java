package io.github.greatericontop.greatimpostor.core;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ImpostorProfile extends PlayerProfile {

    public ImpostorProfile(Player player) {
        super(player);
    }

    public boolean isImpostor() {
        return true;
    }

    public void setInventory() {
        Inventory inv = this.getPlayer().getInventory();
        inv.clear();

        inv.setItem(0, tasks.get(0).getDisplayItemStack(subtasksCompletedPerTask[0], "§7Fake A - "));
        inv.setItem(1, tasks.get(1).getDisplayItemStack(subtasksCompletedPerTask[1], "§7Fake B - "));
        inv.setItem(2, tasks.get(2).getDisplayItemStack(subtasksCompletedPerTask[2], "§7Fake C - "));

        ItemStack kill = new ItemStack(Material.NETHERITE_SWORD, 1);
        ItemMeta im = kill.getItemMeta();
        im.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        im.setDisplayName("§4KILL");
        kill.setItemMeta(im);
        inv.setItem(4, kill);

    }

}
