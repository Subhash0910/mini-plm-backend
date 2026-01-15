package com.sam.mini_plm_backend.enums;

public enum LifecycleState {
    IN_WORK("In Work", 1),
    PROTOTYPE_IN_WORK("Prototype In Work", 2),
    PROTOTYPE("Prototype", 3),
    RELEASED("Released", 4),
    OBSOLETE("Obsolete", 5);

    private final String displayName;
    private final int sequence;

    LifecycleState(String displayName, int sequence) {
        this.displayName = displayName;
        this.sequence = sequence;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSequence() {
        return sequence;
    }

    public LifecycleState getNextState() {
        return switch (this) {
            case IN_WORK -> PROTOTYPE_IN_WORK;
            case PROTOTYPE_IN_WORK -> PROTOTYPE;
            case PROTOTYPE -> RELEASED;
            default -> this;
        };
    }

    public boolean isEditable() {
        return this == IN_WORK || this == PROTOTYPE_IN_WORK || this == PROTOTYPE;
    }

    public boolean isUsableInBOM() {
        return this == RELEASED;
    }

    public boolean isFinal() {
        return this == OBSOLETE || this == RELEASED;
    }
}
