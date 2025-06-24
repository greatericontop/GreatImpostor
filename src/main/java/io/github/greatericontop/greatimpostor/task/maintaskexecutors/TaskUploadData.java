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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TaskUploadData extends DownloadUpload {
    public static final String INVENTORY_NAME = "§aAmong Us - Upload Data";

    public TaskUploadData(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public void startTask(Player player) {
        super.startTask(player, INVENTORY_NAME, "§eClick to start uploading the data.");
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event, plugin, INVENTORY_NAME);
    }

}
