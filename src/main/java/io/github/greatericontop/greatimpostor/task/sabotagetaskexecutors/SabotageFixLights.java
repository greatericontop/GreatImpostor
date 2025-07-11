package io.github.greatericontop.greatimpostor.task.sabotagetaskexecutors;

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
import io.github.greatericontop.greatimpostor.task.sabotage.Sabotage;
import io.github.greatericontop.greatimpostor.task.sabotage.SabotageSubtask;
import io.github.greatericontop.greatimpostor.task.sabotage.BaseSabotageTask;
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

public class SabotageFixLights extends BaseSabotageTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Fix Lights";

    public SabotageFixLights(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public Sabotage getSabotage() {
        return Sabotage.LIGHTS;
    }

    @Override
    public void prepareSabotageTask() {
        // nothing to do
    }

    @Override
    public void startTask(Player player, SabotageSubtask sabotageSubtask) {
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        for (int col = 0; col < 9; col++) {
            gui.setItem(col+9, getItemStackSwitchYes());
            gui.setItem(col+18, getItemStackSwitchNo());
            if (Math.random() < 0.45) { // slightly biased
                gui.setItem(col+36, getItemStackPower());
            }
        }

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        int row = event.getSlot() / 9;
        if (row != 1 && row != 2)  return;
        int col = event.getSlot() % 9;
        // switch
        ItemStack temp = event.getInventory().getItem(col+9);
        event.getInventory().setItem(col+9, event.getInventory().getItem(col+18));
        event.getInventory().setItem(col+18, temp);
        // toggle the "power"
        if (event.getInventory().getItem(col+36) == null) {
            event.getInventory().setItem(col+36, getItemStackPower());
        } else {
            event.getInventory().setItem(col+36, null);
        }
        // check success
        boolean success = true;
        for (int c = 0; c < 9; c++) {
            if (event.getInventory().getItem(c+36) == null) {
                success = false;
                break;
            }
        }
        if (success) {
            this.playSuccessSound(player);
            this.taskSuccessful(player);
            player.closeInventory();
        }
    }

    private ItemStack getItemStackSwitchNo() {
        ItemStack stack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta im = stack.getItemMeta();
        im.displayName(Component.text("§7Click to toggle switch"));
        stack.setItemMeta(im);
        return stack;
    }
    private ItemStack getItemStackSwitchYes() {
        ItemStack stack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta im = stack.getItemMeta();
        im.displayName(Component.text("§7Click to toggle switch"));
        im.addEnchant(Enchantment.LUCK, 1, true);
        stack.setItemMeta(im);
        return stack;
    }
    private ItemStack getItemStackPower() {
        ItemStack stack = new ItemStack(Material.YELLOW_CONCRETE, 1);
        ItemMeta im = stack.getItemMeta();
        im.displayName(Component.text("§ePower"));
        stack.setItemMeta(im);
        return stack;
    }

}
