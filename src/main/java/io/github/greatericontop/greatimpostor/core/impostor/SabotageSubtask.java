package io.github.greatericontop.greatimpostor.core.impostor;

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

    private Sabotage fullSabotage;
    private int magicNumber;

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
