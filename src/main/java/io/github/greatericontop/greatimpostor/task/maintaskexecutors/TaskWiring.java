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
import io.github.greatericontop.greatimpostor.utils.Shuffler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Random;

public class TaskWiring extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Fix Wiring";

    private static final Map<Material, String> COLOR_NAMES = Map.of(
            Material.RED_WOOL, "§cRED",
            Material.ORANGE_WOOL, "§6ORANGE",
            Material.YELLOW_WOOL, "§eYELLOW",
            Material.LIME_WOOL, "§aGREEN",
            Material.CYAN_WOOL, "§aCYAN",
            Material.PURPLE_WOOL, "§5PURPLE"
    );

    public TaskWiring(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.WIRING;
    }

    @Override
    public void startTask(Player player) {
        Random random = new Random();
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        Material[] materials = { // the ones you actually click, on the right
                Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL,
                Material.LIME_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL,
        };
        Shuffler.shuffle(materials, random);
        Material[] targetMaterials = { // the ones showing you the order, on the left
                Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL,
                Material.LIME_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL,
        };
        Shuffler.shuffle(targetMaterials, random);

        for (int i = 0; i < 6; i++) {
            ItemStack stack = ItemMaker.createStack(targetMaterials[i], 1,
                    "§eIn order, click the colored blocks on the right.",
                    String.format("§7Click %s §7on the right side", COLOR_NAMES.get(targetMaterials[0])));
            if (i == 0) {
                // show glint on current one
                stack.addUnsafeEnchantment(Enchantment.LUCK, 1);
            }
            gui.setItem(i*9, stack);
            gui.setItem(i*9+8, new ItemStack(materials[i], 1));
            for (int j = 1; j < 8; j++) {
                gui.setItem(i*9+j, new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
            }
        }

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        event.setCancelled(true);
        if (!event.getClickedInventory().equals(event.getView().getTopInventory()))  return;
        Player player = (Player) event.getWhoClicked();

        int currentSourceSlot = 0;
        for (int i = 0; i < 45; i += 9) {
            if (event.getClickedInventory().getItem(i).getType() == Material.LIME_STAINED_GLASS) {
                currentSourceSlot = i + 9;
            } else {
                break;
            }
        }

        int slot = event.getSlot();
        if (event.getClickedInventory().getItem(slot).getType() == event.getClickedInventory().getItem(currentSourceSlot).getType()
                && slot != currentSourceSlot
        ) {
            this.playSuccessSound(player);
            event.getClickedInventory().setItem(currentSourceSlot, ItemMaker.createStack(Material.LIME_STAINED_GLASS, 1, "§aDone!"));
            if (currentSourceSlot == 45) {
                this.taskSuccessful(player);
                player.closeInventory();
            } else {
                Inventory inv = event.getClickedInventory();
                Material nextMat = inv.getItem(currentSourceSlot+9).getType();
                for (int i = currentSourceSlot + 9; i <= 45; i += 9) {
                    ItemStack stack = ItemMaker.createStack(inv.getItem(i).getType(), 1,
                        "§eIn order, click the colored blocks on the right.",
                        String.format("§7Click %s §7on the right side", COLOR_NAMES.get(nextMat)));
                    if (i == currentSourceSlot + 9) {
                        stack.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    }
                    inv.setItem(i, stack);
                }
            }
        } else {
            this.playFailSound(player);
            player.sendMessage("§cYou failed!");
            player.closeInventory();
        }
    }

}
