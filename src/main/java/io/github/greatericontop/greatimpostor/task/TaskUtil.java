package io.github.greatericontop.greatimpostor.task;

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
import io.github.greatericontop.greatimpostor.task.sabotage.Sabotage;
import io.github.greatericontop.greatimpostor.task.sabotage.BaseSabotageTask;

public class TaskUtil {

    public static TaskType[] INITIAL_TASKS = {
            // TODO: isn't INITIAL_TASKS just all of them? is this even necessary?
            //       use this for weights? but then you can't shuffle
            TaskType.WIRING,
            TaskType.REDIRECT_ACCEPT_POWER,
            TaskType.DOWNLOAD_UPLOAD_DATA,
            TaskType.FUEL_ENGINES,
            TaskType.SWIPE_CARD,
            TaskType.ADJUST_STEERING,
            TaskType.CLEAN_OXYGEN_FILTER,
            TaskType.CLEAR_ASTEROIDS,
            TaskType.EMPTY_TRASH,
            TaskType.STABILIZE_NAVIGATION,
            TaskType.START_REACTOR,
            TaskType.UNLOCK_MANIFOLDS,
            TaskType.SUBMIT_SCAN,
            TaskType.PRIME_SHIELDS,
            TaskType.ANALYZE_SAMPLE,
    };

    public static BaseTask getTaskClass(GreatImpostorMain plugin, Subtask subtask) {
        switch (subtask.getFullTask()) {
            case WIRING -> {
                return plugin.taskWiring;
            }
            case REDIRECT_ACCEPT_POWER -> {
                if (subtask == Subtask.REDIRECT_POWER) {
                    return plugin.taskRedirectPower;
                } else {
                    return plugin.taskAcceptPower;
                }
            }
            case DOWNLOAD_UPLOAD_DATA -> {
                if (subtask == Subtask.UPLOAD_DATA) {
                    return plugin.taskUploadData;
                } else {
                    return plugin.taskDownloadData;
                }
            }
            case FUEL_ENGINES -> {
                if (subtask == Subtask.FETCH_FUEL) {
                    return plugin.taskFetchFuel;
                } else {
                    return plugin.taskFuelEngines;
                }
            }
            case SWIPE_CARD -> {
                return plugin.taskSwipeCard;
            }
            case ADJUST_STEERING -> {
                return plugin.taskAdjustSteering;
            }
            case CLEAN_OXYGEN_FILTER -> {
                return plugin.taskCleanOxygenFilter;
            }
            case CLEAR_ASTEROIDS -> {
                return plugin.taskClearAsteroids;
            }
            case EMPTY_TRASH -> {
                return plugin.taskEmptyTrash;
            }
            case STABILIZE_NAVIGATION -> {
                return plugin.taskStabilizeNavigation;
            }
            case START_REACTOR -> {
                return plugin.taskStartReactor;
            }
            case UNLOCK_MANIFOLDS -> {
                return plugin.taskUnlockManifolds;
            }
            case SUBMIT_SCAN -> {
                return plugin.taskSubmitScan;
            }
            case PRIME_SHIELDS -> {
                return plugin.taskPrimeShields;
            }
            case ANALYZE_SAMPLE -> {
                return plugin.taskAnalyzeSample;
            }
            default -> throw new IllegalArgumentException("Unknown/unrecognized task type: " + subtask.getFullTask());
        }
    }

    public static BaseSabotageTask getSabotageTaskClass(GreatImpostorMain plugin, Sabotage sabotage) {
        switch (sabotage) {
            case REACTOR -> {
                return plugin.sabotageReactor;
            }
            case OXYGEN -> {
                return plugin.sabotageOxygen;
            }
            case LIGHTS -> {
                return plugin.sabotageFixLights;
            }
            case COMMUNICATIONS -> {
                return plugin.sabotageCommunications;
            }
            default -> throw new IllegalArgumentException("Unknown/unrecognized sabotage task type: " + sabotage);
        }
    }

}
