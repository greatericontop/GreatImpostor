package io.github.greatericontop.greatimpostor.core.events;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import io.github.greatericontop.greatimpostor.utils.PartialCoordinates;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SecurityCameraManager implements Listener {
    /*
     * The code here is quite similar to VentManager
     */

    private final List<PartialCoordinates> cameraSystem;

    private final Map<UUID, Location> originalLocations = new HashMap<>();
    private final Map<UUID, Boolean> movementCooldown = new HashMap<>();

    private final GreatImpostorMain plugin;
    public SecurityCameraManager(GreatImpostorMain plugin) {
        this.plugin = plugin;
        cameraSystem = new ArrayList<>();
        loadCamerasFromConfig();
    }

    private void loadCamerasFromConfig() {
        plugin.getConfig().getList("cameras").forEach(rawCoords -> {
            List<Double> coords = (List<Double>) rawCoords;
            cameraSystem.add(new PartialCoordinates(coords.get(0), coords.get(1), coords.get(2)));
        });
    }

    /*
     * Called when the sign is clicked
     */
    public void enterCameras(PlayerProfile profile, Player player) {
        if (!profile.isAlive()) {
            player.sendMessage("§cYou can't view security cameras while dead!");
            return;
        }
        if (profile.isInCameras) {
            player.sendMessage("§cYou're already viewing the security cameras.");
            return;
        }

        profile.isInCameras = true;
        profile.currentCameraNumber = -1; // cycleCamera will set it to 0
        originalLocations.put(player.getUniqueId(), player.getLocation());
        player.setGameMode(GameMode.SPECTATOR);
        cycleCamera(profile, player);
    }

    @EventHandler()
    public void onSpace(PlayerMoveEvent event) {
        if (event.getTo().getY() - 0.05 <= event.getFrom().getY())  return; // jump
        if (movementCooldown.getOrDefault(event.getPlayer().getUniqueId(), false))  return;
        Player player = event.getPlayer();
        PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
        if (profile == null)  return;
        if (profile.isInCameras) {
            cycleCamera(profile, player);
            movementCooldown.put(player.getUniqueId(), true);
            new BukkitRunnable() {
                public void run() {
                    movementCooldown.put(player.getUniqueId(), false);
                }
            }.runTaskLater(plugin, 10L);
        }
    }

    @EventHandler()
    public void onShift(PlayerToggleSneakEvent event) {
        if (!event.isSneaking())  return; // only runs when shift is toggled ON
        Player player = event.getPlayer();
        PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
        if (profile == null)  return;
        if (profile.isInCameras) {
            profile.isInCameras = false;
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(originalLocations.get(player.getUniqueId()));
        }
    }

    private void cycleCamera(PlayerProfile profile, Player player) {
        int nextNum = (profile.currentCameraNumber + 1) % cameraSystem.size();
        profile.currentCameraNumber = nextNum;
        PartialCoordinates cameraCoords = cameraSystem.get(nextNum);
        player.teleport(cameraCoords.teleportLocation(player.getWorld()));
    }

    // setBack

}
