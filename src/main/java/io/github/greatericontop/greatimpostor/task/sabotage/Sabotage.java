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

public enum Sabotage {
    REACTOR("Reactor Meltdown", true, true) {
        @Override
        public SabotageSubtask[] getRequiredSubtasks(GreatImpostorMain plugin) {
            return new SabotageSubtask[]{ SabotageSubtask.REACTOR };
        }
    },

    OXYGEN("Oxygen Depleted", true, true) {
        @Override
        public SabotageSubtask[] getRequiredSubtasks(GreatImpostorMain plugin) {
            int totalCompletionState = plugin.sabotageOxygen.getTotalCompletionState();
            if (totalCompletionState == 0b00) {
                return new SabotageSubtask[]{ SabotageSubtask.OXYGEN_IN_OXYGEN, SabotageSubtask.OXYGEN_IN_ADMIN };
            }
            if (totalCompletionState == 0b01) { // oxygen done so we still need admin
                return new SabotageSubtask[]{ SabotageSubtask.OXYGEN_IN_ADMIN };
            }
            if (totalCompletionState == 0b10) {
                return new SabotageSubtask[]{ SabotageSubtask.OXYGEN_IN_OXYGEN };
            }
            return new SabotageSubtask[]{}; // should never happen
        }
    },

    LIGHTS("Fix Lights", false, false) {
        @Override
        public SabotageSubtask[] getRequiredSubtasks(GreatImpostorMain plugin) {
            return new SabotageSubtask[]{ SabotageSubtask.LIGHTS };
        }
    },

    COMMUNICATIONS("Fix Communications", false, true) {
        @Override
        public SabotageSubtask[] getRequiredSubtasks(GreatImpostorMain plugin) {
            return new SabotageSubtask[]{ SabotageSubtask.REACTOR };
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
     * Return the subtasks that still need to be completed.
     * Players will be hinted to go to these locations.
     */
    public SabotageSubtask[] getRequiredSubtasks(GreatImpostorMain plugin) {
        // Default implementation
        throw new RuntimeException("getRequiredSubtasks not implemented");
    }

}
