package io.github.greatericontop.greatimpostor.task.maintaskexecutors;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.task.BaseTask;
import io.github.greatericontop.greatimpostor.task.TaskType;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TaskSwipeCard extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Swipe Card";
    private static final int OFFSET = 10;
    private static final double CONSISTENCY_FACTOR = 1.6;

    private final Map<UUID, List<Integer>> cardSwipeData = new HashMap<>();

    public TaskSwipeCard(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SWIPE_CARD;
    }

    @Override
    public void startTask(Player player) {
        Inventory gui = Bukkit.createInventory(player, 18, Component.text(INVENTORY_NAME));

        ItemStack info = new ItemStack(Material.WHITE_STAINED_GLASS, 1);
        ItemMeta im = info.getItemMeta();
        im.displayName(Component.text("§aSwipe Card"));
        im.lore(List.of(
                Component.text("§7You need to swipe the card."),
                Component.text("§7Click each of the glass panes from left to right."),
                Component.text("§7Don't go too fast or too slow, and stay consistent.")
        ));
        info.setItemMeta(im);
        gui.setItem(4, info);
        for (int i = OFFSET; i < OFFSET+7; i++) {
            gui.setItem(i, new ItemStack(Material.CYAN_STAINED_GLASS_PANE, 1));
        }

        cardSwipeData.put(player.getUniqueId(), Arrays.asList(-2, -2, -2, -2, -2, -2, -2));

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME)) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        int numberClicked = event.getSlot() - OFFSET;
        if (numberClicked < 0 || numberClicked >= 7)  return;
        List<Integer> data = cardSwipeData.get(player.getUniqueId());
        if (data.get(numberClicked) != -2 || (numberClicked != 0 && data.get(numberClicked-1) == -2)) {
            this.playFailSound(player);
            player.sendMessage("§cYou clicked in the wrong order!");
            return;
        }
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
        data.set(numberClicked, plugin.getClock());
        if (numberClicked == 6) {
            int[] deltas = getDeltas(data);
            // find the total deviation from target
            double accuracy = Math.sqrt(getVariance(deltas, 5.0));
            // consistency: find total deviation for itself
            double mean = (deltas[0] + deltas[1] + deltas[2] + deltas[3] + deltas[4] + deltas[5]) / 6.0;
            double consistency = Math.sqrt(getVariance(deltas, mean));
            double score = accuracy + CONSISTENCY_FACTOR*consistency;
            player.sendMessage(String.format("§7accuracy §f%.2f §7consistency §f%.2f §7(mean %.2f)  §7|  SCORE: §e%.2f", accuracy, consistency, mean, score));
            if (score < 7.00) {
                this.playSuccessSound(player);
                this.taskSuccessful(player);
                player.closeInventory();
            } else {
                this.playFailSound(player);
                player.closeInventory();
                player.sendMessage("§cCouldn't read the card! Try again!");
                if (mean >= 5.7) {
                    player.sendMessage("§cYou were too slow!");
                } else if (mean <= 4.3) {
                    player.sendMessage("§cYou were too fast!");
                } else if (CONSISTENCY_FACTOR*consistency >= 4.5) {
                    player.sendMessage("§cYou were not very consistent.");
                } else {
                    player.sendMessage("§cYou were close. Try again!");
                }
            }
        }

    }

    private int[] getDeltas(List<Integer> data) {
        int[] deltas = new int[6];
        for (int i = 0; i < 6; i++) {
            deltas[i] = data.get(i+1) - data.get(i);
        }
        return deltas;
    }

    private double getVariance(int[] deltas, double targetValue) {
        double variance = 0;
        for (int i = 0; i < 6; i++) {
            variance += (deltas[i]-targetValue) * (deltas[i]-targetValue);
        }
        return variance;
    }

}
