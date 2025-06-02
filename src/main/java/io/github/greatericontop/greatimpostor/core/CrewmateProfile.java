package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.impostor.Sabotage;
import io.github.greatericontop.greatimpostor.utils.ImpostorUtil;
import io.github.greatericontop.greatimpostor.utils.PlayerColor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CrewmateProfile extends PlayerProfile {

    public CrewmateProfile(GreatImpostorMain plugin, Player player, PlayerColor color) {
        super(plugin, player, color);
    }


    @Override
    public void die() {
        player.sendMessage("§6------------------------------");
        player.sendMessage("§bOops! Looks like you §cdied§b!");
        player.sendMessage("§eHelp the crew by finishing your tasks!");
        player.sendMessage("§7You may not report bodies, call meetings, vote, or fix sabotages.");
        player.sendMessage("§6------------------------------");
        dieGeneric();
    }

    @Override
    public boolean isImpostor() {
        return false;
    }

    @Override
    public void setActionBar() {
        if (plugin.sabotageManager.getActiveSabotage() == Sabotage.COMMUNICATIONS) {
            player.sendActionBar(Component.text(String.format("§c[!] §d[%s]", Sabotage.COMMUNICATIONS.getDisplayName())));
            return;
        }

        int[] taskStatus = getTaskStatus(plugin.playerProfiles.values());
        String tasks = String.format("§6[Total Tasks Completed §e%d/%d§6]", taskStatus[0], taskStatus[1]);
        String sabotage = "";
        if (plugin.sabotageManager.isSabotageActive()) {
            sabotage = String.format("   §d[%s]", plugin.sabotageManager.getActiveSabotage().getDisplayName());
        }
        player.sendActionBar(Component.text(String.format("%s%s", tasks, sabotage)));
    }

    @Override
    public void setInventory() {
        Inventory inv = super.setInventoryCommon();

        if (plugin.sabotageManager.getActiveSabotage() == Sabotage.COMMUNICATIONS) {
            for (int i = 0; i < 4; i++) {
                inv.setItem(i, ImpostorUtil.commsSabotageTaskDisplayItemStack());
            }
        } else {
            inv.setItem(0, tasks.get(0).getDisplayItemStack(subtasksCompletedPerTask[0], "§7Task A - "));
            inv.setItem(1, tasks.get(1).getDisplayItemStack(subtasksCompletedPerTask[1], "§7Task B - "));
            inv.setItem(2, tasks.get(2).getDisplayItemStack(subtasksCompletedPerTask[2], "§7Task C - "));
            inv.setItem(3, tasks.get(3).getDisplayItemStack(subtasksCompletedPerTask[3], "§7Task D - "));
        }

        inv.setItem(8, ImpostorUtil.reportItemStack());

    }

}
