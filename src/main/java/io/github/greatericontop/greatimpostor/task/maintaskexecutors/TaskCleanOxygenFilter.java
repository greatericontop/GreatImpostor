package io.github.greatericontop.greatimpostor.task.maintaskexecutors;

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

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.task.BaseTask;
import io.github.greatericontop.greatimpostor.task.TaskType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TaskCleanOxygenFilter extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Clean Oxygen Filter";

    public TaskCleanOxygenFilter(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.CLEAN_OXYGEN_FILTER;
    }

    @Override
    public void startTask(Player player) {
        Random random = new Random();
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        for (int i = 0; i < 10; i++) {
            int slot = random.nextInt(54);
            gui.setItem(slot, new ItemStack(Material.MOSS_BLOCK, 1));
        }

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        event.setCancelled(true);
        if (!event.getClickedInventory().equals(event.getView().getTopInventory()))  return;
        Player player = (Player) event.getWhoClicked();

        event.getInventory().setItem(event.getSlot(), null);

        if (isCleared(event.getInventory())) {
            this.playSuccessSound(player);
            this.taskSuccessful(player);
            player.closeInventory();
        }

    }

    private boolean isCleared(Inventory inv) {
        for (ItemStack stack : inv.getContents()) {
            if (stack != null && stack.getType() == Material.MOSS_BLOCK) {
                return false;
            }
        }
        return true;
    }

}
