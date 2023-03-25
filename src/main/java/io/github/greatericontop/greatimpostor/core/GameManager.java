package io.github.greatericontop.greatimpostor.core;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.impostor.Sabotage;
import io.github.greatericontop.greatimpostor.utils.PartialCoordinates;
import org.bukkit.entity.Player;
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

                    // darkness effect for everyone (no particles or icon)
                    //player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 5, 0, false, false, false));
                    // if lights sabotage, disallow sprinting (takes effect immediately), otherwise refill hunger
                    player.setFoodLevel((plugin.sabotageManager.getActiveSabotage() == Sabotage.LIGHTS) ? 6 : 20);

                    if (plugin.meetingManager.isMeetingActive()) {
                        plugin.meetingManager.setMeetingActionBar(profile.getPlayer());
                    } else {
                        profile.setActionBar();
                    }
                }

            }
        }.runTaskTimer(plugin, 1L, 1L);

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

}
