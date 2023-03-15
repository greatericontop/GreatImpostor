package io.github.greatericontop.greatimpostor.core;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrewmateProfile extends PlayerProfile {

    public CrewmateProfile(Player player) {
        super(player);
    }

    public void setInventory() {
        Inventory inv = this.getPlayer().getInventory();
        inv.clear();

        ItemStack firstTask = new ItemStack(Material.RED_STAINED_GLASS, 1); // TODO
        ItemMeta im1 = firstTask.getItemMeta();
        im1.displayName(Component.text(tasks.get(0).name())); // TODO
        firstTask.setItemMeta(im1);
        inv.setItem(0, firstTask);

        ItemStack secondTask = new ItemStack(Material.RED_STAINED_GLASS, 1); // TODO
        ItemMeta im2 = secondTask.getItemMeta();
        im2.displayName(Component.text(tasks.get(1).name())); // TODO
        secondTask.setItemMeta(im2);
        inv.setItem(1, secondTask);

        ItemStack thirdTask = new ItemStack(Material.RED_STAINED_GLASS, 1); // TODO
        ItemMeta im3 = thirdTask.getItemMeta();
        im3.displayName(Component.text(tasks.get(2).name())); // TODO
        thirdTask.setItemMeta(im3);
        inv.setItem(2, thirdTask);

    }

}
