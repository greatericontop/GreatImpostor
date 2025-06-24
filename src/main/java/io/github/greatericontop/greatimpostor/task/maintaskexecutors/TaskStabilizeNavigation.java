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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class TaskStabilizeNavigation extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Stabilize Navigation";
    private static final int MIDDLE = 22;
    private static final int[] dInv = {-1, 1, -9, 9};

    public TaskStabilizeNavigation(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.STABILIZE_NAVIGATION;
    }

    @Override
    public void startTask(Player player) {
        Random random = new Random();
        Inventory gui = Bukkit.createInventory(player, 45, Component.text(INVENTORY_NAME));

        int startingPos;
        do {
            startingPos = random.nextInt(45);
        } while (startingPos == MIDDLE || startingPos == MIDDLE+1 || startingPos == MIDDLE-1 || startingPos == MIDDLE+9 || startingPos == MIDDLE-9);

        gui.setItem(startingPos, currentBlockItemStack());
        for (int d : dInv) {
            int slot = startingPos + d;
            if (slot >= 0 && slot < 45 && ImpostorUtil.checkOrthoInvSlots(startingPos, slot)) {
                gui.setItem(slot, moveItemStack());
            }
        }
        gui.setItem(MIDDLE, targetBlockItemStack());

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        event.setCancelled(true);
        if (!event.getClickedInventory().equals(event.getView().getTopInventory()))  return;
        Player player = (Player) event.getWhoClicked();

        int clickedOn = event.getSlot();
        int currentLocation = event.getInventory().first(Material.WHITE_WOOL);

        if (!ImpostorUtil.checkOrthoInvSlots(currentLocation, clickedOn)) {
            this.playFailSound(player);
            return;
        }

        if (clickedOn == MIDDLE) {
            this.playSuccessSound(player);
            this.taskSuccessful(player);
            player.closeInventory();
            return;
        }

        // redraw the entire inventory, drawing the target last (to avoid overwriting)
        Inventory gui = event.getInventory();
        gui.clear();
        gui.setItem(clickedOn, currentBlockItemStack());
        for (int d : dInv) {
            int slot = clickedOn + d;
            if (slot >= 0 && slot < 45 && ImpostorUtil.checkOrthoInvSlots(clickedOn, slot)) {
                gui.setItem(slot, moveItemStack());
            }
        }
        gui.setItem(MIDDLE, targetBlockItemStack());
    }

    private ItemStack targetBlockItemStack() {
        ItemStack stack = new ItemStack(Material.TARGET, 1);
        ItemMeta im = stack.getItemMeta();
        im.addEnchant(Enchantment.LUCK, 1, true);
        im.displayName(Component.text("§eGet the target here."));
        stack.setItemMeta(im);
        return stack;
    }

    private ItemStack currentBlockItemStack() {
        ItemStack stack = new ItemStack(Material.WHITE_WOOL, 1);
        ItemMeta im = stack.getItemMeta();
        im.addEnchant(Enchantment.LUCK, 1, true);
        im.displayName(Component.text("§eThe ship is currently set here."));
        stack.setItemMeta(im);
        return stack;
    }

    private ItemStack moveItemStack() {
        ItemStack stack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta im = stack.getItemMeta();
        im.displayName(Component.text("§7Click"));
        stack.setItemMeta(im);
        return stack;
    }

}
