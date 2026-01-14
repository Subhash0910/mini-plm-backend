package com.sam.mini_plm_backend.service;

import com.sam.mini_plm_backend.Model.Part;
import com.sam.mini_plm_backend.Model.StateTransitionHistory;
import com.sam.mini_plm_backend.enums.LifecycleState;
import com.sam.mini_plm_backend.repository.PartRepository;
import com.sam.mini_plm_backend.repository.StateTransitionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LifecycleService {

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private StateTransitionHistoryRepository historyRepository;

    public Part promote(Long partId, String transitionedBy) throws Exception {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new Exception("Part not found: " + partId));

        LifecycleState currentState = part.getLifecycleState();
        if (currentState == null) currentState = LifecycleState.IN_WORK;

        LifecycleState nextState = currentState.getNextState();
        if (nextState == currentState) {
            throw new Exception("Cannot promote from " + currentState);
        }

        recordStateTransition(part, currentState, nextState, transitionedBy, "Promoted to next state");

        part.setLifecycleState(nextState);
        part.setLastModifiedBy(transitionedBy);
        part.setLastModifiedDate(LocalDateTime.now());

        if (nextState == LifecycleState.RELEASED) {
            part.setReleasedDate(LocalDateTime.now());
        }

        return partRepository.save(part);
    }

    // Revise creates a NEW row (new revision) for same partNumber
    public Part revise(Long partId, String transitionedBy) throws Exception {
        Part oldPart = partRepository.findById(partId)
                .orElseThrow(() -> new Exception("Part not found: " + partId));

        if (oldPart.getLifecycleState() == LifecycleState.RELEASED) {
            throw new Exception("Cannot revise a RELEASED part directly. Use Change (ECR/ECO).");
        }
        if (oldPart.getLifecycleState() == LifecycleState.OBSOLETE) {
            throw new Exception("Cannot revise an OBSOLETE part.");
        }

        // latest-only revise
        List<Part> versions = partRepository.findByPartNumberOrderByRevisionNumberDesc(oldPart.getPartNumber());
        if (versions == null || versions.isEmpty()) {
            throw new Exception("No revisions found for partNumber: " + oldPart.getPartNumber());
        }

        Part latest = versions.get(0);
        if (!latest.getId().equals(oldPart.getId())) {
            throw new Exception("Revise is allowed only on the latest revision: " + latest.getRevisionSequence());
        }

        Integer latestRevNum = latest.getRevisionNumber() != null ? latest.getRevisionNumber() : 1;
        int newRevNum = latestRevNum + 1;

        Part newPart = new Part();
        newPart.setPartNumber(latest.getPartNumber());
        newPart.setName(latest.getName());
        newPart.setDescription(latest.getDescription());
        newPart.setVersion(latest.getVersion());

        newPart.setRevisionNumber(newRevNum);
        newPart.setRevisionLetter(null);
        newPart.setRevisionSequence(newRevNum + ".0");

        newPart.setLifecycleState(LifecycleState.IN_WORK);
        newPart.setCreatedBy(transitionedBy);
        newPart.setLastModifiedBy(transitionedBy);
        newPart.setCreatedDate(LocalDateTime.now());
        newPart.setLastModifiedDate(LocalDateTime.now());

        Part saved = partRepository.save(newPart);

        recordStateTransition(
                latest,
                latest.getLifecycleState(),
                latest.getLifecycleState(),
                transitionedBy,
                "Revised to v" + saved.getRevisionSequence()
        );

        return saved;
    }

    public Part markObsolete(Long partId, String transitionedBy) throws Exception {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new Exception("Part not found: " + partId));

        LifecycleState oldState = part.getLifecycleState();

        recordStateTransition(part, oldState, LifecycleState.OBSOLETE, transitionedBy, "Marked as obsolete");

        part.setLifecycleState(LifecycleState.OBSOLETE);
        part.setObsoleteDate(LocalDateTime.now());
        part.setLastModifiedBy(transitionedBy);
        part.setLastModifiedDate(LocalDateTime.now());

        return partRepository.save(part);
    }

    private void recordStateTransition(
            Part part,
            LifecycleState fromState,
            LifecycleState toState,
            String transitionedBy,
            String reason
    ) {
        StateTransitionHistory history = new StateTransitionHistory(
                part, fromState, toState, transitionedBy, reason
        );
        historyRepository.save(history);
    }
}
