package io.github.greatericontop.greatimpostor.task.sabotagetaskexecutors;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.impostor.Sabotage;
import io.github.greatericontop.greatimpostor.task.sabotage.SabotageSubtask;
import io.github.greatericontop.greatimpostor.task.sabotage.BaseSabotageTask;
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

import java.util.Random;

public class SabotageCommunications extends BaseSabotageTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Communications";
    private static final int[] INPUT = {4, 13, 22};
    private static final int[] OPTIONS = {37, 39, 41, 43};
    private static final Material[] MATERIALS = {Material.RED_WOOL, Material.LIME_WOOL, Material.BLUE_WOOL, Material.YELLOW_WOOL};
    private static final String[] NAMES = {"§cRED", "§aGREEN", "§9BLUE", "§eYELLOW"};

    private int answer = -1;

    public SabotageCommunications(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public Sabotage getSabotage() {
        return Sabotage.COMMUNICATIONS;
    }

    @Override
    public void prepareSabotageTask() {
        answer = new Random().nextInt(OPTIONS.length);
    }

    @Override
    public void startTask(Player player, SabotageSubtask sabotageSubtask) {
        Inventory gui = Bukkit.createInventory(player, 54, Component.text(INVENTORY_NAME));

        for (int slot : INPUT) {
            gui.setItem(slot, inputItemStack(answer));
        }
        for (int i = 0; i < OPTIONS.length; i++) {
            gui.setItem(OPTIONS[i], new ItemStack(MATERIALS[i], 1));
        }

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        int clicked = findClick(event.getSlot());
        if (clicked == -1)  return;
        if (clicked == answer) {
            this.playSuccessSound(player);
            this.taskSuccessful(player);
            player.closeInventory();
        } else {
            this.playFailSound(player);
            player.sendMessage("§cYou rerouted the communications to the wrong place!");
            player.closeInventory();
        }

    }

    private int findClick(int slotClicked) {
        // Why is there no builtin for this? Why?
        for (int i = 0; i < OPTIONS.length; i++) {
            if (OPTIONS[i] == slotClicked) {
                return i;
            }
        }
        return -1;
    }

    private ItemStack inputItemStack(int ans) {
        ItemStack stack = new ItemStack(MATERIALS[ans], 1);
        ItemMeta im = stack.getItemMeta();
        im.addEnchant(Enchantment.LUCK, 1, true);
        im.displayName(Component.text(String.format("§7Click on the %s§7.", NAMES[ans])));
        stack.setItemMeta(im);
        return stack;
    }

}
