package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ImpostorProfile extends PlayerProfile {
    private static final int SHORT_COOLDOWN = 200;
    private static final int LONG_COOLDOWN = 700;

    private int nextKillTime;

    public ImpostorProfile(GreatImpostorMain plugin, Player player) {
        super(plugin, player);
        nextKillTime = plugin.getClock();
    }


    public boolean isImpostor() {
        return true;
    }

    public boolean getCanKill() {
        return nextKillTime <= plugin.getClock();
    }

    public void resetCooldown(boolean isShort) {
        nextKillTime = plugin.getClock() + (isShort ? SHORT_COOLDOWN : LONG_COOLDOWN);
    }

    public void setActionBar() {
        if (getCanKill()) {
            player.sendActionBar(Component.text(String.format("§e[§cKill §aAVAILABLE§e]")));
        } else {
            double seconds = 0.05 * (nextKillTime - plugin.getClock());
            player.sendActionBar(Component.text(String.format("§e[§cKill §b%.1fs§e]", seconds)));
        }
    }

    public void setInventory() {
        Inventory inv = this.getPlayer().getInventory();
        inv.clear();

        inv.setItem(0, tasks.get(0).getDisplayItemStack(subtasksCompletedPerTask[0], "§7Fake A - "));
        inv.setItem(1, tasks.get(1).getDisplayItemStack(subtasksCompletedPerTask[1], "§7Fake B - "));
        inv.setItem(2, tasks.get(2).getDisplayItemStack(subtasksCompletedPerTask[2], "§7Fake C - "));

        ItemStack kill = new ItemStack(Material.NETHERITE_SWORD, 1);
        ItemMeta im = kill.getItemMeta();
        im.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        im.setDisplayName("§4KILL");
        kill.setItemMeta(im);
        inv.setItem(4, kill);

    }

}
