package com.sam.mini_plm_backend.service;

import com.sam.mini_plm_backend.dto.CreatePartRequest;
import com.sam.mini_plm_backend.dto.PartResponse;
import com.sam.mini_plm_backend.dto.UpdatePartRequest;
import com.sam.mini_plm_backend.entity.Part;
import com.sam.mini_plm_backend.enums.LifecycleState;
import com.sam.mini_plm_backend.exception.BusinessException;
import com.sam.mini_plm_backend.exception.PartNotFoundException;
import com.sam.mini_plm_backend.repository.PartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class PartService {

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
        validatePartNumberFormat(req.getPartNumber());

        if (partRepository.existsByPartNumberAndIsDeletedFalse(req.getPartNumber())) {
            throw new BusinessException("Part number already exists: " + req.getPartNumber());
        }

        Part part = partMapper.toNewEntity(req, createdBy);
        Part saved = partRepository.save(part);
        return partMapper.toResponse(saved);
    }

    private void validatePartNumberFormat(String partNumber) {
        if (!StringUtils.hasText(partNumber)) {
            throw new BusinessException("Part number is required");
        }

        // Example: simple rule, you can tighten it later
        // e.g., ABC-1234
        String pattern = "^[A-Za-z0-9_-]{3,50}$";
        if (!partNumber.matches(pattern)) {
            throw new BusinessException("Invalid part number format");
        }
    }

    // =========================
    // READ
    // =========================

    public PartResponse getPartById(Long id) {
        Part part = partRepository.findById(id)
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .orElseThrow(() -> new PartNotFoundException(id));

        return partMapper.toResponse(part);
    }

    public Page<PartResponse> searchParts(String name, String partNumber, Pageable pageable) {
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

        return page.map(partMapper::toResponse);
    }

    public Page<PartResponse> getAllParts(String lifecycleState, Pageable pageable) {
        if (StringUtils.hasText(lifecycleState)) {
            LifecycleState state = LifecycleState.valueOf(lifecycleState);
            Page<Part> page = partRepository
                    .findByLifecycleStateAndIsDeletedFalse(state, pageable);

            return page.map(partMapper::toResponse);
        }

        return partRepository.findByIsDeletedFalse(pageable).map(partMapper::toResponse);
    }

    // =========================
    // UPDATE
    // =========================

    public PartResponse updatePart(Long id, UpdatePartRequest req, String modifiedBy) {
        Part existing = partRepository.findById(id)
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .orElseThrow(() -> new PartNotFoundException(id));

        if (existing.getLifecycleState() != LifecycleState.IN_WORK) {
            throw new BusinessException("Edit allowed only in IN_WORK state");
        }

        existing.setName(req.getName());
        existing.setDescription(req.getDescription());
        existing.setVersion(req.getVersion());
        existing.setLastModifiedBy(modifiedBy);
        existing.setLastModifiedDate(LocalDateTime.now());

        Part saved = partRepository.save(existing);
        return partMapper.toResponse(saved);
    }

    // =========================
    // DELETE (Soft delete)
    // =========================

    public void deletePart(Long id) {
        Part existing = partRepository.findById(id)
                .filter(p -> !Boolean.TRUE.equals(p.getIsDeleted()))
                .orElseThrow(() -> new PartNotFoundException(id));

        if (existing.getLifecycleState() != LifecycleState.IN_WORK) {
            throw new BusinessException("Delete allowed only in IN_WORK. Use Obsolete instead.");
        }

        existing.setIsDeleted(true);
        existing.setLastModifiedDate(LocalDateTime.now());
        partRepository.save(existing);
    }
}
