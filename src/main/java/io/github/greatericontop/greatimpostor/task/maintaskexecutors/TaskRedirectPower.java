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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TaskRedirectPower extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Redirect Power";
    private static final Material[] MATERIALS = {
            Material.IRON_INGOT, Material.GOLD_INGOT, Material.EMERALD,
            Material.REDSTONE, Material.LAPIS_LAZULI, Material.DIAMOND,
    };
    private static final String[] MATERIAL_NAMES = {
            "§f§lIRON", "§6GOLD", "§2EMERALD",
            "§4REDSTONE", "§9LAPIS", "§bDIAMOND",
    };
    private final Map<UUID, Integer> playerItemNumbers = new HashMap<>();

    public TaskRedirectPower(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.REDIRECT_ACCEPT_POWER;
    }

    @Override
    public void startTask(Player player) {
        Random random = new Random();
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        int targetItemNumber = random.nextInt(MATERIALS.length);
        playerItemNumbers.put(player.getUniqueId(), targetItemNumber);

        for (int i = 0; i < 54; i++) {
            ItemStack stack = new ItemStack(MATERIALS[random.nextInt(MATERIALS.length)], 1);
            ItemMeta im = stack.getItemMeta();
            im.displayName(Component.text(String.format("§eClick on all of the %s§e.", MATERIAL_NAMES[targetItemNumber])));
            stack.setItemMeta(im);
            gui.setItem(i, stack);
        }

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        event.setCancelled(true);
        if (!event.getClickedInventory().equals(event.getView().getTopInventory()))  return;
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null)  return;
        Material clickedMaterial = event.getCurrentItem().getType();
        Material targetMaterial = MATERIALS[playerItemNumbers.get(player.getUniqueId())];
        if (clickedMaterial == targetMaterial) {
            this.playSuccessSound(player);
            event.getInventory().setItem(event.getSlot(), null);
            messWithInventory(event.getInventory(), new Random());
            if (containsNone(event.getInventory(), targetMaterial)) {
                this.taskSuccessful(player);
                player.closeInventory();
            }
        } else {
            this.playFailSound(player);
            player.sendMessage("§cYou clicked on the wrong item! You failed!");
            player.closeInventory();
        }

    }

    private boolean containsNone (Inventory inventory, Material material) {
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.getType() == material) {
                return false;
            }
        }
        return true;
    }

    private void messWithInventory(Inventory inventory, Random random) {
        for (int i = 0; i < 8; i++) {
            int x = random.nextInt(54);
            int y = random.nextInt(54);
            ItemStack temp = inventory.getItem(x);
            inventory.setItem(x, inventory.getItem(y));
            inventory.setItem(y, temp);
        }
    }

}
