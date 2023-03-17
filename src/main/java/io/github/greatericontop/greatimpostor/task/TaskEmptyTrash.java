package io.github.greatericontop.greatimpostor.task;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class TaskEmptyTrash extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Empty Trash";
    private static final Material[] TRASH_MATERIALS = {
            Material.COBBLESTONE, Material.STONE, Material.MOSSY_COBBLESTONE,
            Material.STONE_BRICKS, Material.CRACKED_STONE_BRICKS, Material.DEEPSLATE_BRICKS,
            Material.GRASS_BLOCK, Material.DIRT, Material.COARSE_DIRT, Material.OAK_LEAVES,
            Material.GRASS, Material.TALL_GRASS, Material.FERN, Material.LARGE_FERN,
            Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET, Material.CORNFLOWER, Material.LILY_OF_THE_VALLEY,
            Material.KELP, Material.SEA_PICKLE, Material.SEAGRASS,
    };

    public TaskEmptyTrash(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EMPTY_TRASH;
    }


    @Override
    public void startTask(Player player) {
        Random random = new Random();
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        for (int i = 0; i < 54; i++) {
            ItemStack stack = new ItemStack(TRASH_MATERIALS[random.nextInt(TRASH_MATERIALS.length)], 1);
            ItemMeta im = stack.getItemMeta();
            im.displayName(Component.text("§7Click to empty the trash chute."));
            stack.setItemMeta(im);
            gui.setItem(i, stack);
        }

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME)) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        // item in slot 0 must NOT be empty (otherwise we've already started and we shouldn't go again)
        if (event.getInventory().getItem(0) == null)  return;

        new BukkitRunnable() {
            int loopNum = 0;
            public void run() {
                if (event.getViewers().isEmpty()) {
                    TaskEmptyTrash.this.playFailSound(player);
                    player.sendMessage("§cYou closed the inventory!");
                    this.cancel();
                    return;
                }
                int rowStart = loopNum * 9;
                if (rowStart == 54) {
                    TaskEmptyTrash.this.playSuccessSound(player);
                    TaskEmptyTrash.this.taskSuccessful(player);
                    player.closeInventory();
                    this.cancel();
                    return;
                }
                // slide each one down
                for (int row = 45; row > rowStart; row -= 9) {
                    for (int i = 0; i < 9; i++) {
                        event.getInventory().setItem(row + i, event.getInventory().getItem(row + i - 9));
                    }
                }
                // clear current
                for (int i = rowStart; i < rowStart + 9; i++) {
                    event.getInventory().setItem(i, null);
                }
                loopNum++;
            }
        }.runTaskTimer(plugin, 0L, 15L);
    }

}
