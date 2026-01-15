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

    /**
     * Get the next state in the lifecycle progression
     * IN_WORK -> PROTOTYPE_IN_WORK -> PROTOTYPE -> RELEASED
     */
    public LifecycleState getNextState() {
        if (this == IN_WORK) return PROTOTYPE_IN_WORK;
        if (this == PROTOTYPE_IN_WORK) return PROTOTYPE;
        if (this == PROTOTYPE) return RELEASED;
        return this; // RELEASED and OBSOLETE don't auto-promote
    }

    /**
     * Check if this state allows editing
     */
    public boolean isEditable() {
        return this == IN_WORK || this == PROTOTYPE_IN_WORK || this == PROTOTYPE;
    }

    /**
     * Check if this part can be used in BOM (Bill of Materials)
     * Only RELEASED parts can be used
     */
    public boolean isUsableInBOM() {
        return this == RELEASED;
    }

    /**
     * Check if this state is a final state
     */
    public boolean isFinal() {
        return this == RELEASED || this == OBSOLETE;
    }
}
