package io.github.greatericontop.greatimpostor.task.taskexecutors;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.task.BaseTask;
import io.github.greatericontop.greatimpostor.task.TaskType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TaskAcceptPower extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Accept Power";

    public TaskAcceptPower(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.REDIRECT_ACCEPT_POWER;
    }

    @Override
    public void startTask(Player player) {
        Inventory gui = Bukkit.createInventory(player, 45, Component.text(INVENTORY_NAME));


        for (int i = 18; i < 22; i++) {
            gui.setItem(i, new ItemStack(Material.YELLOW_STAINED_GLASS_PANE, 1));
        }
        ItemStack stack = new ItemStack(Material.WHITE_STAINED_GLASS, 1);
        ItemMeta im = stack.getItemMeta();
        im.displayName(Component.text("§7Click to accept the incoming power."));
        stack.setItemMeta(im);
        gui.setItem(22, stack);
        for (int i = 23; i < 27; i++) {
            gui.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        }

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME)) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() == 22) {
            this.playSuccessSound(player);
            this.taskSuccessful(player);
            player.closeInventory();
        }

    }

}
