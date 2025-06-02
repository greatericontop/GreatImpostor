package io.github.greatericontop.greatimpostor.core.impostor;

public enum Sabotage {
    REACTOR("Reactor Meltdown", true, true),
    OXYGEN("Oxygen Depleted", true, true),
    LIGHTS("Fix Lights", false, false),
    COMMUNICATIONS("Fix Communications", false, true),

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

}
