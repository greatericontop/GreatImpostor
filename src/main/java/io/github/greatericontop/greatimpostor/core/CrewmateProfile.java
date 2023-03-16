package io.github.greatericontop.greatimpostor.core;

import net.kyori.adventure.text.Component;
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

        ItemStack firstTask = new ItemStack(tasks.get(0).getDisplayMaterial(subtasksCompletedPerTask[0]), 1);
        ItemMeta im1 = firstTask.getItemMeta();
        im1.displayName(Component.text(tasks.get(0).getDisplayName()));
        firstTask.setItemMeta(im1);
        inv.setItem(0, firstTask);

        ItemStack secondTask = new ItemStack(tasks.get(1).getDisplayMaterial(subtasksCompletedPerTask[1]), 1);
        ItemMeta im2 = secondTask.getItemMeta();
        im2.displayName(Component.text(tasks.get(1).getDisplayName()));
        secondTask.setItemMeta(im2);
        inv.setItem(1, secondTask);

        ItemStack thirdTask = new ItemStack(tasks.get(2).getDisplayMaterial(subtasksCompletedPerTask[2]), 1);
        ItemMeta im3 = thirdTask.getItemMeta();
        im3.displayName(Component.text(tasks.get(2).getDisplayName()));
        thirdTask.setItemMeta(im3);
        inv.setItem(2, thirdTask);

    }

}
