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

/**
 * Service for managing part lifecycle transitions
 * All methods are transactional to ensure data consistency
 */
@Service
public class LifecycleService {

    private static final Logger logger = LoggerFactory.getLogger(LifecycleService.class);

    private final PartRepository partRepository;
    private final StateTransitionHistoryRepository historyRepository;

    public LifecycleService(PartRepository partRepository, StateTransitionHistoryRepository historyRepository) {
        this.partRepository = partRepository;
        this.historyRepository = historyRepository;
    }

    /**
     * Promote part to next lifecycle state
     * Transactional: all operations succeed or all roll back
     */
    @Transactional
    public Part promote(Long partId, String transitionedBy) throws Exception {
        logger.info("Promoting part with ID: {}", partId);
        
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> {
                    logger.warn("Part not found for promotion - ID: {}", partId);
                    return new PartNotFoundException(partId);
                });

        LifecycleState currentState = part.getLifecycleState();
        
        // Validation
        if (currentState == LifecycleState.OBSOLETE) {
            logger.warn("Cannot promote part - already OBSOLETE. Part ID: {}", partId);
            throw new BusinessException("Cannot promote OBSOLETE parts");
        }

        if (currentState == LifecycleState.RELEASED) {
            logger.warn("Cannot promote part - already RELEASED. Part ID: {}", partId);
            throw new BusinessException("Part is already in RELEASED state");
        }

        LifecycleState newState = currentState.getNextState();
        logger.debug("Transitioning part from {} to {}", currentState, newState);

        // Update part
        part.setLifecycleState(newState);
        part.setLastModifiedBy(transitionedBy);
        part.setLastModifiedDate(LocalDateTime.now());
        Part savedPart = partRepository.save(part);
        logger.debug("Part state updated in database");

        // Create history record
        StateTransitionHistory history = StateTransitionHistory.builder()
                .part(part)
                .fromState(currentState)
                .toState(newState)
                .transitionDate(LocalDateTime.now())
                .transitionedBy(transitionedBy)
                .build();

        historyRepository.save(history);
        logger.info("Part promoted successfully from {} to {}. Part ID: {}", currentState, newState, partId);

        return savedPart;
    }

    /**
     * Revise part - creates a new revision
     * Transactional: all operations succeed or all roll back
     */
    @Transactional
    public Part revise(Long partId, String transitionedBy) throws Exception {
        logger.info("Revising part with ID: {}", partId);
        
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> {
                    logger.warn("Part not found for revision - ID: {}", partId);
                    return new PartNotFoundException(partId);
                });

        // Only RELEASED parts can be revised
        if (part.getLifecycleState() != LifecycleState.RELEASED) {
            logger.warn("Cannot revise part - not RELEASED. Current state: {}, Part ID: {}", 
                part.getLifecycleState(), partId);
            throw new BusinessException("Only RELEASED parts can be revised. Current state: " + part.getLifecycleState());
        }

        // Increment revision
        Integer oldRevision = part.getRevisionNumber();
        Integer newRevision = oldRevision + 1;
        logger.debug("Incrementing revision from {} to {}", oldRevision, newRevision);

        part.setRevisionNumber(newRevision);
        part.setRevisionSequence(newRevision + ".0");
        part.setLifecycleState(LifecycleState.IN_WORK); // Reset to IN_WORK for editing
        part.setLastModifiedBy(transitionedBy);
        part.setLastModifiedDate(LocalDateTime.now());
        Part savedPart = partRepository.save(part);
        logger.debug("Part revision updated in database");

        // Create history record
        StateTransitionHistory history = StateTransitionHistory.builder()
                .part(part)
                .fromState(LifecycleState.RELEASED)
                .toState(LifecycleState.IN_WORK)
                .transitionDate(LocalDateTime.now())
                .transitionedBy(transitionedBy)
                .build();

        historyRepository.save(history);
        logger.info("Part revised successfully - new revision: {}. Part ID: {}", newRevision, partId);

        return savedPart;
    }

    /**
     * Mark part as obsolete
     * Transactional: all operations succeed or all roll back
     */
    @Transactional
    public Part markObsolete(Long partId, String transitionedBy) throws Exception {
        logger.info("Marking part as obsolete - ID: {}", partId);
        
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> {
                    logger.warn("Part not found for obsolete marking - ID: {}", partId);
                    return new PartNotFoundException(partId);
                });

        LifecycleState currentState = part.getLifecycleState();
        
        // Only RELEASED parts can be marked obsolete
        if (currentState != LifecycleState.RELEASED) {
            logger.warn("Cannot mark obsolete - not RELEASED. Current state: {}, Part ID: {}", currentState, partId);
            throw new BusinessException("Only RELEASED parts can be marked obsolete. Current state: " + currentState);
        }

        logger.debug("Transitioning part to OBSOLETE state");

        part.setLifecycleState(LifecycleState.OBSOLETE);
        part.setObsoleteDate(LocalDateTime.now());
        part.setLastModifiedBy(transitionedBy);
        part.setLastModifiedDate(LocalDateTime.now());
        Part savedPart = partRepository.save(part);
        logger.debug("Part obsolete state updated in database");

        // Create history record
        StateTransitionHistory history = StateTransitionHistory.builder()
                .part(part)
                .fromState(currentState)
                .toState(LifecycleState.OBSOLETE)
                .transitionDate(LocalDateTime.now())
                .transitionedBy(transitionedBy)
                .build();

        historyRepository.save(history);
        logger.info("Part marked as obsolete successfully. Part ID: {}", partId);

        return savedPart;
    }
}
