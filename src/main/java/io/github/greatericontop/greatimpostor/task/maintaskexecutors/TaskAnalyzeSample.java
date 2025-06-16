package io.github.greatericontop.greatimpostor.task.maintaskexecutors;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.task.BaseTask;
import io.github.greatericontop.greatimpostor.task.TaskType;
import io.github.greatericontop.greatimpostor.utils.ItemMaker;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TaskAnalyzeSample extends BaseTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Analyze Sample";

    private static final int START_BUTTON = 13;
    private static final int[] SAMPLES = {46, 48, 50, 52};
    private static final List<Integer> SAMPLES_L = List.of(46, 48, 50, 52);


    private enum Status {
        NOT_STARTED,
        WAITING,
        READY,
    }


    private final Random random = new Random();
    private final Map<UUID, Status> playerStatusMap = new HashMap<>();
    private final Map<UUID, Integer> whichSample = new HashMap<>();

    public TaskAnalyzeSample(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.ANALYZE_SAMPLE;
    }

    @Override
    public void startTask(Player player) {
        if (!playerStatusMap.containsKey(player.getUniqueId())) {
            playerStatusMap.put(player.getUniqueId(), Status.NOT_STARTED);
            whichSample.put(player.getUniqueId(), SAMPLES[random.nextInt(SAMPLES.length)]);
        }
        Inventory inv = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));
        updateInventory(player, inv, -1); // (if opened mid-timer it will show the placeholder for up to 1 second)
        player.openInventory(inv);
    }

    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Status status = playerStatusMap.get(player.getUniqueId());
        switch (status) {
            case NOT_STARTED -> {
                if (event.getSlot() == START_BUTTON) {
                    playerStatusMap.put(player.getUniqueId(), Status.WAITING);
                    new BukkitRunnable() {
                        int secondsLeft = 1 + 20;
                        public void run() {
                            secondsLeft -= 1;
                            if (secondsLeft <= 0) {
                                playerStatusMap.put(player.getUniqueId(), Status.READY);
                                updateInventory(player, event.getInventory(), -1);
                                this.cancel();
                            } else {
                                // This allows the player to exit the menu and update it if they come back
                                if (player.getOpenInventory().getTitle().equals(INVENTORY_NAME)) {
                                    updateInventory(player, player.getOpenInventory().getTopInventory(), secondsLeft);
                                }
                            }
                        }
                    }.runTaskTimer(plugin, 1L, 20L);

                }
            }
            case READY -> {
                if (SAMPLES_L.contains(event.getSlot())) {
                    if (whichSample.get(player.getUniqueId()) == event.getSlot()) {
                        this.playSuccessSound(player);
                        this.taskSuccessful(player);
                        player.closeInventory();
                    } else {
                        this.playFailSound(player);
                        player.sendMessage(Component.text("§cThis sample does not contain the anomaly!"));
                        player.closeInventory();
                        playerStatusMap.remove(player.getUniqueId());
                    }
                }
            }
            default -> {}
        }
    }

    private void updateInventory(Player player, Inventory inv, int secondsLeft) {
        Status status = playerStatusMap.get(player.getUniqueId());
        switch (status) {
            case NOT_STARTED -> {
                inv.setItem(START_BUTTON, ItemMaker.createStack(Material.BREWING_STAND, 1, "§3Start Analysis"));
                for (int slot : SAMPLES) {
                    ItemStack stack = new ItemStack(Material.POTION, 1);
                    PotionMeta im = (PotionMeta) stack.getItemMeta();
                    im.displayName(Component.text(String.format("§6Sample %d", (slot-44)/2)));
                    im.setColor(Color.fromRGB(0xaaaaaa));
                }
            }
            case WAITING -> {
                inv.setItem(START_BUTTON, ItemMaker.createStack(Material.CLOCK, 1,
                        String.format("§eAnalyzing... §6%d §eseconds left", secondsLeft), "§7Grab a coffee?"));
                for (int slot : SAMPLES) {
                    ItemStack stack = new ItemStack(Material.POTION, 1);
                    PotionMeta im = (PotionMeta) stack.getItemMeta();
                    im.displayName(Component.text(String.format("§6Sample %d", (slot-44)/2)));
                    im.setColor(Color.fromRGB(0xaaaaaa));
                }
            }
            case READY -> {
                inv.setItem(START_BUTTON, ItemMaker.createStack(Material.LIME_CONCRETE, 1, "§aAnalysis complete! Click on the sample with the anomaly."));
                for (int slot : SAMPLES) {
                    ItemStack stack = new ItemStack(Material.POTION, 1);
                    PotionMeta im = (PotionMeta) stack.getItemMeta();
                    im.displayName(Component.text(String.format("§6Sample %d", (slot-44)/2)));
                    im.setColor(whichSample.get(player.getUniqueId()) == slot ? Color.fromRGB(0xaa0000) : Color.fromRGB(0xffffff));
                }
            }
        }
    }

}
