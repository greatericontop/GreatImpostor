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
import io.github.greatericontop.greatimpostor.utils.ImpostorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class TaskAdjustSteering extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Adjust Steering";
    private static final Material ROCK = Material.BEDROCK;
    private static final Material PREVIOUS_PATH = Material.WHITE_STAINED_GLASS_PANE;
    private static final Material CURRENT = Material.WHITE_WOOL;

    public TaskAdjustSteering(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.ADJUST_STEERING;
    }

    @Override
    public void startTask(Player player) {
        Random random = new Random();
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        for (int i = 0; i < 54; i++) {
            ItemStack stack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta im = stack.getItemMeta();
            im.displayName(Component.text("§eStarting from the left, make a path to the right side."));
            stack.setItemMeta(im);
            gui.setItem(i, stack);
        }
        for (int i = 0; i < 20; i++) {
            ItemStack stack = new ItemStack(ROCK, 1);
            ItemMeta im = stack.getItemMeta();
            im.displayName(Component.text("§cDon't touch this."));
            stack.setItemMeta(im);
            gui.setItem(random.nextInt(54), stack);
        }

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        event.setCancelled(true);
        if (!event.getClickedInventory().equals(event.getView().getTopInventory()))  return;
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null)  return;
        if (clickedItem.getType() == PREVIOUS_PATH) {
            this.playFailSound(player);
            // only play sound, don't fail the task
            return;
        }
        if (clickedItem.getType() == ROCK) {
            this.playFailSound(player);
            player.closeInventory();
            player.sendMessage("§cYou crashed into the rock!");
            return;
        }

        int clickLocation = event.getSlot();
        Integer lastKnownLocation = findLastKnownLocation(inv);
        if (lastKnownLocation == null) {
            if (clickLocation % 9 != 0) {
                this.playFailSound(player);
                return;
            }
        } else {
            if (!ImpostorUtil.checkOrthoInvSlots(clickLocation, lastKnownLocation)) {
                this.playFailSound(player);
                return;
            }
        }

        if (lastKnownLocation != null) {
            inv.setItem(lastKnownLocation, new ItemStack(PREVIOUS_PATH, 1));
        }
        ItemStack currentItemStack = new ItemStack(CURRENT, 1);
        ItemMeta im = currentItemStack.getItemMeta();
        im.displayName(Component.text("§eThis is your current position."));
        currentItemStack.setItemMeta(im);
        inv.setItem(clickLocation, currentItemStack);

        if (clickLocation % 9 == 8) {
            this.playSuccessSound(player);
            this.taskSuccessful(player);
            player.closeInventory();
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
        }
    }

    private Integer findLastKnownLocation(Inventory inv) {
        for (int i = 0; i < 54; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack == null) continue;
            if (stack.getType() == CURRENT) {
                return i;
            }
        }
        return null;
    }

}
