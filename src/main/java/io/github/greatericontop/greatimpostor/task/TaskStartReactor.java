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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TaskStartReactor extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Start Reactor";

    private final Map<UUID, Integer> currentNumber = new HashMap<>();

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
        Inventory gui = Bukkit.createInventory(player, 36, Component.text(INVENTORY_NAME));

        Integer[] inventorySlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
        Shuffler.shuffle(inventorySlots, random);
        for (int i = 0; i < 14; i++) {
            Material mat = inventorySlots[i] % 2 == 0 ? Material.BLUE_STAINED_GLASS_PANE : Material.LIGHT_BLUE_STAINED_GLASS_PANE;
            ItemStack stack = new ItemStack(mat, i+1);
            ItemMeta im = stack.getItemMeta();
            im.displayName(Component.text(String.format("§e%d", i+1)));
            stack.setItemMeta(im);
            gui.setItem(inventorySlots[i], stack);
        }
        currentNumber.put(player.getUniqueId(), 1);

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME)) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        ItemStack clickedOn = event.getCurrentItem();
        if (clickedOn == null)  return;
        int numberClickedOn = clickedOn.getAmount();

        if (numberClickedOn != currentNumber.get(player.getUniqueId())) {
            this.playFailSound(player);
            player.sendMessage("§cYou clicked the wrong button!");
            player.closeInventory();
            return;
        }
        this.playSuccessSound(player);
        if (numberClickedOn == 14) {
            this.taskSuccessful(player);
            player.closeInventory();
        } else {
            currentNumber.put(player.getUniqueId(), numberClickedOn + 1);
            clickedOn.setType(Material.LIME_STAINED_GLASS_PANE);
        }
    }

}
