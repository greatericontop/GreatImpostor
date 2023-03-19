package io.github.greatericontop.greatimpostor.impostor;

public enum Sabotage {
    REACTOR("Reactor Meltdown", true),
    OXYGEN("Oxygen Depleted", true),
    LIGHTS("Fix Lights", false),
    COMMUNICATIONS("Fix Communications", false),

    ;

    private String displayName;
    private boolean isCritical;

    Sabotage(String displayName, boolean isCritical) {
        this.displayName = displayName;
        this.isCritical = isCritical;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCritical() {
        return isCritical;
    }

}
