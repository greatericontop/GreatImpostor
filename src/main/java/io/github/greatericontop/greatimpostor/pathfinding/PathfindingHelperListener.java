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
    private static final int STEPS = 10;
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

            XYZ cur = new XYZ(player.getLocation().getBlockX(), plugin.mapGraph.yLevel, player.getLocation().getBlockZ());
            for (int i = 0; i < STEPS; i++) {
                XYZ next = plugin.mapGraph.shortestPathsCache.get(target).get(cur);
                if (next == null)  break;
                // Draw from :cur: to :next:
                int step = (cur.x() != next.x() && cur.z() != next.z()) ? PARTICLE_STEP_DIAGONAL : PARTICLE_STEP;
                Location loc = new Location(player.getWorld(), cur.x()+0.5, cur.y()+0.5, cur.z()+0.5);
                Vector vec = new Vector(next.x()-cur.x(), 0, next.z()-cur.z()).multiply(1.0/step);
                for (int j = 0; j < step; j++) {
                    loc.add(vec);
                    loc.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1, 0.0, 0.0, 0.0, 0.0);
                }
                cur = next;
            }
        }
    }


}
