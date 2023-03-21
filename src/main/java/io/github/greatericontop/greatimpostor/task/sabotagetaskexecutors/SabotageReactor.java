package io.github.greatericontop.greatimpostor.task.sabotagetaskexecutors;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.impostor.Sabotage;
import io.github.greatericontop.greatimpostor.task.BaseSabotageTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SabotageReactor extends BaseSabotageTask {
    public static final String INVENTORY_NAME = "§aAmong Us - Reactor Meltdown";

    private Player otherPlayer;

    public SabotageReactor(GreatImpostorMain plugin) {
        super(plugin);
    }

    @Override
    public Sabotage getSabotage() {
        return Sabotage.REACTOR;
    }

    @Override
    public void prepareSabotageTask() {
        otherPlayer = null;
    }

    @Override
    public void startTask(Player player) {
        Inventory gui = Bukkit.createInventory(player, 45, Component.text(INVENTORY_NAME));

        ItemStack stack = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS, 1);
        ItemMeta im = stack.getItemMeta();
        im.displayName(Component.text("§bClick"));
        stack.setItemMeta(im);
        gui.setItem(22, stack);

        player.openInventory(gui);
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_NAME))  return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() != 22)  return;
        if (event.getInventory().getItem(22) == null)  return;
        event.getInventory().setItem(22, null);
        this.playSuccessSound(player);
        if (otherPlayer != null ){//&& !otherPlayer.getUniqueId().equals(player.getUniqueId())) { // TODO: for testing, allow same player to do it twice
            this.playSuccessSound(otherPlayer);
            player.closeInventory();
            otherPlayer.closeInventory();
            this.taskSuccessful(player);
            this.taskSuccessful(otherPlayer); // no harm in calling this twice
        } else {
            otherPlayer = player;
        }

    }

}
