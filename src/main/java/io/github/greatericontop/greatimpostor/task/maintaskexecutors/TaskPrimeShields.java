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
import io.github.greatericontop.greatimpostor.utils.ItemMaker;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class TaskPrimeShields extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Prime Shields";

    private static final Material GOOD_MAT = Material.LIME_CONCRETE;
    private static final int TOPLEFT = 3;
    private static final int TOP = 4;
    private static final int TOPRIGHT = 5;
    private static final int LEFT = 12;
    private static final int MIDDLE = 13;
    private static final int RIGHT = 14;
    private static final int BOTTOMLEFT = 21;
    private static final int BOTTOM = 22;
    private static final int BOTTOMRIGHT = 23;
    private static final int[] ALL_POSITIONS = {TOPLEFT, TOP, TOPRIGHT, LEFT, MIDDLE, RIGHT, BOTTOMLEFT, BOTTOM, BOTTOMRIGHT};
    private static final Map<Integer, List<Integer>> SURROUNDINGS = Map.of(
        TOPLEFT, List.of(TOPLEFT, TOP, LEFT),
        TOP, List.of(TOPLEFT, TOP, TOPRIGHT, MIDDLE),
        TOPRIGHT, List.of(TOP, TOPRIGHT, RIGHT),
        LEFT, List.of(TOPLEFT, LEFT, MIDDLE, BOTTOMLEFT),
        MIDDLE, List.of(TOP, LEFT, MIDDLE, RIGHT, BOTTOM),
        RIGHT, List.of(TOPRIGHT, MIDDLE, RIGHT, BOTTOMRIGHT),
        BOTTOMLEFT, List.of(LEFT, BOTTOMLEFT, BOTTOM),
        BOTTOM, List.of(MIDDLE, BOTTOMLEFT, BOTTOM, BOTTOMRIGHT),
        BOTTOMRIGHT, List.of(RIGHT, BOTTOM, BOTTOMRIGHT)
    );

    public TaskPrimeShields(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.PRIME_SHIELDS;
    }

    @Override
    public void startTask(Player player) {
        Inventory gui = Bukkit.createInventory(player, 27, Component.text(INVENTORY_NAME));
        for (int slot : ALL_POSITIONS) {
            gui.setItem(slot, getNodeItem(Math.random() < 0.3));
        }
        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        event.setCancelled(true);
        if (!event.getClickedInventory().equals(event.getView().getTopInventory()))  return;
        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getSlot();
        List<Integer> neighbors = SURROUNDINGS.get(clickedSlot);
        if (neighbors == null)  return;
        for (int slot : neighbors) {
            ItemStack item = event.getClickedInventory().getItem(slot);
            if (item != null && item.getType() == GOOD_MAT) {
                event.getClickedInventory().setItem(slot, getNodeItem(false));
            } else {
                event.getClickedInventory().setItem(slot, getNodeItem(true));
            }
        }
        boolean allGreen = true;
        for (int slot : ALL_POSITIONS) {
            ItemStack stack = event.getClickedInventory().getItem(slot);
            if (stack == null || stack.getType() != GOOD_MAT) {
                allGreen = false;
                break;
            }
        }
        if (allGreen) {
            this.playSuccessSound(player);
            this.taskSuccessful(player);
            player.closeInventory();
        }
    }

    private static ItemStack getNodeItem(boolean isGood) {
        Material mat = isGood ? GOOD_MAT : Material.LIGHT_GRAY_CONCRETE;
        return ItemMaker.createStack(mat, 1, "§eClick", "§3Turn all of them green!");
    }

}
