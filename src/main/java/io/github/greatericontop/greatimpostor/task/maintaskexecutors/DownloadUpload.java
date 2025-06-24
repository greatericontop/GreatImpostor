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
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class DownloadUpload extends BaseTask {

    private static final int[] SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 35, 44, 43, 42, 41, 40, 39, 38, 37, 36, 27, 18, 9};
    private static final int MIDDLE = 22;
    private static final Material PROGRESS_YES = Material.GREEN_STAINED_GLASS_PANE;
    private static final Material PROGRESS_NO = Material.RED_STAINED_GLASS_PANE;

    public DownloadUpload(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.DOWNLOAD_UPLOAD_DATA;
    }

    protected void startTask(Player player, String inventoryName, String startingMessage) {
        Inventory gui = Bukkit.createInventory(player, 45, Component.text(inventoryName));

        for (int slot : SLOTS) {
            gui.setItem(slot, new ItemStack(PROGRESS_NO, 1));
        }

        ItemStack stack = new ItemStack(Material.MUSIC_DISC_PIGSTEP, 1);
        ItemMeta im = stack.getItemMeta();
        im.addEnchant(Enchantment.LUCK, 1, true);
        im.displayName(Component.text(startingMessage));
        stack.setItemMeta(im);
        gui.setItem(MIDDLE, stack);

        player.openInventory(gui);
    }

    protected void onInventoryClick(InventoryClickEvent event, GreatImpostorMain plugin, String inventoryName) {
        if (!event.getView().getTitle().equals(inventoryName))  return;
        event.setCancelled(true);
        if (!event.getClickedInventory().equals(event.getView().getTopInventory()))  return;
        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() != 22)  return;
        // check for the progress bar
        if (event.getInventory().getItem(0).getType() == PROGRESS_YES)  return;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        new BukkitRunnable() {
            int loopNum = 0;
            public void run() {
                if (event.getViewers().isEmpty()) {
                    DownloadUpload.this.playFailSound(player);
                    player.sendMessage("§cYou closed the inventory!");
                    this.cancel();
                    return;
                }
                if (loopNum == SLOTS.length) { // one higher than the last index
                    DownloadUpload.this.playSuccessSound(player);
                    DownloadUpload.this.taskSuccessful(player);
                    player.closeInventory();
                    this.cancel();
                    return;
                }
                event.getInventory().setItem(SLOTS[loopNum], new ItemStack(PROGRESS_YES, 1));
                loopNum++;
            }
        }.runTaskTimer(plugin, 0L, 9L); // 24 intervals * 9 ticks = 10.8 seconds
    }

}
