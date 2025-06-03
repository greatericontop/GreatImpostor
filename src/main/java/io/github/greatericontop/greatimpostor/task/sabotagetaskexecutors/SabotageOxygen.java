package io.github.greatericontop.greatimpostor.task.sabotagetaskexecutors;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.impostor.Sabotage;
import io.github.greatericontop.greatimpostor.task.BaseSabotageTask;
import io.github.greatericontop.greatimpostor.task.SabotageSubtask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SabotageOxygen extends BaseSabotageTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Oxygen Depleted";
    private static final int DISPLAY_CODE_SLOT = 16;
    private static final int[] BUTTON_LOCATIONS = {38, 10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final int DIGIT_COUNT = 5;

    private Map<UUID, Integer> playerCurrentDigits = new HashMap<>();
    private Map<UUID, Integer> playerSubtaskNumber = new HashMap<>();
    private int[] codes = null;
    private int totalCompletionState = 0;

    public SabotageOxygen(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public Sabotage getSabotage() {
        return Sabotage.OXYGEN;
    }

    @Override
    public void prepareSabotageTask() {
        codes = new int[]{new Random().nextInt(100000), new Random().nextInt(100000)};
        totalCompletionState = 0;
    }

    @Override
    public void startTask(Player player, SabotageSubtask sabotageSubtask) {
        int sabotageNumber = sabotageSubtask.getMagicNumber();
        if ((totalCompletionState & (1 << sabotageNumber)) != 0) {
            player.sendMessage("§cThis panel has already been fixed! Go fix the other one!");
            return;
        }
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        for (int i = 0; i < 10; i++) {
            Material mat = BUTTON_LOCATIONS[i] % 2 == 0 ? Material.BLUE_STAINED_GLASS_PANE : Material.LIGHT_BLUE_STAINED_GLASS_PANE;
            ItemStack button = new ItemStack(mat, Math.max(i, 1));
            ItemMeta im = button.getItemMeta();
            im.displayName(Component.text("§e" + i));
            button.setItemMeta(im);
            gui.setItem(BUTTON_LOCATIONS[i], button);
        }

        ItemStack displayCode = new ItemStack(Material.PAPER, 1);
        ItemMeta im = displayCode.getItemMeta();
        im.addEnchant(Enchantment.LUCK, 1, true);
        im.displayName(Component.text(String.format("§aToday's Code: §b%05d", codes[sabotageNumber])));
        displayCode.setItemMeta(im);
        gui.setItem(DISPLAY_CODE_SLOT, displayCode);

        playerCurrentDigits.put(player.getUniqueId(), DIGIT_COUNT-1);
        playerSubtaskNumber.put(player.getUniqueId(), sabotageNumber);
        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        int clickedSlot = event.getSlot();
        int clickedNumber = -2;
        for (int i = 0; i < 10; i++) {
            if (clickedSlot == BUTTON_LOCATIONS[i]) {
                clickedNumber = i;
                break;
            }
        }
        if (clickedNumber == -2)  return;

        int currentSabotageNumber = playerSubtaskNumber.get(player.getUniqueId());
        int currentDigitIndex = playerCurrentDigits.get(player.getUniqueId());
        int correctDigit = ((int) (codes[currentSabotageNumber] / Math.pow(10, currentDigitIndex))) % 10;
        if (clickedNumber == correctDigit) {
            this.playSuccessSound(player);
            playerCurrentDigits.put(player.getUniqueId(), currentDigitIndex - 1);
            if (currentDigitIndex == 0) {
                player.closeInventory();
                player.sendMessage("§aYou fixed this panel!");
                totalCompletionState |= 1 << playerSubtaskNumber.get(player.getUniqueId());
                if (totalCompletionState == 0b11) {
                    this.taskSuccessful(player);
                }
            }
        } else {
            this.playFailSound(player);
            player.closeInventory();
            player.sendMessage("§cTry again!");
        }
    }

    @Override
    public double[][] getPOICoordinates() {
        List<Double> coordsOxygen = plugin.getConfig().getDoubleList("sabotage-fix-coordinates.oxygen-in-oxygen");
        List<Double> coordsAdmin = plugin.getConfig().getDoubleList("sabotage-fix-coordinates.oxygen-in-oxygen");
        // questionable code quality
        if (totalCompletionState == 0b00) {
            return new double[][]{ {coordsOxygen.get(0), coordsOxygen.get(1)}, {coordsAdmin.get(0), coordsAdmin.get(1)} };
        }
        if (totalCompletionState == 0b01) { // oxygen done so we still need admin
            return new double[][]{ {coordsAdmin.get(0), coordsAdmin.get(1)} };
        }
        if (totalCompletionState == 0b10) {
            return new double[][]{ {coordsOxygen.get(0), coordsOxygen.get(1)} };
        }
        return new double[][]{}; // shouldn't happen
    }

}
