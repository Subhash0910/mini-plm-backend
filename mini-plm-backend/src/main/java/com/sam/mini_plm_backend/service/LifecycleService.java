package com.sam.mini_plm_backend.service;

import com.sam.mini_plm_backend.entity.Part;
import com.sam.mini_plm_backend.entity.StateTransitionHistory;
import com.sam.mini_plm_backend.enums.LifecycleState;
import com.sam.mini_plm_backend.exception.BusinessException;
import com.sam.mini_plm_backend.exception.PartNotFoundException;
import com.sam.mini_plm_backend.repository.PartRepository;
import com.sam.mini_plm_backend.repository.StateTransitionHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LifecycleService {

    private static final Logger logger = LoggerFactory.getLogger(LifecycleService.class);

    private final PartRepository partRepository;
    private final StateTransitionHistoryRepository historyRepository;

    public LifecycleService(PartRepository partRepository, StateTransitionHistoryRepository historyRepository) {
        this.partRepository = partRepository;
        this.historyRepository = historyRepository;
    }

    public Part promote(Long partId, String transitionedBy) {
        logger.info("Promoting part {} by user {}", partId, transitionedBy);

        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new PartNotFoundException(partId));

        LifecycleState currentState = part.getLifecycleState();
        if (currentState == null) currentState = LifecycleState.IN_WORK;

        LifecycleState nextState = currentState.getNextState();
        if (nextState == currentState) {
            logger.warn("Cannot promote part {} from state {}", partId, currentState);
            throw new BusinessException("Cannot promote from " + currentState);
        }

        recordStateTransition(part, currentState, nextState, transitionedBy, "Promoted to next state");

        part.setLifecycleState(nextState);
        part.setLastModifiedBy(transitionedBy);
        part.setLastModifiedDate(LocalDateTime.now());

        if (nextState == LifecycleState.RELEASED) {
            part.setReleasedDate(LocalDateTime.now());
            logger.info("Part {} released at {}", partId, part.getReleasedDate());
        }

        Part saved = partRepository.save(part);
        logger.info("Part {} promoted to state {}", partId, nextState);
        return saved;
    }

    public Part revise(Long partId, String transitionedBy) {
        logger.info("Creating revision for part {} by user {}", partId, transitionedBy);

        Part oldPart = partRepository.findById(partId)
                .orElseThrow(() -> new PartNotFoundException(partId));

        if (oldPart.getLifecycleState() == LifecycleState.RELEASED) {
            logger.warn("Cannot revise RELEASED part {}", partId);
            throw new BusinessException("Cannot revise a RELEASED part directly. Use Change (ECR/ECO).");
        }
        if (oldPart.getLifecycleState() == LifecycleState.OBSOLETE) {
            logger.warn("Cannot revise OBSOLETE part {}", partId);
            throw new BusinessException("Cannot revise an OBSOLETE part.");
        }

        List<Part> versions = partRepository.findByPartNumberOrderByRevisionNumberDesc(oldPart.getPartNumber());
        if (versions == null || versions.isEmpty()) {
            logger.error("No revisions found for partNumber: {}", oldPart.getPartNumber());
            throw new BusinessException("No revisions found for partNumber: " + oldPart.getPartNumber());
        }

        Part latest = versions.get(0);
        if (!latest.getId().equals(oldPart.getId())) {
            logger.warn("Revise allowed only on latest revision. Latest: {}, Requested: {}",
                    latest.getRevisionSequence(), oldPart.getRevisionSequence());
            throw new BusinessException("Revise is allowed only on the latest revision: " + latest.getRevisionSequence());
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

        logger.info("New revision created for part {} - Revision: {}",
                oldPart.getPartNumber(), saved.getRevisionSequence());
        return saved;
    }

    public Part markObsolete(Long partId, String transitionedBy) {
        logger.info("Marking part {} as obsolete by user {}", partId, transitionedBy);

        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new PartNotFoundException(partId));

        LifecycleState oldState = part.getLifecycleState();

        recordStateTransition(part, oldState, LifecycleState.OBSOLETE, transitionedBy, "Marked as obsolete");

        part.setLifecycleState(LifecycleState.OBSOLETE);
        part.setObsoleteDate(LocalDateTime.now());
        part.setLastModifiedBy(transitionedBy);
        part.setLastModifiedDate(LocalDateTime.now());

        Part saved = partRepository.save(part);
        logger.info("Part {} marked as obsolete", partId);
        return saved;
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
        logger.debug("State transition recorded for part {} from {} to {}",
                part.getId(), fromState, toState);
    }
}
