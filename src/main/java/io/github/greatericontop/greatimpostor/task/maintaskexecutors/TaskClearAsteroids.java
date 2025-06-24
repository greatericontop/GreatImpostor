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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TaskClearAsteroids extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Clear Asteroids";

    private final Map<UUID, Integer> score = new HashMap<>();
    private final Random random = new Random();

    public TaskClearAsteroids(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.CLEAR_ASTEROIDS;
    }

    @Override
    public void startTask(Player player) {
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        for (int i = 0; i < 5; i++) {
            int slot = random.nextInt(54); // repeats possible but we don't care
            gui.setItem(slot, new ItemStack(Material.BEDROCK, 1));
        }

        score.put(player.getUniqueId(), 0);

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        event.setCancelled(true);
        if (!event.getClickedInventory().equals(event.getView().getTopInventory()))  return;
        Player player = (Player) event.getWhoClicked();

        ItemStack clickedOn = event.getCurrentItem();
        if (clickedOn != null && clickedOn.getType() == Material.BEDROCK) {
            event.getInventory().setItem(event.getSlot(), null);
            int newSlot = random.nextInt(54);
            event.getInventory().setItem(newSlot, new ItemStack(Material.BEDROCK, 1));
            score.put(player.getUniqueId(), score.get(player.getUniqueId()) + 1);
            this.playSuccessSound(player);
            if (score.get(player.getUniqueId()) >= 20) {
                this.taskSuccessful(player);
                player.closeInventory();
            }
        }

    }

}
