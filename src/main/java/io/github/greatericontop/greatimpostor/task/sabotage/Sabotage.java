package io.github.greatericontop.greatimpostor.task.sabotage;

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

import java.util.List;

public enum Sabotage {
    REACTOR("Reactor Meltdown", true, true) {
        @Override
        public double[][] getPOICoordinates(GreatImpostorMain plugin) {
            List<Double> coords = plugin.getConfig().getDoubleList("sabotage-fix-coordinates.reactor");
            return new double[][]{ {coords.get(0), coords.get(1)} };
        }
    },

    OXYGEN("Oxygen Depleted", true, true) {
        @Override
        public double[][] getPOICoordinates(GreatImpostorMain plugin) {
            List<Double> coordsOxygen = plugin.getConfig().getDoubleList("sabotage-fix-coordinates.oxygen-in-oxygen");
            List<Double> coordsAdmin = plugin.getConfig().getDoubleList("sabotage-fix-coordinates.oxygen-in-admin");
            int totalCompletionState = plugin.sabotageOxygen.getTotalCompletionState();
            if (totalCompletionState == 0b00) {
                return new double[][]{ {coordsOxygen.get(0), coordsOxygen.get(1)}, {coordsAdmin.get(0), coordsAdmin.get(1)} };
            }
            if (totalCompletionState == 0b01) { // oxygen done so we still need admin
                return new double[][]{ {coordsAdmin.get(0), coordsAdmin.get(1)} };
            }
            if (totalCompletionState == 0b10) {
                return new double[][]{ {coordsOxygen.get(0), coordsOxygen.get(1)} };
            }
            return new double[][]{}; // shouldn't happen
        }
    },

    LIGHTS("Fix Lights", false, false) {
        @Override
        public double[][] getPOICoordinates(GreatImpostorMain plugin) {
            return new double[][]{};
        }
    },

    COMMUNICATIONS("Fix Communications", false, true) {
        @Override
        public double[][] getPOICoordinates(GreatImpostorMain plugin) {
            return new double[][]{};
        }
    },

    ;

    private final String displayName;
    private final boolean isCritical;
    private final boolean disruptsGame;

    Sabotage(String displayName, boolean isCritical, boolean disruptsGame) {
        this.displayName = displayName;
        this.isCritical = isCritical;
        this.disruptsGame = disruptsGame;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public boolean disruptsGame() {
        return disruptsGame;
    }

    /*
     * Return coordinates of the places that the sabotage needs to be fixed at.
     * This is for particles leading the player to said coordinates.
     * Returns a double[] of [X, Z]
     */
    public double[][] getPOICoordinates(GreatImpostorMain plugin) {
        // Default implementation
        throw new RuntimeException("getPOICoordinates not implemented");
    }

}
