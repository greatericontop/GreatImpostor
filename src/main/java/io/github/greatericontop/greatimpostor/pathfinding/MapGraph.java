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
import org.bukkit.World;

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

    private final Map<XYZ, List<XYZ>> adj = new HashMap<>();
    // XYZ target -> parent table
    private final Map<XYZ, Map<XYZ, XYZ>> shortestPathsCache = new HashMap<>();

    private final GreatImpostorMain plugin;
    public MapGraph(GreatImpostorMain plugin) {
        this.plugin = plugin;
    }



    public void generate(World world, XYZ root) {
        if (!adj.isEmpty()) {
            throw new IllegalStateException("Graph has already been generated");
        }
        if (!root.isEmpty(world)) {
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

        plugin.getLogger().info("Generated map graph with %d vertices!".formatted(adj.size()));
    }

    public void generateSingleTargetShortestPath(XYZ root) {
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




}
