package io.github.greatericontop.greatimpostor.task;

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

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Subtask {

    // To prevent a cyclic dependency between Subtask and Task this needs to be a string that gets put into a TaskType at runtime

    WIRING_ELECTRICAL("WIRING", "§eElectrical: Fix Wiring"),
    WIRING_STORAGE("WIRING", "§eStorage: Fix Wiring"),
    WIRING_ADMIN("WIRING", "§eAdmin: Fix Wiring"),
    WIRING_NAVIGATION("WIRING", "§eNavigation: Fix Wiring"),
    WIRING_CAFETERIA("WIRING", "§eCafeteria: Fix Wiring"),
    WIRING_SECURITY("WIRING", "§eSecurity: Fix Wiring"),

    REDIRECT_POWER("REDIRECT_ACCEPT_POWER", "§eElectrical: Redirect Power"),

    ACCEPT_POWER_COMMUNICATIONS("REDIRECT_ACCEPT_POWER", "§eCommunications: Accept Redirected Power"),
    ACCEPT_POWER_LOWER_ENGINE("REDIRECT_ACCEPT_POWER", "§eLower Engine: Accept Redirected Power"),
    ACCEPT_POWER_UPPER_ENGINE("REDIRECT_ACCEPT_POWER", "§eUpper Engine: Accept Redirected Power"),
    ACCEPT_POWER_NAVIGATION("REDIRECT_ACCEPT_POWER", "§eNavigation: Accept Redirected Power"),
    ACCEPT_POWER_OXYGEN("REDIRECT_ACCEPT_POWER", "§eOxygen: Accept Redirected Power"),
    ACCEPT_POWER_SECURITY("REDIRECT_ACCEPT_POWER", "§eSecurity: Accept Redirected Power"),
    ACCEPT_POWER_SHIELDS("REDIRECT_ACCEPT_POWER", "§eShields: Accept Redirected Power"),
    ACCEPT_POWER_WEAPONS("REDIRECT_ACCEPT_POWER", "§eWeapons: Accept Redirected Power"),

    DOWNLOAD_DATA_CAFETERIA("DOWNLOAD_UPLOAD_DATA", "§eCafeteria: Download Data"),
    DOWNLOAD_DATA_COMMUNICATIONS("DOWNLOAD_UPLOAD_DATA", "§eCommunications: Download Data"),
    DOWNLOAD_DATA_ELECTRICAL("DOWNLOAD_UPLOAD_DATA", "§eElectrical: Download Data"),
    DOWNLOAD_DATA_NAVIGATION("DOWNLOAD_UPLOAD_DATA", "§eNavigation: Download Data"),
    DOWNLOAD_DATA_WEAPONS("DOWNLOAD_UPLOAD_DATA", "§eWeapons: Download Data"),

    UPLOAD_DATA("DOWNLOAD_UPLOAD_DATA", "§eAdmin: Upload Data"),

    FETCH_FUEL("FUEL_ENGINES", "§eStorage: Fetch Fuel"),

    FUEL_ENGINES_UPPER("FUEL_ENGINES", "§eUpper Engine: Fuel Engine"),
    FUEL_ENGINES_LOWER("FUEL_ENGINES", "§eLower Engine: Fuel Engine"),

    SWIPE_CARD("SWIPE_CARD", "§eAdmin: Swipe Card"),

    ADJUST_STEERING("ADJUST_STEERING", "§eNavigation: Adjust Steering"),

    CLEAN_OXYGEN_FILTER("CLEAN_OXYGEN_FILTER", "§eOxygen: Clean Oxygen Filter"),

    CLEAR_ASTEROIDS("CLEAR_ASTEROIDS", "§eWeapons: Clear Asteroids"),

    EMPTY_TRASH_CAFETERIA("EMPTY_TRASH", "§eCafeteria: Empty Trash"),
    EMPTY_TRASH_STORAGE("EMPTY_TRASH", "§eStorage: Empty Trash"),

    STABILIZE_NAVIGATION("STABILIZE_NAVIGATION", "§eNavigation: Stabilize Navigation"),

    START_REACTOR("START_REACTOR", "§eReactor: Start Reactor"),

    UNLOCK_MANIFOLDS("UNLOCK_MANIFOLDS", "§eReactor: Unlock Manifolds"),

    SUBMIT_SCAN("SUBMIT_SCAN", "§eMedbay: Submit Scan"),

    PRIME_SHIELDS("PRIME_SHIELDS", "§eShields: Prime Shields"),

    ANALYZE_SAMPLE("ANALYZE_SAMPLE", "§eMedbay: Analyze Sample"),

    ;

    private final String fullTask;
    private final String displayName;

    public TaskType getFullTask() {
        return TaskType.valueOf(fullTask);
    }
    public String getDisplayName() {
        return displayName;
    }

    Subtask(String fullTask, String displayName) {
        this.displayName = displayName;
        this.fullTask = fullTask;
    }

    public ItemStack getDisplayItemStack(int numberCompleted, String prefix) {
        int total = getFullTask().getRequiredSubtaskCount();
        Material mat;
        String displayName;
        if (numberCompleted == total) {
            mat = Material.LIME_STAINED_GLASS;
            displayName = "§aAll Done!";
        } else if (numberCompleted == 0) {
            mat = Material.RED_STAINED_GLASS;
            displayName = String.format("%s §b(%d/%d)", getDisplayName(), numberCompleted+1, total);
        } else {
            mat = Material.YELLOW_STAINED_GLASS;
            displayName = String.format("%s §b(%d/%d)", getDisplayName(), numberCompleted+1, total);
        }
        ItemStack taskStack = new ItemStack(mat, 1);
        ItemMeta im = taskStack.getItemMeta();
        im.displayName(Component.text(prefix + displayName));
        taskStack.setItemMeta(im);
        return taskStack;
    }

}
