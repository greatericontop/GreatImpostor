package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.utils.ImpostorUtil;
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


    @Override
    public boolean isImpostor() {
        return true;
    }

    public boolean getCanKill() {
        return nextKillTime <= plugin.getClock() && (!plugin.meetingManager.isMeetingActive());
    }

    public void resetCooldown(boolean isShort) {
        nextKillTime = plugin.getClock() + (isShort ? SHORT_COOLDOWN : LONG_COOLDOWN);
    }

    @Override
    public void setActionBar() {
        int[] taskStatus = getTaskStatus(plugin.playerProfiles.values());
        String tasks = String.format("§e[§6Tasks §d%d/%d§e]", taskStatus[0], taskStatus[1]);
        String kill;
        if (getCanKill()) {
            kill = String.format("§e[§cKill §aAVAILABLE§e]");
        } else {
            double seconds = 0.05 * (nextKillTime - plugin.getClock());
            kill = String.format("§e[§cKill §3%.1fs§e]", seconds);
        }
        player.sendActionBar(Component.text(String.format("%s %s", kill, tasks)));
    }

    @Override
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

        inv.setItem(8, ImpostorUtil.reportItemStack());

    }

}
