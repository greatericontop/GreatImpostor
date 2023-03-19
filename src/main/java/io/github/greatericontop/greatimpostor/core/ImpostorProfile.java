package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.impostor.Sabotage;
import io.github.greatericontop.greatimpostor.utils.ImpostorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ImpostorProfile extends PlayerProfile {
    private static final int SHORT_COOLDOWN = 200;
    private static final int KILL_COOLDOWN = 700;
    private static final int SABOTAGE_COOLDOWN = 600;

    private int nextKillTime;
    private int nextSabotageTime;
    public Sabotage selectedSabotage;

    public ImpostorProfile(GreatImpostorMain plugin, Player player) {
        super(plugin, player);
        nextKillTime = plugin.getClock();
        nextSabotageTime = plugin.getClock();
        selectedSabotage = Sabotage.REACTOR;
    }


    @Override
    public boolean isImpostor() {
        return true;
    }

    public boolean getCanKill() {
        return nextKillTime <= plugin.getClock() && (!plugin.meetingManager.isMeetingActive());
    }
    public void resetKillCooldown(boolean isShort) {
        nextKillTime = plugin.getClock() + (isShort ? SHORT_COOLDOWN : KILL_COOLDOWN);
    }
    public boolean getCanSabotage() {
        return nextSabotageTime <= plugin.getClock() && (!plugin.meetingManager.isMeetingActive());
    }
    public void resetSabotageCooldown(boolean isShort) {
        nextSabotageTime = plugin.getClock() + (isShort ? SHORT_COOLDOWN : SABOTAGE_COOLDOWN);
    }

    @Override
    public void setActionBar() {
        int[] taskStatus = getTaskStatus(plugin.playerProfiles.values());
        String tasks = String.format("§6[Tasks §e%d/%d§6]", taskStatus[0], taskStatus[1]);
        String kill;
        if (getCanKill()) {
            kill = String.format("§c[Kill §aAVAILABLE§c]");
        } else {
            double seconds = 0.05 * (nextKillTime - plugin.getClock());
            kill = String.format("§c[Kill §3%.1fs§c]", seconds);
        }
        String sabotage;
        if (plugin.sabotageManager.isSabotageActive()) {
            sabotage = String.format("§d[%s §eACTIVE§d]", plugin.sabotageManager.getActiveSabotage().getDisplayName());
        } else if (getCanSabotage()) {
            sabotage = String.format("§d[%s §aREADY§d]", selectedSabotage.getDisplayName());
        } else {
            double seconds = 0.05 * (nextSabotageTime - plugin.getClock());
            sabotage = String.format("§d[%s §3%.1fs§d]", selectedSabotage.getDisplayName(), seconds);
        }
        player.sendActionBar(Component.text(String.format("%s   %s   %s", kill, tasks, sabotage)));
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
        im.displayName(Component.text("§4KILL"));
        kill.setItemMeta(im);
        inv.setItem(4, kill);

        ItemStack sabotageSelect = new ItemStack(Material.TNT, 1);
        im = sabotageSelect.getItemMeta();
        im.displayName(Component.text("§cSelect Sabotage"));
        im.lore(List.of(
                Component.text("§bHotkey §7to this item to select one"),
                Component.text("§7of the sabotages.")
        ));
        sabotageSelect.setItemMeta(im);
        inv.setItem(5, sabotageSelect);

        ItemStack sabotageActivate = new ItemStack(Material.REDSTONE_TORCH, 1);
        im = sabotageActivate.getItemMeta();
        im.displayName(Component.text("§cActivate Sabotage"));
        im.lore(List.of(
                Component.text("§bHotkey §7to this item to activate"),
                Component.text("§7your selected sabotage.")
        ));
        sabotageActivate.setItemMeta(im);
        inv.setItem(6, sabotageActivate);

        inv.setItem(8, ImpostorUtil.reportItemStack());

    }

}
