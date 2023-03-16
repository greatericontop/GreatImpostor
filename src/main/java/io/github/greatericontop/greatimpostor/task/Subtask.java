package io.github.greatericontop.greatimpostor.task;

import org.bukkit.Material;

public enum Subtask {

    // To prevent a cyclic dependency between Subtask and Task this needs to be a string that gets put into a TaskType at runtime

    WIRING_ELECTRICAL("WIRING"),
    WIRING_STORAGE("WIRING"),
    WIRING_ADMIN("WIRING"),
    WIRING_NAVIGATION("WIRING"),
    WIRING_CAFETERIA("WIRING"),
    WIRING_SECURITY("WIRING"),

    REDIRECT_POWER("REDIRECT_ACCEPT_POWER"),

    ACCEPT_POWER_COMMUNICATIONS("REDIRECT_ACCEPT_POWER"),
    ACCEPT_POWER_LOWER_ENGINE("REDIRECT_ACCEPT_POWER"),
    ACCEPT_POWER_UPPER_ENGINE("REDIRECT_ACCEPT_POWER"),
    ACCEPT_POWER_NAVIGATION("REDIRECT_ACCEPT_POWER"),
    ACCEPT_POWER_OXYGEN("REDIRECT_ACCEPT_POWER"),
    ACCEPT_POWER_SECURITY("REDIRECT_ACCEPT_POWER"),
    ACCEPT_POWER_SHIELDS("REDIRECT_ACCEPT_POWER"),
    ACCEPT_POWER_WEAPONS("REDIRECT_ACCEPT_POWER"),

    DOWNLOAD_DATA_CAFETERIA("DOWNLOAD_UPLOAD_DATA"),
    DOWNLOAD_DATA_COMMUNICATIONS("DOWNLOAD_UPLOAD_DATA"),
    DOWNLOAD_DATA_ELECTRICAL("DOWNLOAD_UPLOAD_DATA"),
    DOWNLOAD_DATA_NAVIGATION("DOWNLOAD_UPLOAD_DATA"),
    DOWNLOAD_DATA_WEAPONS("DOWNLOAD_UPLOAD_DATA"),

    UPLOAD_DATA("DOWNLOAD_UPLOAD_DATA"),

    ;

    private final String fullTask;
    private final String displayName;

    public TaskType getFullTask() {
        return TaskType.valueOf(fullTask);
    }
    public String getDisplayName() {
        return displayName;
    }

    Subtask(String fullTask) {
        this.displayName = this.name(); // TODO: actually get nice names
        this.fullTask = fullTask;
    }

    public Material getDisplayMaterial(int numberCompleted) {
        int total = getFullTask().getRequiredSubtaskCount();
        if (numberCompleted == total) {
            return Material.LIME_STAINED_GLASS;
        } else if (numberCompleted == 0) {
            return Material.RED_STAINED_GLASS;
        } else {
            return Material.YELLOW_STAINED_GLASS;
        }
    }

}
