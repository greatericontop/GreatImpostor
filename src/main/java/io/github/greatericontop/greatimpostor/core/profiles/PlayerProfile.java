package io.github.greatericontop.greatimpostor.core.profiles;

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
import io.github.greatericontop.greatimpostor.task.Subtask;
import io.github.greatericontop.greatimpostor.task.TaskType;
import io.github.greatericontop.greatimpostor.task.TaskUtil;
import io.github.greatericontop.greatimpostor.utils.ItemMaker;
import io.github.greatericontop.greatimpostor.utils.PlayerColor;
import io.github.greatericontop.greatimpostor.utils.Shuffler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class PlayerProfile {
    public static final int TASKS_PER = 4;

    protected final Random random;
    public final List<Subtask> tasks = new ArrayList<>();
    protected int[] subtasksCompletedPerTask = null;
    protected final Set<Subtask> tasksAlreadyCompleted = new HashSet<>();
    protected boolean alive;
    private final PlayerColor color;
    private int meetingsCalled;
    public boolean isInCameras;
    public int currentCameraNumber = -1;

    protected GreatImpostorMain plugin;
    protected final Player player;

    public Player getPlayer() {
        return player;
    }
    public boolean isAlive() {
        return alive;
    }
    public PlayerColor getColor() {
        return color;
    }
    public int getMeetingsCalled() {
        return meetingsCalled;
    }
    public void incrementMeetingsCalled() {
        meetingsCalled++;
    }

    public PlayerProfile(GreatImpostorMain plugin, Player player, PlayerColor color) {
        this.plugin = plugin;
        this.random = new Random();
        this.player = player;
        this.alive = true;
        this.color = color;
        this.meetingsCalled = 0;
        this.isInCameras = false;
    }

    //

    /*
     * Render method for use with the alive/dead players display in meetings.
     */
    public String renderNameDisplay(String nameColor) {
        return String.format("%s%s§3(%s§3)", nameColor, player.getName(), color.getDisplayName());
    }

    /*
     * Get the number of tasks completed and the number that need to be completed
     */
    public static int[] getTaskStatus(Collection<PlayerProfile> profiles) {
        int completed = 0;
        int total = 0;
        for (PlayerProfile p : profiles) {
            if (p.isImpostor())  continue;
            for (int i = 0; i < p.tasks.size(); i++) {
                if (p.isFullyCompleted(i)) {
                    completed++;
                }
                total++;
            }
        }
        return new int[]{completed, total};
    }

    //

    public void setInitialTasks() {
        tasksAlreadyCompleted.clear();
        TaskType[] initialTasks = TaskUtil.INITIAL_TASKS.clone();

        do {
            tasks.clear();
            Shuffler.shuffle(initialTasks, random);
            for (int i = 0; i < TASKS_PER; i++) {
                Subtask[] possible = initialTasks[i].getPossibleNextSubtasks(0);
                tasks.add(possible[random.nextInt(possible.length)]);
            }
        } while (
                (getFrequentTaskCount() == 0 && random.nextDouble() < 0.85) // 85% chance to roll again if no frequent tasks
        );
        subtasksCompletedPerTask = new int[]{0, 0, 0, 0};
    }
    private int getFrequentTaskCount() {
        int count = 0;
        for (Subtask subtask : tasks) {
            if (subtask.getFullTask().isFrequent()) {
                count++;
            }
        }
        return count;
    }

    public boolean isFullyCompleted(TaskType taskType) {
        // find the index
        int taskIndex = -1;
        for (int i = 0; i < tasks.size(); i++) { // not O(1) but whatever
            if (tasks.get(i).getFullTask() == taskType) {
                taskIndex = i;
                break;
            }
        }
        if (taskIndex == -1) {
            return false; // not found
        }
        return isFullyCompleted(taskIndex);
    }
    public boolean isFullyCompleted(int i) {
        return subtasksCompletedPerTask[i] >= tasks.get(i).getFullTask().getRequiredSubtaskCount();
    }

    public void processSubtaskCompleted(TaskType taskType) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getFullTask() == taskType) {
                processSubtaskCompleted(i, true);
                return;
            }
        }
        throw new IllegalArgumentException("The given subtask couldn't be found in the list for this profile");
    }
    public void processSubtaskCompleted(int i, boolean shouldUpdateInventory) {
        subtasksCompletedPerTask[i]++;
        tasksAlreadyCompleted.add(tasks.get(i));
        TaskType fullTask = tasks.get(i).getFullTask();
        if (subtasksCompletedPerTask[i] < fullTask.getRequiredSubtaskCount()) {
            // (If you already have completed enough)
            //     This will simply show a green piece of glass with the last task shown as completed.
            Subtask[] possibleNextSubtasks = fullTask.getPossibleNextSubtasks(subtasksCompletedPerTask[i]);
            Subtask nextSubtask;
            do {
                nextSubtask = possibleNextSubtasks[random.nextInt(possibleNextSubtasks.length)];
            } while (fullTask.doAlreadyCompletedCheck() && tasksAlreadyCompleted.contains(nextSubtask));
            tasks.set(i, nextSubtask);
        }
        if (shouldUpdateInventory) {
            setInventory();
        }
    }

    /*
     * Generic stuff that happens in death common to all players.
     */
    protected void dieGeneric() {
        alive = false;
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            p.hidePlayer(plugin, player);
        }
        player.showTitle(Title.title(
                Component.text("§cYou Died!"),
                Component.text("§cYou Died!"),
                Title.Times.times(Duration.ofMillis(1000L), Duration.ofMillis(8000L), Duration.ofMillis(1000L))
        ));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 4)); // this should be obvious (if lights are on)
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4)); // this should shrink and re-enlarge FOV
    }

    /*
     * Inventory setting common to all players. Clears inventory and puts colored armor on.
     */
    protected Inventory setInventoryCommon() {
        PlayerInventory inv = this.getPlayer().getInventory();
        inv.clear();
        inv.setItem(EquipmentSlot.HEAD, ItemMaker.createLeatherArmor(Material.LEATHER_HELMET, color.getColorCode(), color.getDisplayName()));
        inv.setItem(EquipmentSlot.CHEST, ItemMaker.createLeatherArmor(Material.LEATHER_CHESTPLATE, color.getColorCode(), color.getDisplayName()));
        inv.setItem(EquipmentSlot.LEGS, ItemMaker.createLeatherArmor(Material.LEATHER_LEGGINGS, color.getColorCode(), color.getDisplayName()));
        inv.setItem(EquipmentSlot.FEET, ItemMaker.createLeatherArmor(Material.LEATHER_BOOTS, color.getColorCode(), color.getDisplayName()));
        return inv;
    }

    public abstract void die();

    public abstract boolean isImpostor();

    public abstract void setActionBar();

    public abstract void setInventory();

}
