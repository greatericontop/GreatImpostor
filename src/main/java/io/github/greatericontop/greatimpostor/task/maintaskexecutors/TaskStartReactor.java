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
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TaskStartReactor extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Start Reactor";
    private static final float[] PITCHES = {0.707107F, 0.793701F, 0.890899F, 0.943874F, 1.059463F};
    private static final Material[] MATERIALS = {
            Material.RED_WOOL, Material.BLUE_WOOL, Material.LIME_WOOL,
            Material.YELLOW_WOOL, Material.WHITE_WOOL
    };
    // top-middle 7 slots to display the correct code
    private static final int DISPLAY_OFFSET = 1;
    // red: 29+0=29, blue: 29+1=30, green (mid): 29+2=31
    private static final int KEY_OFFSET = 29;
    private final Map<Player, ArrayList<Integer>> playerPasswordMap = new HashMap<>();
    // e.g. playerDigitCount=2 means second digit (index < 2)
    private final Map<Player, Integer> playerDigitCount = new HashMap<>();
    private final Map<Player, Boolean> cooldown = new HashMap<>();

    public TaskStartReactor(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.START_REACTOR;
    }

    @Override
    public void startTask(Player player) {
        Random random = new Random();
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        ArrayList<Integer> password = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            password.add(random.nextInt(MATERIALS.length));
        }
        playerPasswordMap.put(player, password);
        playerDigitCount.put(player, 1);

        for (int i = 0; i < MATERIALS.length; i++) {
            gui.setItem(KEY_OFFSET + i, ItemMaker.createStack(MATERIALS[i], 1, "§eClick on this to enter the sequence."));
        }

        for (int i = 0; i < 7; i++) {
            gui.setItem(DISPLAY_OFFSET + i, blackGlassStack());
        }
        ItemStack sequenceItemStack = ItemMaker.createStack(MATERIALS[password.get(0)], 1, "§3Memorize the sequence and enter it below.");
        gui.setItem(DISPLAY_OFFSET, sequenceItemStack);

        // extra visual padding
        gui.setItem(0, new ItemStack(Material.NETHER_STAR, 1));
        gui.setItem(8, new ItemStack(Material.NETHER_STAR, 1));
        for (int i = 19; i <= 25; i++) {
            gui.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
        }
        gui.setItem(28, new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
        gui.setItem(34, new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
        for (int i = 37; i <= 43; i++) {
            gui.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
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
        Material mat = event.getCurrentItem().getType();
        if (mat == Material.GRAY_STAINED_GLASS_PANE)  return;

        int indexClicked = event.getSlot() - KEY_OFFSET;
        if (indexClicked < 0 || indexClicked >= 5)  return;
        Inventory inv = event.getInventory();

        // cooldown
        if (cooldown.getOrDefault(player, false)) {
            player.sendMessage("§cClicking too fast!");
            return;
        }
        cooldown.put(player, true);
        new BukkitRunnable() {
            public void run() {
                cooldown.put(player, false);
            }
        }.runTaskLater(plugin, 3L);

        // Check what digit of the password we're on (scan the top row)
        int currentDigitIndex = -1;
        // check for first non-green pane (if we're on the first digit then all of them will be wool, otherwise
        //   the current digit would be the first red one)
        for (int i = 0; i < 7; i++) {
            if (inv.getItem(DISPLAY_OFFSET + i).getType() != Material.GREEN_STAINED_GLASS) {
                currentDigitIndex = i;
                break;
            }
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, PITCHES[indexClicked]);

        // Check correctness
        if (indexClicked != playerPasswordMap.get(player).get(currentDigitIndex)) {
            this.playFailSound(player);
            player.sendMessage("§cIncorrect code!");
            player.closeInventory();
            return;
        }

        // Check for "win" condition
        if (currentDigitIndex == playerDigitCount.get(player) - 1) {
            this.playSuccessSound(player);
            int newDigitCount = playerDigitCount.get(player) + 1;
            if (newDigitCount == 8) { // win, solved the 7 case
                this.taskSuccessful(player);
                player.closeInventory();
                return;
            }
            playerDigitCount.put(player, newDigitCount);
            for (int i = 0; i < newDigitCount; i++) {
                ItemStack sequenceItemStack = ItemMaker.createStack(MATERIALS[playerPasswordMap.get(player).get(i)], 1, "§3Memorize the sequence and enter it below.");
                inv.setItem(DISPLAY_OFFSET + i, sequenceItemStack);
            }
            for (int i = newDigitCount; i < 7; i++) {
                inv.setItem(DISPLAY_OFFSET + i, blackGlassStack());
            }
        } else {
            // [NOT victory] Update the display of top digits
            if (currentDigitIndex == 0) {
                // after pressing first digit, set first digit to success & clear display
                inv.setItem(DISPLAY_OFFSET, greenGlassStack());
                for (int i = 1; i < 7; i++) {
                    inv.setItem(DISPLAY_OFFSET + i, redGlassStack());
                }
            } else {
                inv.setItem(DISPLAY_OFFSET + currentDigitIndex, greenGlassStack());
            }
        }

    }

    private ItemStack blackGlassStack() {
        ItemStack stack = new ItemStack(Material.BLACK_STAINED_GLASS, 1);
        ItemMeta im = stack.getItemMeta();
        im.displayName(Component.text("§7This part of the code will be used later."));
        stack.setItemMeta(im);
        return stack;
    }

    private ItemStack redGlassStack() {
        ItemStack stack = new ItemStack(Material.RED_STAINED_GLASS, 1);
        ItemMeta im = stack.getItemMeta();
        im.displayName(Component.text("§cYou can't see this right now!"));
        stack.setItemMeta(im);
        return stack;
    }

    private ItemStack greenGlassStack() {
        ItemStack stack = new ItemStack(Material.GREEN_STAINED_GLASS, 1);
        ItemMeta im = stack.getItemMeta();
        im.displayName(Component.text("§aYou already solved this one."));
        stack.setItemMeta(im);
        return stack;
    }

}
