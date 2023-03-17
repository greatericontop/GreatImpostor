package io.github.greatericontop.greatimpostor.task;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
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

public class TaskWiring extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Fix Wiring";

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

        Material[] materials = {
                Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL,
                Material.LIME_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL,
        };
        Shuffler.shuffle(materials, random);
        Material[] targetMaterials = {
                Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL,
                Material.LIME_WOOL, Material.CYAN_WOOL, Material.PURPLE_WOOL,
        };
        Shuffler.shuffle(targetMaterials, random);

        for (int i = 0; i < 6; i++) {
            ItemStack stack = new ItemStack(targetMaterials[i], 1);
            ItemMeta im = stack.getItemMeta();
            im.displayName(Component.text("§eIn order, click the colored blocks on the right."));
            stack.setItemMeta(im);
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
        Player player = (Player) event.getWhoClicked();

        int currentSourceSlot = 0;
        for (int i = 0; i < 45; i += 9) {
            if (event.getClickedInventory().getItem(i).getType() == Material.GRAY_STAINED_GLASS) {
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
            event.getClickedInventory().setItem(currentSourceSlot, new ItemStack(Material.GRAY_STAINED_GLASS, 1));
            if (currentSourceSlot == 45) {
                this.taskSuccessful(player);
                player.closeInventory();
            }
        } else {
            this.playFailSound(player);
            player.sendMessage("§cYou failed!");
            player.closeInventory();
        }
    }

}
