package com.sam.mini_plm_backend.service;

import com.sam.mini_plm_backend.dto.CreatePartRequest;
import com.sam.mini_plm_backend.dto.LifecycleTransitionResponse;
import com.sam.mini_plm_backend.dto.PartResponse;
import com.sam.mini_plm_backend.entity.Part;
import com.sam.mini_plm_backend.enums.LifecycleState;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PartMapper {

    public Part toNewEntity(CreatePartRequest req, String createdBy) {
        LocalDateTime now = LocalDateTime.now();

        Part part = new Part();
        part.setPartNumber(req.getPartNumber());
        part.setName(req.getName());
        part.setDescription(req.getDescription());
        part.setVersion(req.getVersion());

        // lifecycle defaults
        part.setLifecycleState(LifecycleState.IN_WORK);
        part.setRevisionNumber(1);
        part.setRevisionSequence("1.0");

        part.setCreatedBy(createdBy);
        part.setLastModifiedBy(createdBy);
        part.setCreatedDate(now);
        part.setLastModifiedDate(now);
        part.setIsDeleted(false);
        part.setIsAssembly(false);

        return part;
    }

    public PartResponse toResponse(Part part) {
        if (part == null) return null;

        return PartResponse.builder()
                .id(part.getId())
                .partNumber(part.getPartNumber())
                .name(part.getName())
                .description(part.getDescription())
                .version(part.getVersion())
                .lifecycleState(part.getLifecycleState() != null
                        ? part.getLifecycleState().name()
                        : null)
                .revisionNumber(part.getRevisionNumber())
                .revisionSequence(part.getRevisionSequence())
                .createdDate(part.getCreatedDate())
                .lastModifiedDate(part.getLastModifiedDate())
                .releasedDate(part.getReleasedDate())
                .obsoleteDate(part.getObsoleteDate())
                .createdBy(part.getCreatedBy())
                .lastModifiedBy(part.getLastModifiedBy())
                .isAssembly(part.getIsAssembly())
                .build();
    }
    public LifecycleTransitionResponse toTransitionResponse(Part part) {
        if (part == null) {
            return null;
        }
        return LifecycleTransitionResponse.builder()
                .id(part.getId())
                .partNumber(part.getPartNumber())
                .name(part.getName())
                .lifecycleState(part.getLifecycleState())
                .revisionNumber(part.getRevisionNumber())
                .revisionSequence(part.getRevisionSequence())
                .lastModifiedDate(part.getLastModifiedDate())
                .lastModifiedBy(part.getLastModifiedBy())
                .releasedDate(part.getReleasedDate())
                .obsoleteDate(part.getObsoleteDate())
                .build();
    }

}
