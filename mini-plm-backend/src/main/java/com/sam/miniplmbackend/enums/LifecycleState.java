package com.sam.miniplmbackend.enums;

/**
 * Lifecycle state for Parts (legacy package).
 */
public enum LifecycleState {
    IN_WORK,
    REVIEW,
    RELEASED,
    OBSOLETE;

    /**
     * Returns the next lifecycle state in the standard workflow.
     * IN_WORK -> REVIEW -> RELEASED -> OBSOLETE.
     */
    public LifecycleState getNextState() {
        return switch (this) {
            case IN_WORK -> REVIEW;
            case REVIEW -> RELEASED;
            case RELEASED, OBSOLETE -> OBSOLETE;
        };
    }
}
