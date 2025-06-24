package io.github.greatericontop.greatimpostor.core.impostor;

/*
 * Copyright (C) 2023-present greateric.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty  of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import io.github.greatericontop.greatimpostor.GreatImpostorMain;
import io.github.greatericontop.greatimpostor.core.profiles.PlayerProfile;
import io.github.greatericontop.greatimpostor.core.profiles.ImpostorProfile;
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
        if (!profileGeneric.isImpostor()) {
            // cancel crewmates if they are shifting on a vent
            PartialCoordinates coordinates = PartialCoordinates.ofLocation(player.getLocation());
            if (plugin.gameManager.findVentSystem(coordinates) != null) {
                player.sendMessage("§7To prevent abuse, you can't shift on vents.");
                event.setCancelled(true);
            }
            return;
        }
        ImpostorProfile profile = (ImpostorProfile) profileGeneric;
        if (profile.isInVent) {
            exitVent(player, profile);
        } else {
            enterVent(player, profile);
        }
    }

    @EventHandler()
    public void onSpace(PlayerMoveEvent event) {
        if (event.getTo().getY() - 0.05 <= event.getFrom().getY())  return; // only runs when player jumps (y after > y before, with tolerance)
        if (movementCooldown.getOrDefault(event.getPlayer().getUniqueId(), false))  return;
        Player player = event.getPlayer();
        PlayerProfile profileGeneric = plugin.playerProfiles.get(player.getUniqueId());
        if (profileGeneric == null)  return;
        if (!profileGeneric.isImpostor())  return;
        ImpostorProfile profile = (ImpostorProfile) profileGeneric;
        if (profile.isInVent) {
            cycleVent(player, profile);
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
    }

    private void cycleVent(Player player, ImpostorProfile profile) {
        int ventSystem = profile.ventSystem;
        int ventNumber = profile.ventNumber;
        int nextVentNumber = (ventNumber + 1) % plugin.gameManager.getVentCount(ventSystem);
        profile.ventNumber = nextVentNumber;
        PartialCoordinates coordinates = plugin.gameManager.getVent(ventSystem, nextVentNumber);
        player.teleport(coordinates.teleportLocation(player.getWorld()));
    }

    public void setBackVentedImpostor(ImpostorProfile profile) {
        if (!profile.isInVent)  return;
        PartialCoordinates coordinates = plugin.gameManager.getVent(profile.ventSystem, profile.ventNumber);
        Player player = profile.getPlayer();
        if (coordinates.isClose(PartialCoordinates.ofLocation(player.getLocation())))  return;
        player.teleport(coordinates.teleportLocation(player.getWorld()));
    }

}
