package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.profiles.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.impostor.Sabotage;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import io.github.greatericontop.greatimpostor.utils.ImpostorUtil;
import io.github.greatericontop.greatimpostor.utils.PartialCoordinates;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private boolean inventoryChangeRequested;
    private List<List<PartialCoordinates>> vents = null;

    private final GreatImpostorMain plugin;
    public GameManager(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    public void requestInventoryChange() {
        inventoryChangeRequested = true;
    }

    public void resetAllSabotageCooldowns(boolean isShort) {
        for (PlayerProfile profile : plugin.playerProfiles.values()) {
            if (profile.isImpostor()) {
                ImpostorProfile impostorProfile = (ImpostorProfile) profile;
                impostorProfile.resetSabotageCooldownSelfOnly(isShort);
            }
        }
    }

    public void registerGameRunnable() {
        new BukkitRunnable() {
            public void run() {

                plugin.sabotageManager.tickSabotages();

                if (inventoryChangeRequested) {
                    for (PlayerProfile profile : plugin.playerProfiles.values()) {
                        profile.setInventory();
                    }
                    inventoryChangeRequested = false;
                }

                for (PlayerProfile profile : plugin.playerProfiles.values()) {
                    Player player = profile.getPlayer();

                    if (profile.isImpostor()) {
                        ImpostorProfile impostorProfile = (ImpostorProfile) profile;
                        impostorProfile.tickCooldowns();
                        plugin.ventManager.setBackVentedImpostor(impostorProfile);
                    }

                    plugin.securityCameraManager.setBackSecurityCameraPlayer(profile);
                    if (profile.isInCameras) {
                        if (plugin.sabotageManager.getActiveSabotage() == Sabotage.COMMUNICATIONS) {
                            player.sendMessage("§cCommunications sabotaged!");
                            plugin.securityCameraManager.exitCameras(profile, player);
                        }
                        // darkness applied if watching cameras (so you can't see too far with them)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 600, 0, false, false, false));
                    } else {
                        player.removePotionEffect(PotionEffectType.DARKNESS);
                    }
                    // if lights sabotage, disallow sprinting (takes effect immediately), otherwise refill hunger
                    player.setFoodLevel((plugin.sabotageManager.getActiveSabotage() == Sabotage.LIGHTS) ? 6 : 20);

                    if (!profile.isAlive()) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5, 0, false, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 5, 0, false, false, false));
                    }

                    if (plugin.meetingManager.isMeetingActive()) {
                        plugin.meetingManager.setMeetingActionBar(profile.getPlayer());
                    } else {
                        profile.setActionBar();
                    }
                }

                checkGameFinished();

            }
        }.runTaskTimer(plugin, 1L, 1L);

    }

    private void checkGameFinished() {
        if (plugin.playerProfiles.size() == 0) {
            return;
        }
        if (!plugin.getConfig().getBoolean("enable-victory-checks")) {
            return;
        }
        // all tasks completed
        int[] taskStatus = PlayerProfile.getTaskStatus(plugin.playerProfiles.values());
        if (taskStatus[1] > 0 && taskStatus[0] == taskStatus[1]) { // at least one task and all tasks completed
            endGame("§bCrewmates win! §aAll tasks were completed!");
            return;
        }

        // all impostors dead
        if (getAliveImpostorCount() == 0) {
            endGame("§bCrewmates win! §aNo impostors left!");
            return;
        }

        // impostors can't be voted out anymore
        if (getAliveImpostorCount() >= getAliveCrewCount()) {
            endGame("§cImpostors win! §aToo many crewmates died!");
            return;
        }

        // critical sabotage is handled by sabotage manager
    }

    private int getAliveImpostorCount() {
        int aliveImpostors = 0;
        for (PlayerProfile profile : plugin.playerProfiles.values()) {
            if (profile.isImpostor() && profile.isAlive()) {
                aliveImpostors++;
            }
        }
        return aliveImpostors;
    }
    private int getAliveCrewCount() {
        int aliveCrew = 0;
        for (PlayerProfile profile : plugin.playerProfiles.values()) {
            if ((!profile.isImpostor()) && profile.isAlive()) {
                aliveCrew++;
            }
        }
        return aliveCrew;
    }

    public void endGame(String message) {
        Bukkit.broadcast(Component.text("§9--------------------------------------------------"));
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text(message));
        Bukkit.broadcast(Component.text(""));
        Bukkit.broadcast(Component.text("§9--------------------------------------------------"));
        plugin.playerProfiles.clear();
        showAllPlayers();
        plugin.meetingManager.killMeeting();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(p.getWorld().getSpawnLocation());
            p.setGameMode(Bukkit.getDefaultGameMode());
        }
    }

    public void loadVents() {
        vents = new ArrayList<>();
        plugin.getConfig().getList("vents").forEach(ventSystem -> {
            List<List<Double>> convertedVentSystem = (List<List<Double>>) ventSystem;
            List<PartialCoordinates> ventSystemList = new ArrayList<>(convertedVentSystem.size());
            convertedVentSystem.forEach(vent -> ventSystemList.add(new PartialCoordinates(vent.get(0), vent.get(1), vent.get(2))));
            vents.add(ventSystemList);
        });
    }

    public int[] findVentSystem(PartialCoordinates coordinates) {
        for (int system = 0; system < vents.size(); system++) {
            for (int individualVent = 0; individualVent < vents.get(system).size(); individualVent++) {
                if (vents.get(system).get(individualVent).isClose(coordinates)) {
                    return new int[]{system, individualVent};
                }
            }
        }
        return null;
    }

    public int getVentCount(int system) {
        return vents.get(system).size();
    }

    public PartialCoordinates getVent(int ventSystem, int ventNumber) {
        return vents.get(ventSystem).get(ventNumber);
    }

    //
    // Game utils used by multiple classes
    //

    public void showAllPlayers() {
        for (Player p1 : Bukkit.getOnlinePlayers()) {
            for (Player p2 : Bukkit.getOnlinePlayers()) {
                if (!p1.equals(p2)) {
                    p1.showPlayer(plugin, p2);
                }
            }
        }
    }

    public int removeAllBodiesAndFakePlayers() {
        int amount = 0;
        for (Entity entity : plugin.getStartingLocation().getWorld().getEntities()) {
            if (entity instanceof ArmorStand armorStand) {
                if (armorStand.getPersistentDataContainer().has(ImpostorUtil.DEAD_BODY_KEY, PersistentDataType.INTEGER)
                        || armorStand.getPersistentDataContainer().has(ImpostorUtil.FAKE_PLAYER_KEY, PersistentDataType.STRING)
                ) {
                    armorStand.remove();
                    amount++;
                }
            }
        }
        return amount;
    }

}
