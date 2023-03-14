package io.github.greatericontop.greatimpostor.task;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TaskCleanOxygenFilter implements BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Clean Oxygen Filter";

    @Override
    public boolean canExecute(Player player) {
        return true;
    }

    @Override
    public void startTask(Player player) {
        Random random = new Random();
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        for (int i = 0; i < 10; i++) {
            int slot = random.nextInt(54);
            gui.setItem(slot, new ItemStack(Material.MOSS_BLOCK, 1));
        }

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME)) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        event.getInventory().setItem(event.getSlot(), null);

        if (isCleared(event.getInventory())) {
            this.playSuccessSound(player);
            this.taskSuccessful(player);
            player.closeInventory();
        }

    }

    private boolean isCleared(Inventory inv) {
        for (ItemStack stack : inv.getContents()) {
            if (stack != null && stack.getType() == Material.MOSS_BLOCK) {
                return false;
            }
        }
        return true;
    }

}
