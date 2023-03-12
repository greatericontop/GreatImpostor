package io.github.greatericontop.greatimpostor.task;

import io.github.greatericontop.greatimpostor.utils.Shuffler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class TaskWiring implements BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Fix Wiring";

    @Override
    public boolean canExecute(Player player) {
        return true;
    }

    @Override
    public void startTask(Player player) {
        Random random = new Random();
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        Material[] materials = {
                Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL,
                Material.LIME_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL,
        };
        Shuffler.shuffle(materials, random);
        Material targetMaterial = materials[random.nextInt(materials.length)];

        for (int i = 0; i < 6; i++) {
            ItemStack stack = new ItemStack(targetMaterial, 1);
            ItemMeta im = stack.getItemMeta();
            im.displayName(Component.text("§7Click the corresponding block on the right!"));
            stack.setItemMeta(im);
            gui.setItem(i*9, stack);
            gui.setItem(i*9+8, new ItemStack(materials[i], 1));
            for (int j = 1; j < 8; j++) {
                gui.setItem(i*9+j, new ItemStack(Material.GRAY_STAINED_GLASS, 1));
            }
        }

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        Player player = (Player) event.getWhoClicked();

        int slot = event.getSlot();
        if (event.getClickedInventory().getItem(slot).getType() == event.getClickedInventory().getItem(0).getType()) {
            playSuccessSound(player);
            this.taskSuccessful(player);
        } else {
            playFailSound(player);
            player.sendMessage("§cYou failed!");
        }
        event.setCancelled(true);
        player.closeInventory();
    }

}
