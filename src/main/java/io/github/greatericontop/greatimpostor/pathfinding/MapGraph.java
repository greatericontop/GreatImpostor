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
import io.github.greatericontop.greatimpostor.task.SignListener;
import io.github.greatericontop.greatimpostor.task.Subtask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class MapGraph {
    // Note: understanding this class requires knowledge of graph algorithms

    private static final int MAX_NODES = 15_000; // somewhat arbitrary, but should be enough for reasonably sized maps
    private static final int MAX_Y = 3; // Search -3 to +3 in the y direction for signs

    public final List<String> messages = new ArrayList<>();
    private final Map<XYZ, List<XYZ>> adj = new HashMap<>();
    // XYZ target -> parent table
    private final Map<XYZ, Map<XYZ, XYZ>> shortestPathsCache = new HashMap<>();
    // Maps a subtask's sign to its XYZ location in the graph
    private final Map<Subtask, XYZ> signToGraph = new HashMap<>();

    private final GreatImpostorMain plugin;
    public MapGraph(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }

    public void generate(World world, XYZ root) {
        if (!adj.isEmpty()) {
            throw new IllegalStateException("Graph has already been generated");
        }
        if (!root.isEmpty(world)) {
            messages.add("Root vertex of the map graph is not air, cannot generate graph");
            plugin.getLogger().warning("Root vertex of the map graph is not air, cannot generate graph");
            return;
        }

        Set<XYZ> visited = new HashSet<>();
        Queue<XYZ> vertexQueue = new ArrayDeque<>();
        vertexQueue.add(root);
        while (!vertexQueue.isEmpty() && adj.size() < MAX_NODES) {
            XYZ vertex = vertexQueue.poll();
            if (visited.contains(vertex)) {
                continue;
            }
            visited.add(vertex);
            if (!vertex.isEmpty(world)) {
                throw new IllegalStateException("Vertex in the vertex queue is not air, this should never happen!");
            }
            if (adj.containsKey(vertex)) {
                throw new IllegalStateException("There should be no adj entry for a vertex in the vertex queue!");
            }
            adj.put(vertex, new ArrayList<>(8));

            // Each vertex will be visited once, but all edges are added correctly even if
            // its neighbors have already been visited.
            // We only need to make adj entries for ourselves; our neighbors will add us when they are processed.
            // If we hit the limit, we will have MAX_NODES "correct" vertices with some extra edges that go
            // to black holes. This shouldn't affect any algorithms though.

            XYZ plusX = vertex.add(1, 0);
            XYZ minusX = vertex.add(-1, 0);
            XYZ plusZ = vertex.add(0, 1);
            XYZ minusZ = vertex.add(0, -1);
            XYZ plusXPlusZ = vertex.add(1, 1);
            XYZ plusXMinusZ = vertex.add(1, -1);
            XYZ minusXPlusZ = vertex.add(-1, 1);
            XYZ minusXMinusZ = vertex.add(-1, -1);
            if (plusX.isEmpty(world)) {
                adj.get(vertex).add(plusX);
                vertexQueue.add(plusX);
            }
            if (minusX.isEmpty(world)) {
                adj.get(vertex).add(minusX);
                vertexQueue.add(minusX);
            }
            if (plusZ.isEmpty(world)) {
                adj.get(vertex).add(plusZ);
                vertexQueue.add(plusZ);
            }
            if (minusZ.isEmpty(world)) {
                adj.get(vertex).add(minusZ);
                vertexQueue.add(minusZ);
            }
            if (plusXPlusZ.isEmpty(world) && plusX.isEmpty(world) && plusZ.isEmpty(world)) {
                adj.get(vertex).add(plusXPlusZ);
                vertexQueue.add(plusXPlusZ);
            }
            if (plusXMinusZ.isEmpty(world) && plusX.isEmpty(world) && minusZ.isEmpty(world)) {
                adj.get(vertex).add(plusXMinusZ);
                vertexQueue.add(plusXMinusZ);
            }
            if (minusXPlusZ.isEmpty(world) && minusX.isEmpty(world) && plusZ.isEmpty(world)) {
                adj.get(vertex).add(minusXPlusZ);
                vertexQueue.add(minusXPlusZ);
            }
            if (minusXMinusZ.isEmpty(world) && minusX.isEmpty(world) && minusZ.isEmpty(world)) {
                adj.get(vertex).add(minusXMinusZ);
                vertexQueue.add(minusXMinusZ);
            }
        }

        messages.add("Generated map graph with %d vertices!".formatted(adj.size()));
        plugin.getLogger().info("Generated map graph with %d vertices!".formatted(adj.size()));
    }

    private void generateSingleTargetShortestPath(XYZ root) {
        if (adj.isEmpty()) {
            throw new IllegalStateException("Graph has not been generated yet");
        }
        // Parent table: BFS out from root; map[vertex -> parent]
        // Then when we want the shortest path from [any vertex -> root], we follow the parent pointers
        // Parent table also used to check visited
        Map<XYZ, XYZ> parentTable = new HashMap<>();
        Queue<XYZ> vertexQueue = new ArrayDeque<>();
        vertexQueue.add(root);
        while (!vertexQueue.isEmpty()) {
            XYZ vertex = vertexQueue.poll();
            for (XYZ neighbor : adj.get(vertex)) {
                if (parentTable.containsKey(neighbor)) {
                    continue;
                }
                parentTable.put(neighbor, vertex);
                vertexQueue.add(neighbor);
            }
        }
        shortestPathsCache.put(root, parentTable);
    }

    public void findSignsInGraph(World world) {
        if (adj.isEmpty()) {
            throw new IllegalStateException("Graph has not been generated yet");
        }
        for (XYZ vertex : adj.keySet()) {
            Location vertexLoc = vertex.toLocation(world);
            for (int y = -MAX_Y; y <= MAX_Y; y++) {
                Block b = world.getBlockAt(vertexLoc.clone().add(0, y, 0));
                if (b.getType() != Material.OAK_SIGN && b.getType() != Material.OAK_WALL_SIGN)  continue;
                Sign signBlock = (Sign) b.getState();
                PersistentDataContainer pdc = signBlock.getPersistentDataContainer();
                if (!pdc.has(SignListener.TASK_SIGN_KEY, PersistentDataType.STRING))  continue;
                String subtaskName = pdc.get(SignListener.TASK_SIGN_KEY, PersistentDataType.STRING);
                if (subtaskName.startsWith("@"))  continue;
                Subtask subtask;
                try {
                    subtask = Subtask.valueOf(subtaskName);
                } catch (IllegalArgumentException e) {
                    messages.add("Found sign with invalid subtask name '%s'".formatted(subtaskName));
                    plugin.getLogger().warning("Found sign with invalid subtask name '%s'".formatted(subtaskName));
                    continue;
                }
                if (signToGraph.containsKey(subtask)) {
                    messages.add("Found multiple signs for subtask %s, ignoring all but the first one".formatted(subtask.name()));
                    plugin.getLogger().warning("Found multiple signs for subtask %s, ignoring all but the first one".formatted(subtask.name()));
                    continue;
                }
                signToGraph.put(subtask, vertex);
                generateSingleTargetShortestPath(vertex);
            }
        }
    }



}
