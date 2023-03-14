package io.github.greatericontop.greatimpostor.task;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskDownloadData implements BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Download Data";
    private static final int[] SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 35, 44, 43, 42, 41, 40, 39, 38, 37, 36, 27, 18, 9};
    private static final int MIDDLE = 22;
    private static final Material PROGRESS_YES = Material.GREEN_STAINED_GLASS_PANE;
    private static final Material PROGRESS_NO = Material.RED_STAINED_GLASS_PANE;

    private final GreatImpostorMain plugin;
    public TaskDownloadData(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canExecute(Player player) {
        return true;
    }

    @Override
    public void startTask(Player player) {
        Inventory gui = Bukkit.createInventory(player, 45, Component.text(INVENTORY_NAME));

        for (int slot : SLOTS) {
            gui.setItem(slot, new ItemStack(PROGRESS_NO, 1));
        }

        ItemStack stack = new ItemStack(Material.MUSIC_DISC_PIGSTEP, 1);
        ItemMeta im = stack.getItemMeta();
        im.displayName(Component.text("§eClick to start downloading the data."));
        stack.setItemMeta(im);
        gui.setItem(MIDDLE, stack);

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME)) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() != 22)  return;
        // check for the progress bar
        if (event.getInventory().getItem(0).getType() == PROGRESS_YES)  return;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
        new BukkitRunnable() {
            int loopNum = 0;
            public void run() {
                if (event.getViewers().isEmpty()) {
                    TaskDownloadData.this.playFailSound(player);
                    player.sendMessage("§cYou closed the inventory!");
                    this.cancel();
                    return;
                }
                if (loopNum == SLOTS.length) { // one higher than the last index
                    TaskDownloadData.this.playSuccessSound(player);
                    TaskDownloadData.this.taskSuccessful(player);
                    player.closeInventory();
                    this.cancel();
                    return;
                }
                event.getInventory().setItem(SLOTS[loopNum], new ItemStack(PROGRESS_YES, 1));
                loopNum++;
            }
        }.runTaskTimer(plugin, 0L, 12L); // 24 intervals * 12 ticks = 14.4 seconds
    }

}
