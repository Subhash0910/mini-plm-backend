package com.sam.mini_plm_backend.entity;


import com.sam.mini_plm_backend.enums.LifecycleState;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "state_transition_history")
public class StateTransitionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LifecycleState fromState;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LifecycleState toState;

    @Column(nullable = false)
    private LocalDateTime transitionDate = LocalDateTime.now();

    @Column
    private String transitionedBy;

    @Column
    private String reason;

    // Constructors
    public StateTransitionHistory() {}

    public StateTransitionHistory(Part part, LifecycleState fromState, LifecycleState toState, String transitionedBy, String reason) {
        this.part = part;
        this.fromState = fromState;
        this.toState = toState;
        this.transitionedBy = transitionedBy;
        this.reason = reason;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Part getPart() { return part; }
    public void setPart(Part part) { this.part = part; }

    public LifecycleState getFromState() { return fromState; }
    public void setFromState(LifecycleState fromState) { this.fromState = fromState; }

    public LifecycleState getToState() { return toState; }
    public void setToState(LifecycleState toState) { this.toState = toState; }

    public LocalDateTime getTransitionDate() { return transitionDate; }
    public void setTransitionDate(LocalDateTime transitionDate) { this.transitionDate = transitionDate; }

    public String getTransitionedBy() { return transitionedBy; }
    public void setTransitionedBy(String transitionedBy) { this.transitionedBy = transitionedBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

