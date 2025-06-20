package io.github.greatericontop.greatimpostor.task.sabotage;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import io.github.greatericontop.greatimpostor.core.profiles.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.impostor.Sabotage;
import io.github.greatericontop.greatimpostor.task.TaskUtil;
import io.github.greatericontop.greatimpostor.utils.ImpostorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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
    public boolean shouldRemoveWhenBodyReported() {
        return isSabotageActive() && (activeSabotage == Sabotage.REACTOR || activeSabotage == Sabotage.OXYGEN);
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
        if (!playerProfile.isAlive()) {
            player.sendMessage("§cYou can't sabotage while dead!"); // TODO: allow dead to sabotage
            return;
        }
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
        criticalCountdown = plugin.getConfig().getInt("critical-sabotage-fix-ticks");
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
        // All impostors' cooldowns are reset together.
        // They are not completely shared though, because individual impostors can, for example, have their cooldowns
        //   not run while they are hiding in vents.
        plugin.gameManager.resetAllSabotageCooldowns(false);
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
                forceEndSabotage();
                plugin.gameManager.endGame("§cImpostors win! §aThe sabotage caused the crew to die!");
                return;
            }
        }

        double[][] poiCoordinates = activeSabotage.getPOICoordinates(plugin);
        if (plugin.getClock() % 30 == 0) {
            for (double[] coord : poiCoordinates) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        // Don't show to impostors in vents who are in spectator
                        continue;
                    }
                    Location loc = ImpostorUtil.forceLocationDownwards(player.getLocation());
                    Vector arrowDirection = new Vector(coord[0] - loc.getX(), 0, coord[1] - loc.getZ())
                            .normalize().multiply(0.09);
                    for (int i = 0; i < 60; i++) {
                        loc.add(arrowDirection);
                        loc.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1, 0.0, 0.0, 0.0);
                    }
                }
            }
        }

        switch (activeSabotage) {
            case REACTOR, OXYGEN -> {}
            case LIGHTS -> {
                for (Player player : Bukkit.getOnlinePlayers()) { // TODO: use PlayerProfile?
                    // note: hunger will be handled later in GameManager
                    PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
                    if (profile == null)  continue;
                    if (!profile.isImpostor()) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 39, 0));
                    }
                    // this counters the effect only somewhat
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, 0));
                }
            }
            case COMMUNICATIONS -> {}
        }
    }

}
