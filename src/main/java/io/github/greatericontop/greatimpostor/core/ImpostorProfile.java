package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.impostor.Sabotage;
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
    private static final int SHORT_COOLDOWN_LEN = 200;
    private static final int KILL_COOLDOWN_LEN = 700;
    private static final int SABOTAGE_COOLDOWN_LEN = 600;

    public boolean isInVent;
    public int ventSystem = -1;
    public int ventNumber = -1;
    private int killCooldownTicks;
    private int sabotageCooldownTicks;
    public Sabotage selectedSabotage;

    public ImpostorProfile(GreatImpostorMain plugin, Player player) {
        super(plugin, player);
        isInVent = false;
        killCooldownTicks = Integer.MAX_VALUE;
        sabotageCooldownTicks = Integer.MAX_VALUE;
        selectedSabotage = Sabotage.REACTOR;
    }


    @Override
    public void die() {
        player.sendMessage("§6------------------------------");
        player.sendMessage("§bOops! Looks like you §cdied§b!");
        player.sendMessage("§e(You can still kill and sabotage, you shouldn't be able to. And what about shared cooldowns?)");
        player.sendMessage("§7You may not report bodies, call meetings, vote, or fix sabotages.");
        player.sendMessage("§7You may not §c(unfinished§7) kill, sabotage, or vent.");
        player.sendMessage("§6------------------------------");
        dieGeneric();
    }

    @Override
    public boolean isImpostor() {
        return true;
    }

    public boolean getCanKill() {
        return killCooldownTicks <= 0 && (!plugin.meetingManager.isMeetingActive()) && (!isInVent);
    }
    public void resetKillCooldown(boolean isShort) {
        killCooldownTicks = (isShort ? SHORT_COOLDOWN_LEN : KILL_COOLDOWN_LEN);
    }
    public boolean getCanSabotage() {
        return sabotageCooldownTicks <= 0 && (!plugin.meetingManager.isMeetingActive()) && (!isInVent);
    }
    public void resetSabotageCooldown(boolean isShort) {
        sabotageCooldownTicks = (isShort ? SHORT_COOLDOWN_LEN : SABOTAGE_COOLDOWN_LEN);
    }
    public void applyVentEntrancePenalty() {
        // increased by 3 seconds each time you enter a vent (no penalty if ability is ready)
        if (killCooldownTicks > 0) {
            killCooldownTicks = Math.min(killCooldownTicks + 60, KILL_COOLDOWN_LEN);
        }
        if (sabotageCooldownTicks > 0) {
            sabotageCooldownTicks = Math.min(sabotageCooldownTicks + 60, SABOTAGE_COOLDOWN_LEN);
        }
    }
    public void tickCooldowns() {
        if (!isInVent) {
            killCooldownTicks--;
            sabotageCooldownTicks--;
        }
    }

    @Override
    public void setActionBar() {
        if (isInVent) {
            String vent = "§aYou're in a vent!  §7|  §eJUMP §bto cycle  §7|  §eSNEAK §bto exit";
            player.sendActionBar(Component.text(vent));
            return;
        }

        int[] taskStatus = getTaskStatus(plugin.playerProfiles.values());
        String tasks = String.format("§6[Tasks §e%d/%d§6]", taskStatus[0], taskStatus[1]);
        String kill;
        if (getCanKill()) {
            kill = "§c[Kill §aAVAILABLE§c]";
        } else {
            double seconds = 0.05 * killCooldownTicks;
            kill = String.format("§c[Kill §3%.1fs§c]", seconds);
        }
        String sabotage;
        if (plugin.sabotageManager.isSabotageActive()) {
            sabotage = String.format("§d[%s §eACTIVE§d]", plugin.sabotageManager.getActiveSabotage().getDisplayName());
        } else if (getCanSabotage()) {
            sabotage = String.format("§d[%s §aREADY§d]", selectedSabotage.getDisplayName());
        } else {
            double seconds = 0.05 * sabotageCooldownTicks;
            sabotage = String.format("§d[%s §3%.1fs§d]", selectedSabotage.getDisplayName(), seconds);
        }
        player.sendActionBar(Component.text(String.format("%s   %s   %s", kill, tasks, sabotage)));
    }

    @Override
    public void setInventory() {
        Inventory inv = this.getPlayer().getInventory();
        inv.clear();

        if (plugin.sabotageManager.getActiveSabotage() == Sabotage.COMMUNICATIONS) {
            for (int i = 0; i < 3; i++) { // 3
                inv.setItem(i, ImpostorUtil.commsSabotageTaskDisplayItemStack());
            }
        } else {
            inv.setItem(0, tasks.get(0).getDisplayItemStack(subtasksCompletedPerTask[0], "§7Fake A - "));
            inv.setItem(1, tasks.get(1).getDisplayItemStack(subtasksCompletedPerTask[1], "§7Fake B - "));
            inv.setItem(2, tasks.get(2).getDisplayItemStack(subtasksCompletedPerTask[2], "§7Fake C - "));
            //inv.setItem(3, tasks.get(3).getDisplayItemStack(subtasksCompletedPerTask[3], "§7Fake D - "));
        }

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
