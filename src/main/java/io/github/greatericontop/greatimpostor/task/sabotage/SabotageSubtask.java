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

public enum SabotageSubtask {

    /*
     * This is mainly for the purpose of the Oxygen Depleted sabotage.
     * We get the real sabotage involved, and also pass this to the handler.
     *   The handler itself checks if both sabotage subtasks are completed.
     * There's no back-and-forth needed that :Subtask: and :TaskType: use.
     */

    REACTOR(Sabotage.REACTOR, -1),

    OXYGEN_IN_OXYGEN(Sabotage.OXYGEN, 0), // these are used as a bitmask to check if both are completed
    OXYGEN_IN_ADMIN(Sabotage.OXYGEN, 1),

    LIGHTS(Sabotage.LIGHTS, -1),

    COMMUNICATIONS(Sabotage.COMMUNICATIONS, -1),

    ;

    private final Sabotage fullSabotage;
    private final int magicNumber;

    public Sabotage getFullSabotage() {
        return fullSabotage;
    }
    public int getMagicNumber() {
        return magicNumber;
    }

    SabotageSubtask(Sabotage fullSabotage, int magicNumber) {
        this.fullSabotage = fullSabotage;
        this.magicNumber = magicNumber;
    }

}
