package com.sam.mini_plm_backend.service;

import com.sam.mini_plm_backend.dto.CreatePartRequest;
import com.sam.mini_plm_backend.dto.PartResponse;
import com.sam.mini_plm_backend.dto.UpdatePartRequest;
import com.sam.mini_plm_backend.entity.Part;
import com.sam.mini_plm_backend.enums.LifecycleState;
import com.sam.mini_plm_backend.exception.BusinessException;
import com.sam.mini_plm_backend.exception.PartNotFoundException;
import com.sam.mini_plm_backend.repository.PartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class PartService {

    private static final Logger logger = LoggerFactory.getLogger(PartService.class);

    private final PartRepository partRepository;
    private final PartMapper partMapper;

    public PartService(PartRepository partRepository, PartMapper partMapper) {
        this.partRepository = partRepository;
        this.partMapper = partMapper;
    }

    // =========================
    // CREATE
    // =========================

    public PartResponse createPart(CreatePartRequest req, String createdBy) {
        logger.info("Creating new part with number: {}", req.getPartNumber());
        
        validatePartNumberFormat(req.getPartNumber());

        if (partRepository.existsByPartNumberAndIsDeletedFalse(req.getPartNumber())) {
            logger.warn("Part creation failed - duplicate part number: {}", req.getPartNumber());
            throw new BusinessException("Part number already exists: " + req.getPartNumber());
        }

        Part part = partMapper.toNewEntity(req, createdBy);
        Part saved = partRepository.save(part);
        
        logger.info("Part created successfully with ID: {}", saved.getId());
        return partMapper.toResponse(saved);
    }

    private void validatePartNumberFormat(String partNumber) {
        if (!StringUtils.hasText(partNumber)) {
            logger.warn("Part number validation failed - empty part number");
            throw new BusinessException("Part number is required");
        }

        // Pattern: alphanumeric, hyphens, underscores, 3-50 chars
        String pattern = "^[A-Za-z0-9_-]{3,50}$";
        if (!partNumber.matches(pattern)) {
            logger.warn("Part number validation failed - invalid format: {}", partNumber);
            throw new BusinessException("Invalid part number format. Use alphanumeric, hyphens, or underscores (3-50 chars)");
        }
    }

    // =========================
    // READ
    // =========================

    public PartResponse getPartById(Long id) {
        logger.debug("Fetching part with ID: {}", id);
        
        Part part = partRepository.findById(id)
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .orElseThrow(() -> {
                    logger.warn("Part not found with ID: {}", id);
                    return new PartNotFoundException(id);
                });

        return partMapper.toResponse(part);
    }

    public Page<PartResponse> searchParts(String name, String partNumber, Pageable pageable) {
        logger.debug("Searching parts - name: {}, partNumber: {}", name, partNumber);
        
        boolean hasName = StringUtils.hasText(name);
        boolean hasPartNumber = StringUtils.hasText(partNumber);

        Page<Part> page;

        if (hasName && hasPartNumber) {
            page = partRepository
                    .findByNameContainingIgnoreCaseAndPartNumberContainingIgnoreCaseAndIsDeletedFalse(
                            name, partNumber, pageable
                    );
        } else if (hasName) {
            page = partRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name, pageable);
        } else if (hasPartNumber) {
            page = partRepository.findByPartNumberContainingIgnoreCaseAndIsDeletedFalse(partNumber, pageable);
        } else {
            page = partRepository.findByIsDeletedFalse(pageable);
        }

        logger.info("Search completed - found {} parts", page.getTotalElements());
        return page.map(partMapper::toResponse);
    }

    public Page<PartResponse> getAllParts(String lifecycleState, Pageable pageable) {
        logger.debug("Fetching all parts - lifecycleState: {}", lifecycleState);
        
        if (StringUtils.hasText(lifecycleState)) {
            try {
                LifecycleState state = LifecycleState.valueOf(lifecycleState);
                Page<Part> page = partRepository
                        .findByLifecycleStateAndIsDeletedFalse(state, pageable);
                logger.info("Fetched {} parts with state: {}", page.getTotalElements(), lifecycleState);
                return page.map(partMapper::toResponse);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid lifecycle state: {}", lifecycleState);
                throw new BusinessException("Invalid lifecycle state: " + lifecycleState);
            }
        }

        return partRepository.findByIsDeletedFalse(pageable).map(partMapper::toResponse);
    }

    // =========================
    // UPDATE (WITH NULL SAFETY)
    // =========================

    public PartResponse updatePart(Long id, UpdatePartRequest req, String modifiedBy) {
        logger.info("Updating part with ID: {}", id);
        
        Part existing = partRepository.findById(id)
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .orElseThrow(() -> {
                    logger.warn("Part not found for update - ID: {}", id);
                    return new PartNotFoundException(id);
                });

        if (existing.getLifecycleState() != LifecycleState.IN_WORK) {
            logger.warn("Part update failed - not in IN_WORK state. Current state: {}", existing.getLifecycleState());
            throw new BusinessException("Edit allowed only in IN_WORK state. Current state: " + existing.getLifecycleState());
        }

        // ✅ NULL-SAFE UPDATES - Only update if not null
        if (req.getName() != null && StringUtils.hasText(req.getName())) {
            logger.debug("Updating part name from '{}' to '{}'", existing.getName(), req.getName());
            existing.setName(req.getName());
        }
        
        if (req.getDescription() != null) {
            logger.debug("Updating part description");
            existing.setDescription(req.getDescription());
        }
        
        if (req.getVersion() != null && StringUtils.hasText(req.getVersion())) {
            logger.debug("Updating part version from '{}' to '{}'", existing.getVersion(), req.getVersion());
            existing.setVersion(req.getVersion());
        }
        
        existing.setLastModifiedBy(modifiedBy);
        existing.setLastModifiedDate(LocalDateTime.now());

        Part saved = partRepository.save(existing);
        logger.info("Part updated successfully - ID: {}", id);
        return partMapper.toResponse(saved);
    }

    // =========================
    // DELETE (Soft delete)
    // =========================

    public void deletePart(Long id) {
        logger.info("Deleting part with ID: {}", id);
        
        Part existing = partRepository.findById(id)
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .orElseThrow(() -> {
                    logger.warn("Part not found for deletion - ID: {}", id);
                    return new PartNotFoundException(id);
                });

        if (existing.getLifecycleState() != LifecycleState.IN_WORK) {
            logger.warn("Part deletion failed - not in IN_WORK state. Current state: {}", existing.getLifecycleState());
            throw new BusinessException(
                "Delete allowed only in IN_WORK state. Use Obsolete action instead. Current state: " + existing.getLifecycleState()
            );
        }

        existing.setIsDeleted(true);
        existing.setLastModifiedDate(LocalDateTime.now());
        partRepository.save(existing);
        logger.info("Part soft-deleted successfully - ID: {}", id);
    }

    // =========================
    // HELPER METHODS
    // =========================

    public Part getPartEntity(Long id) {
        logger.debug("Fetching part entity with ID: {}", id);
        return partRepository.findById(id)
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .orElseThrow(() -> {
                    logger.warn("Part entity not found - ID: {}", id);
                    return new PartNotFoundException(id);
                });
    }
}
