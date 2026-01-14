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
        if (this == IN_WORK) return PROTOTYPE_IN_WORK;
        if (this == PROTOTYPE_IN_WORK) return PROTOTYPE;
        if (this == PROTOTYPE) return RELEASED;
        return this; // RELEASED and OBSOLETE don't auto-promote
    }

    public boolean isEditable() {
        return this == IN_WORK || this == PROTOTYPE_IN_WORK || this == PROTOTYPE;
    }

    public boolean isUsableInBOM() {
        return this == RELEASED;
    }

    public enum Role {
        ADMIN,      // Full access
        ENGINEER,   // Can create, edit, manage parts and changes
        VIEWER      // Read-only access
    }
}
