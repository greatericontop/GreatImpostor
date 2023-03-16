package io.github.greatericontop.greatimpostor.task;

import io.github.greatericontop.greatimpostor.GreatImpostorMain;

public class TaskUtil {

    public static TaskType[] INITIAL_TASKS = {
            // TODO: isn't INITIAL_TASKS just all of them? is this even necessary?
            TaskType.WIRING,
            TaskType.REDIRECT_ACCEPT_POWER,
            TaskType.DOWNLOAD_UPLOAD_DATA,
            TaskType.FUEL_ENGINES,
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
            default -> throw new IllegalArgumentException("Unknown/unrecognized task type: " + subtask.getFullTask());
        }
    }

}
