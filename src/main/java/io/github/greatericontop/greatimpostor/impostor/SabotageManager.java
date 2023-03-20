package io.github.greatericontop.greatimpostor.impostor;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
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
    public int getCriticalCountdown() {
        return criticalCountdown;
    }
    public boolean isCriticalSabotageActive() {
        return isSabotageActive() && activeSabotage.isCritical();
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
        player.sendMessage(String.format("§6You activated §c%s§6.", activeSabotage.getDisplayName()));
    }

    public void endSabotage(Sabotage sabotageType) {
        if (activeSabotage == sabotageType) {
            forceEndSabotage();
        }
    }
    public void forceEndSabotage() {
        activeSabotage = null;
        responsibleImpostor.resetSabotageCooldown(false);
    }

    public void tickSabotages() {
        if (!isSabotageActive())  return;
        switch (activeSabotage) {
            case REACTOR -> {
                criticalCountdown--;
                int seconds = criticalCountdown / 20;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.showTitle(Title.title(
                            Component.text("§cReactor Meltdown"),
                            Component.text(String.format("§7%s", seconds)),
                            Title.Times.times(Duration.ofMillis(0L), Duration.ofMillis(1000L), Duration.ofMillis(1000L))
                    ));
                    if (criticalCountdown <= 0) {
                        player.setHealth(0.0);
                    }
                }
            }
            case OXYGEN -> {
                criticalCountdown--;
                int seconds = criticalCountdown / 20;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.showTitle(Title.title(
                            Component.text("§cOxygen Depleted"),
                            Component.text(String.format("§7%s", seconds)),
                            Title.Times.times(Duration.ofMillis(0L), Duration.ofMillis(1000L), Duration.ofMillis(1000L))
                    ));
                    if (criticalCountdown <= 0) {
                        player.setHealth(0.0);
                    }
                }
            }
            case LIGHTS -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // TODO: impostors get their hunger set down to prevent them from sprinting, but don't have blindness
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                    // speed counters the no-sprint of the blindness, but you're still much slower than before
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0));
                }
            }
            case COMMUNICATIONS -> {}
        }
    }

}
