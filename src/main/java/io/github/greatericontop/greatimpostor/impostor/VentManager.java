package io.github.greatericontop.greatimpostor.impostor;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.ImpostorProfile;
import io.github.greatericontop.greatimpostor.core.PlayerProfile;
import io.github.greatericontop.greatimpostor.utils.PartialCoordinates;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VentManager implements Listener {

    private final Map<UUID, Boolean> movementCooldown = new HashMap<>();

    private final GreatImpostorMain plugin;
    public VentManager(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onShift(PlayerToggleSneakEvent event) {
        if (!event.isSneaking())  return; // only runs when shift is toggled ON
        Player player = event.getPlayer();
        PlayerProfile profileGeneric = plugin.playerProfiles.get(player.getUniqueId());
        if (profileGeneric == null)  return;
        if (!profileGeneric.isImpostor())  return;
        ImpostorProfile profile = (ImpostorProfile) profileGeneric;
        if (profile.isInVent) {
            exitVent(player, profile);
        } else {
            enterVent(player, profile);
        }
    }

    @EventHandler()
    public void onSpace(PlayerMoveEvent event) {
        if (event.getTo().getY() <= event.getFrom().getY())  return; // only runs when player jumps (y after > y before)
        if (movementCooldown.getOrDefault(event.getPlayer().getUniqueId(), false))  return;
        Player player = event.getPlayer();
        PlayerProfile profileGeneric = plugin.playerProfiles.get(player.getUniqueId());
        if (profileGeneric == null)  return;
        if (!profileGeneric.isImpostor())  return;
        ImpostorProfile profile = (ImpostorProfile) profileGeneric;
        if (profile.isInVent) {
            cycleVent(player, profile);
            event.setCancelled(true); // extra QOL thing in addition to the setback if too far
            movementCooldown.put(player.getUniqueId(), true);
            new BukkitRunnable() {
                public void run() {
                    movementCooldown.put(player.getUniqueId(), false);
                }
            }.runTaskLater(plugin, 10L);
        }
    }

//    @EventHandler()
//    public void onSpace(PlayerJumpEvent event) {
//        Player player = event.getPlayer();
//        PlayerProfile profileGeneric = plugin.playerProfiles.get(player.getUniqueId());
//        if (profileGeneric == null)  return;
//        if (!profileGeneric.isImpostor())  return;
//        ImpostorProfile profile = (ImpostorProfile) profileGeneric;
//        if (profile.isInVent) {
//            cycleVent(player, profile);
//        }
//    }




    private void exitVent(Player player, ImpostorProfile profile) {
        profile.isInVent = false;

        PartialCoordinates coordinates = plugin.gameManager.getVent(profile.ventSystem, profile.ventNumber);
        player.teleport(coordinates.teleportLocation(player.getWorld()));

        // un-vanish
        player.setGameMode(GameMode.ADVENTURE);

        player.sendMessage("§7[D] exiting vent");
    }

    private void enterVent(Player player, ImpostorProfile profile) {
        Location playerLocation = player.getLocation();
        PartialCoordinates coordinates = PartialCoordinates.ofLocation(playerLocation);
        int[] ventData = plugin.gameManager.findVentSystem(coordinates);
        if (ventData == null)  return;
        int ventSystem = ventData[0];
        int ventNumber = ventData[1];

        if (!profile.isAlive()) {
            player.sendMessage("§cYou're dead! You can't use vents!");
            return;
        }

        profile.isInVent = true;
        profile.ventSystem = ventSystem;
        profile.ventNumber = ventNumber;
        profile.applyVentEntrancePenalty();

        // vanish
        player.setGameMode(GameMode.SPECTATOR);

        player.sendMessage(String.format("[D] entering §7ventSystem=%d ventIndex=%d", ventSystem, ventNumber));
    }

    private void cycleVent(Player player, ImpostorProfile profile) {
        int ventSystem = profile.ventSystem;
        int ventNumber = profile.ventNumber;
        int nextVentNumber = (ventNumber + 1) % plugin.gameManager.getVentCount(ventSystem);
        profile.ventNumber = nextVentNumber;
        PartialCoordinates coordinates = plugin.gameManager.getVent(ventSystem, nextVentNumber);
        player.teleport(coordinates.teleportLocation(player.getWorld()));
        player.sendMessage(String.format("[D] §eCYCLE §7ventSystem=%d ventIndex=%d", ventSystem, nextVentNumber));
    }

    public void setBackVentedImpostor(ImpostorProfile profile) {
        if (!profile.isInVent)  return;
        PartialCoordinates coordinates = plugin.gameManager.getVent(profile.ventSystem, profile.ventNumber);
        Player player = profile.getPlayer();
        if (coordinates.isClose(PartialCoordinates.ofLocation(player.getLocation())))  return;
        player.teleport(coordinates.teleportLocation(player.getWorld()));
    }

}
