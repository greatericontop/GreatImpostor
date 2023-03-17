package io.github.greatericontop.greatimpostor.task.taskexecutors;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.task.BaseTask;
import io.github.greatericontop.greatimpostor.task.TaskType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TaskFetchFuel extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Fetch Fuel";
    // (9)  10  11  12 (13)
    // (18) 19  20  21 (22)
    // (27) 28  29  30 (31)
    // (36) 37  38  39 (40)
    // (45)(46)(47)(48)(49)
    private static final int[] CANISTER_SLOTS = {9, 18, 27, 36, 45, 46, 47, 48, 49, 40, 31, 22, 13};
    private static final int[] CANISTER_FILL_UP = {37, 38, 28, 39, 29, 19, 30, 20, 10, 21, 11, 12};
    private static final int BUTTON_SLOT = 24;

    public TaskFetchFuel(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.FUEL_ENGINES;
    }

    @Override
    public void startTask(Player player) {
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        for (int i : CANISTER_SLOTS) {
            gui.setItem(i, new ItemStack(Material.STONE, 1));
        }
        ItemStack stack = new ItemStack(Material.CHARCOAL, 1);
        ItemMeta im = stack.getItemMeta();
        im.addEnchant(Enchantment.LUCK, 1, true);
        im.displayName(Component.text("§eClick to fill up the fuel canister!"));
        stack.setItemMeta(im);
        gui.setItem(BUTTON_SLOT, stack);

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME)) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() != BUTTON_SLOT)  return;

        // find first unfilled slot
        boolean alreadyFilled = true; // java is stupid, for-else does not exist
        for (int slot : CANISTER_FILL_UP) {
            if (event.getView().getItem(slot) == null) {
                event.getView().setItem(slot, new ItemStack(Material.BROWN_STAINED_GLASS_PANE, 1));
                player.playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                alreadyFilled = false;
                break;
            }
        }
        if (alreadyFilled) {
            this.playSuccessSound(player);
            this.taskSuccessful(player);
            player.closeInventory();
        }
    }

}
