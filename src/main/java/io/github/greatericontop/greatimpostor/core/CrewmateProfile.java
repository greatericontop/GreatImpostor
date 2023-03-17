package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.utils.ImpostorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CrewmateProfile extends PlayerProfile {

    public CrewmateProfile(GreatImpostorMain plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public boolean isImpostor() {
        return false;
    }

    @Override
    public void setActionBar() {
        int[] taskStatus = getTaskStatus(plugin.playerProfiles.values());
        String tasks = String.format("§e[§6Tasks §d%d/%d§e]", taskStatus[0], taskStatus[1]);
        player.sendActionBar(Component.text(tasks));
    }

    @Override
    public void setInventory() {
        Inventory inv = this.getPlayer().getInventory();
        inv.clear();

        inv.setItem(0, tasks.get(0).getDisplayItemStack(subtasksCompletedPerTask[0], "§7A - "));
        inv.setItem(1, tasks.get(1).getDisplayItemStack(subtasksCompletedPerTask[1], "§7B - "));
        inv.setItem(2, tasks.get(2).getDisplayItemStack(subtasksCompletedPerTask[2], "§7C - "));
        inv.setItem(8, ImpostorUtil.reportItemStack());

    }

}
