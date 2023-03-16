package io.github.greatericontop.greatimpostor.core;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CrewmateProfile extends PlayerProfile {

    public CrewmateProfile(Player player) {
        super(player);
    }

    public void setInventory() {
        Inventory inv = this.getPlayer().getInventory();
        inv.clear();

        inv.setItem(0, tasks.get(0).getDisplayItemStack(subtasksCompletedPerTask[0], "§7A - "));
        inv.setItem(1, tasks.get(1).getDisplayItemStack(subtasksCompletedPerTask[1], "§7B - "));
        inv.setItem(2, tasks.get(2).getDisplayItemStack(subtasksCompletedPerTask[2], "§7C - "));

    }

}
