package io.github.greatericontop.greatimpostor.task;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
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
import org.bukkit.scheduler.BukkitRunnable;

public class TaskFuelEngines extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Fuel Engines";
    // (11)(12)(13)(14)(15)(16)
    //  20  21  22  23  24 (25)
    //  29  30  31  32  33 (34)
    //  38  39  40  41  42 (43)
    // (47)(48)(49)(50)(51)(52)
    private static final int[] CANISTER_SLOTS = {11, 12, 13, 14, 15, 16, 25, 34, 43, 52, 51, 50, 49, 48, 47};
    private static final int[] CANISTER_EMPTY_ORDER = {20, 21, 29, 22, 23, 30, 24, 31, 38, 32, 39, 33, 40, 41, 42};
    private static final int BUTTON_SLOT = 0;

    public TaskFuelEngines(GreatImpostorMain plugin) {
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
        for (int i : CANISTER_EMPTY_ORDER) {
            gui.setItem(i, new ItemStack(Material.BROWN_STAINED_GLASS_PANE, 1));
        }
        ItemStack stack = new ItemStack(Material.CHARCOAL, 1);
        ItemMeta im = stack.getItemMeta();
        im.addEnchant(Enchantment.LUCK, 1, true);
        im.displayName(Component.text("§eClick to deposit the fuel can into the engine!"));
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
        if (event.getInventory().getItem(BUTTON_SLOT) == null)  return;
        event.getInventory().setItem(BUTTON_SLOT, null);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

        new BukkitRunnable() {
            int loopNum = 0;
            public void run() {
                if (event.getViewers().isEmpty()) {
                    TaskFuelEngines.this.playFailSound(player);
                    player.sendMessage("§cYou closed the inventory!");
                    this.cancel();
                    return;
                }
                if (loopNum == CANISTER_EMPTY_ORDER.length) { // one higher than the last index
                    TaskFuelEngines.this.playSuccessSound(player);
                    TaskFuelEngines.this.taskSuccessful(player);
                    player.closeInventory();
                    this.cancel();
                    return;
                }
                event.getInventory().setItem(CANISTER_EMPTY_ORDER[loopNum], null);
                loopNum++;
            }
        }.runTaskTimer(plugin, 6L, 6L);
    }

}
