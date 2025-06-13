package io.github.greatericontop.greatimpostor.task;

public enum TaskType {

    WIRING(true, // frequent
            3,
            Subtask.WIRING_ELECTRICAL,
            Subtask.WIRING_STORAGE,
            Subtask.WIRING_ADMIN,
            Subtask.WIRING_NAVIGATION,
            Subtask.WIRING_CAFETERIA,
            Subtask.WIRING_SECURITY
    ),

    REDIRECT_ACCEPT_POWER(true, // frequent
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

    DOWNLOAD_UPLOAD_DATA(true, // frequent
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

    FUEL_ENGINES(
            4,
            Subtask.FUEL_ENGINES_UPPER,
            Subtask.FUEL_ENGINES_LOWER
    ) {
        @Override
        public Subtask[] getPossibleNextSubtasks(int numberSubtasksAlreadyCompleted) {
            if (numberSubtasksAlreadyCompleted == 0) {
                return new Subtask[] {Subtask.FETCH_FUEL};
            } else if (numberSubtasksAlreadyCompleted == 1) {
                return new Subtask[] {Subtask.FUEL_ENGINES_LOWER};
            } else if (numberSubtasksAlreadyCompleted == 2) {
                return new Subtask[] {Subtask.FETCH_FUEL};
            } else {
                return new Subtask[] {Subtask.FUEL_ENGINES_UPPER};
            }
        }
        @Override
        public boolean doAlreadyCompletedCheck() {
            return false;
        }
    },


    SWIPE_CARD(
            1,
            Subtask.SWIPE_CARD
    ),

    ADJUST_STEERING(
            1,
            Subtask.ADJUST_STEERING
    ),

    CLEAN_OXYGEN_FILTER(
            1,
            Subtask.CLEAN_OXYGEN_FILTER
    ),

    CLEAR_ASTEROIDS(
            1,
            Subtask.CLEAR_ASTEROIDS
    ),

    EMPTY_TRASH(
            2,
            Subtask.EMPTY_TRASH_CAFETERIA,
            Subtask.EMPTY_TRASH_STORAGE
    ),

    STABILIZE_NAVIGATION(
            1,
            Subtask.STABILIZE_NAVIGATION
    ),

    START_REACTOR(
            1,
            Subtask.START_REACTOR
    ),

    UNLOCK_MANIFOLDS(
            1,
            Subtask.UNLOCK_MANIFOLDS
    ),

    SUBMIT_SCAN(
            1,
            Subtask.SUBMIT_SCAN
    )

    ;

    private final int requiredSubtaskCount;
    private final Subtask[] subtasks;
    private final boolean isFrequent;

    public int getRequiredSubtaskCount() {
        return requiredSubtaskCount;
    }
    public Subtask[] getSubtasks() {
        return subtasks;
    }
    public boolean isFrequent() {
        return isFrequent;
    }

    TaskType(int requiredSubtaskCount, Subtask... subtasks) {
        this.requiredSubtaskCount = requiredSubtaskCount;
        this.subtasks = subtasks;
        this.isFrequent = false;
    }
    TaskType(boolean isFrequent, int requiredSubtaskCount, Subtask... subtasks) {
        this.requiredSubtaskCount = requiredSubtaskCount;
        this.subtasks = subtasks;
        this.isFrequent = isFrequent;
    }

    public Subtask[] getPossibleNextSubtasks(int numberSubtasksAlreadyCompleted) {
        return subtasks; // Default implementation - some tasks require specific subtasks
    }

    public boolean doAlreadyCompletedCheck() {
        return true; // Default implementation - some tasks have repeating, pre-determined steps
    }

}
