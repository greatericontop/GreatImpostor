package io.github.greatericontop.greatimpostor.task.sabotagetaskexecutors;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.impostor.Sabotage;
import io.github.greatericontop.greatimpostor.task.BaseSabotageTask;
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
import java.util.Map;
import java.util.UUID;

public class SabotageOxygen extends BaseSabotageTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Oxygen Depleted";
    private static final int DISPLAY_CODE_SLOT = 16;
    private static final int[] BUTTON_LOCATIONS = {38, 10, 11, 12, 19, 20, 21, 28, 29, 30};
    private static final int DIGIT_COUNT = 5;

    private Map<UUID, Integer> playerCurrentDigits = new HashMap<>();
    private int code;

    public SabotageOxygen(GreatImpostorMain plugin) {
        super(plugin);
        code = 67890; // TODO: you definitely need some init() function called for every sabotage-begin
    }

    @Override
    public Sabotage getSabotage() {
        return Sabotage.OXYGEN; // TODO: you need two separate OXYGEN_1 and OXYGEN_2 in different locations
    }

    @Override
    public void startTask(Player player) {
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
        im.displayName(Component.text(String.format("§aToday's Code: §b%d", code)));
        displayCode.setItemMeta(im);
        gui.setItem(DISPLAY_CODE_SLOT, displayCode);

        playerCurrentDigits.put(player.getUniqueId(), DIGIT_COUNT-1);
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

        int currentDigitIndex = playerCurrentDigits.get(player.getUniqueId());
        int correctDigit = ((int) (code / Math.pow(10, currentDigitIndex))) % 10;
        player.sendMessage(
                String.format("§7[D] currentDigitIndex=%d correctDigit=%d clickedNumber=%d", currentDigitIndex, correctDigit, clickedNumber)
        );
        if (clickedNumber == correctDigit) {
            this.playSuccessSound(player);
            playerCurrentDigits.put(player.getUniqueId(), currentDigitIndex - 1);
            if (currentDigitIndex == 0) {
                player.closeInventory();
                this.taskSuccessful(player);
            }
        } else {
            this.playFailSound(player);
            player.sendMessage("§cTry again!");
        }
    }

}
