package io.github.greatericontop.greatimpostor.impostor;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class SabotageManager implements Listener {

    private Sabotage activeSabotage = null;
    private ImpostorProfile responsibleImpostor = null;

    private final GreatImpostorMain plugin;
    public SabotageManager(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    public Sabotage getActiveSabotage() {
        return activeSabotage;
    }
    public boolean isSabotageActive() {
        return activeSabotage != null;
    }

    @EventHandler()
    public void onHotkey(PlayerItemHeldEvent event) {
        int newSlot = event.getNewSlot();
        PlayerProfile profile = plugin.playerProfiles.get(event.getPlayer().getUniqueId());
        if (profile == null)  return;
        if (!profile.isImpostor())  return;
        if (newSlot == 5) {
            cycleSabotage((ImpostorProfile) profile);
            event.setCancelled(true);
        } else if (newSlot == 6) {
            activateSabotage((ImpostorProfile) profile);
            event.setCancelled(true);
        }
    }

    public void cycleSabotage(ImpostorProfile playerProfile) {
        Sabotage cur = playerProfile.selectedSabotage;
        playerProfile.selectedSabotage = Sabotage.values()[(cur.ordinal() + 1) % Sabotage.values().length];
    }

    public void activateSabotage(ImpostorProfile playerProfile) {
        Player player = playerProfile.getPlayer();
        if (isSabotageActive()) {
            player.sendMessage("§cThere is already a sabotage active!");
            return;
        }
        if (!playerProfile.getCanSabotage()) {
            player.sendMessage("§cYou can't sabotage right now!");
            return;
        }
        activeSabotage = playerProfile.selectedSabotage;
        responsibleImpostor = playerProfile;
        player.sendMessage(String.format("§6You activated §c%s§6.", activeSabotage.getDisplayName()));
    }

    public void deactivateSabotage() {
        activeSabotage = null;
        responsibleImpostor.resetSabotageCooldown(false);
    }

}
