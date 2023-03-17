package io.github.greatericontop.greatimpostor.task;

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
