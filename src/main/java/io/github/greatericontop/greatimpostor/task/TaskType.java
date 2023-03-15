package io.github.greatericontop.greatimpostor.task;

public enum TaskType {

    WIRING(
            3,
            Subtask.WIRING_ELECTRICAL,
            Subtask.WIRING_STORAGE,
            Subtask.WIRING_ADMIN,
            Subtask.WIRING_NAVIGATION,
            Subtask.WIRING_CAFETERIA,
            Subtask.WIRING_SECURITY
    ),

    REDIRECT_ACCEPT_POWER(
            2,
            Subtask.REDIRECT_POWER,
            Subtask.ACCEPT_POWER_COMMUNICATIONS,
            Subtask.ACCEPT_POWER_LOWER_ENGINE,
            Subtask.ACCEPT_POWER_UPPER_ENGINE,
            Subtask.ACCEPT_POWER_NAVIGATION,
            Subtask.ACCEPT_POWER_OXYGEN,
            Subtask.ACCEPT_POWER_SECURITY,
            Subtask.ACCEPT_POWER_SHIELDS,
            Subtask.ACCEPT_POWER_WEAPONS
    ) {
        @Override
        public Subtask[] getPossibleNextSubtasks(int numberSubtasksAlreadyCompleted) {
            if (numberSubtasksAlreadyCompleted == 0) {
                return new Subtask[] {Subtask.REDIRECT_POWER};
            } else {
                return new Subtask[] {
                        Subtask.ACCEPT_POWER_COMMUNICATIONS,
                        Subtask.ACCEPT_POWER_LOWER_ENGINE,
                        Subtask.ACCEPT_POWER_UPPER_ENGINE,
                        Subtask.ACCEPT_POWER_NAVIGATION,
                        Subtask.ACCEPT_POWER_OXYGEN,
                        Subtask.ACCEPT_POWER_SECURITY,
                        Subtask.ACCEPT_POWER_SHIELDS,
                        Subtask.ACCEPT_POWER_WEAPONS
                };
            }
        }
    },

    DOWNLOAD_UPLOAD_DATA(
            2,
            Subtask.DOWNLOAD_DATA_CAFETERIA,
            Subtask.DOWNLOAD_DATA_COMMUNICATIONS,
            Subtask.DOWNLOAD_DATA_ELECTRICAL,
            Subtask.DOWNLOAD_DATA_NAVIGATION,
            Subtask.DOWNLOAD_DATA_WEAPONS,
            Subtask.UPLOAD_DATA
    ) {
        @Override
        public Subtask[] getPossibleNextSubtasks(int numberSubtasksAlreadyCompleted) {
            if (numberSubtasksAlreadyCompleted == 0) {
                return new Subtask[] {
                        Subtask.DOWNLOAD_DATA_CAFETERIA,
                        Subtask.DOWNLOAD_DATA_COMMUNICATIONS,
                        Subtask.DOWNLOAD_DATA_ELECTRICAL,
                        Subtask.DOWNLOAD_DATA_NAVIGATION,
                        Subtask.DOWNLOAD_DATA_WEAPONS
                };
            } else {
                return new Subtask[] {Subtask.UPLOAD_DATA};
            }
        }
    },

    ;

    private final int totalSubtaskCount;
    private final Subtask[] subtasks;

    public int getTotalSubtaskCount() {
        return totalSubtaskCount;
    }
    public Subtask[] getSubtasks() {
        return subtasks;
    }

    TaskType(int totalSubtaskCount, Subtask... subtasks) {
        this.totalSubtaskCount = totalSubtaskCount;
        this.subtasks = subtasks;
    }

    public Subtask[] getPossibleNextSubtasks(int numberSubtasksAlreadyCompleted) {
        return subtasks; // Default implementation - some tasks require specific subtasks
    }

}
