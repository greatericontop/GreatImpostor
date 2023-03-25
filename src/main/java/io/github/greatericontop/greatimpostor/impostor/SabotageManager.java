package io.github.greatericontop.greatimpostor.impostor;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import io.github.greatericontop.greatimpostor.task.TaskUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;

public class SabotageManager implements Listener {

    private Sabotage activeSabotage = null;
    private ImpostorProfile responsibleImpostor = null;
    private int criticalCountdown = -1;

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
    public boolean isDisruptiveSabotageActive() {
        return isSabotageActive() && activeSabotage.disruptsGame();
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
        criticalCountdown = 600;
        TaskUtil.getSabotageTaskClass(plugin, activeSabotage).prepareSabotageTask();
        player.sendMessage(String.format("§6You activated §c%s§6.", activeSabotage.getDisplayName()));
        if (activeSabotage == Sabotage.COMMUNICATIONS) { // update inventory for communications sabotage
            plugin.gameManager.requestInventoryChange();
        }
    }

    public void endSabotage(Sabotage sabotageType) {
        if (activeSabotage == sabotageType) {
            forceEndSabotage();
        }
    }
    public void forceEndSabotage() {
        if (activeSabotage == Sabotage.COMMUNICATIONS) { // update inventory for communications sabotage
            plugin.gameManager.requestInventoryChange();
        }
        activeSabotage = null;
        responsibleImpostor.resetSabotageCooldown(false);
    }

    public void tickSabotages() {
        if (!isSabotageActive())  return;

        if (activeSabotage.isCritical()) {
            criticalCountdown--;
            int seconds = criticalCountdown / 20;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showTitle(Title.title(
                        Component.text(String.format("§c%s", activeSabotage.getDisplayName())),
                        Component.text(String.format("§7%s", seconds)),
                        Title.Times.times(Duration.ofMillis(0L), Duration.ofMillis(200L), Duration.ofMillis(1000L))
                ));
                if (criticalCountdown % 40 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8F, 0.5F);
                }
            }
            if (criticalCountdown <= 0) {
                Bukkit.broadcast(Component.text("§cThe critical sabotage was not fixed in time! The game is over!"));
                forceEndSabotage();
                return;
            }
        }

        switch (activeSabotage) {
            case REACTOR, OXYGEN -> {}
            case LIGHTS -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // note: hunger will be handled later in GameManager
                    PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
                    if (profile == null)  continue;
                    if (!profile.isImpostor()) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 0));
                    }
                    // this counters the effect only somewhat
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 0));
                }
            }
            case COMMUNICATIONS -> {}
        }
    }

}
