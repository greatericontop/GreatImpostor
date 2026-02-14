package io.github.greatericontop.greatimpostor.pathfinding;

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
import io.github.greatericontop.greatimpostor.task.Subtask;
import io.github.greatericontop.greatimpostor.task.sabotage.SabotageSubtask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class PathfindingHelperListener implements Listener {
    private static final int STEPS_NORMAL = 10;
    private static final int STEPS_SABOTAGE = 17;
    private static final int PARTICLE_STEP = 10;
    private static final int PARTICLE_STEP_DIAGONAL = 14;

    private final GreatImpostorMain plugin;
    public PathfindingHelperListener(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }


    @EventHandler()
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)  return;
        Player player = event.getPlayer();
        PlayerProfile profile = plugin.playerProfiles.get(player.getUniqueId());
        if (profile == null)  return;
        Material heldMat = player.getInventory().getItemInMainHand().getType();
        int heldIndex = player.getInventory().getHeldItemSlot();
        if (heldMat == Material.RED_STAINED_GLASS || heldMat == Material.YELLOW_STAINED_GLASS) {
            Subtask currentSubtask = profile.tasks.get(heldIndex);
            if (!plugin.mapGraph.signToGraph.containsKey(currentSubtask)) {
                player.sendMessage("§cSorry, I couldn't pathfind that task for you!");
                return;
            }
            XYZ target = plugin.mapGraph.signToGraph.get(currentSubtask);
            showPath(player, target, true, STEPS_NORMAL, Particle.WAX_OFF);
        }
    }

    public void showSabotagePath(Player player, SabotageSubtask subtask) {
        if (!plugin.mapGraph.sabotageSignToGraph.containsKey(subtask))  return;
        XYZ target = plugin.mapGraph.sabotageSignToGraph.get(subtask);
        showPath(player, target, false, STEPS_SABOTAGE, Particle.WAX_ON);
    }

    private void showPath(Player player, XYZ target, boolean showHint, int rayLength, Particle particle) {
        XYZ cur = new XYZ(player.getLocation().getBlockX(), plugin.mapGraph.yLevel, player.getLocation().getBlockZ());
        for (int i = 0; i < rayLength; i++) {
            XYZ next = plugin.mapGraph.shortestPathsCache.get(target).get(cur);
            if (next == null) {
                if (i == 0 && showHint) {
                    player.sendMessage("§cYour current position is obstructed, please get back on the floor and try again!");
                }
                break;
            }
            // Draw from :cur: to :next:
            int step = (cur.x() != next.x() && cur.z() != next.z()) ? PARTICLE_STEP_DIAGONAL : PARTICLE_STEP;
            Location loc = new Location(player.getWorld(), cur.x()+0.5, cur.y()+0.5, cur.z()+0.5);
            Vector vec = new Vector(next.x()-cur.x(), 0, next.z()-cur.z()).multiply(1.0/step);
            for (int j = 0; j < step; j++) {
                loc.add(vec);
                player.spawnParticle(particle, loc, 1, 0.0, 0.0, 0.0, 0.0);
            }
            cur = next;
        }
    }

}
