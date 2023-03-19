package io.github.greatericontop.greatimpostor.impostor;

public enum Sabotage {
    REACTOR("Reactor Meltdown"),
    OXYGEN("Oxygen Depleted"),
    LIGHTS("Fix Lights"),
    COMMUNICATIONS("Fix Communications"),

    ;

    private String displayName;

    Sabotage(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
