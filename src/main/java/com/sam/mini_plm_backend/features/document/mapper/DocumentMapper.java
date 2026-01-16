package com.sam.mini_plm_backend.features.document.mapper;

import com.sam.mini_plm_backend.features.document.dto.DocumentCreateDTO;
import com.sam.mini_plm_backend.features.document.dto.DocumentResponseDTO;
import com.sam.mini_plm_backend.features.document.entity.Document;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Document Entity and DTOs
 * Handles entity-DTO conversions
 */
@Component
public class DocumentMapper {

    public DocumentResponseDTO toResponseDTO(Document document) {
        if (document == null) {
            return null;
        }
        return DocumentResponseDTO.builder()
                .id(document.getId())
                .documentNumber(document.getDocumentNumber())
                .title(document.getTitle())
                .description(document.getDescription())
                .documentType(document.getDocumentType())
                .state(document.getState())
                .revision(document.getRevision())
                .createdBy(document.getCreatedBy())
                .createdDate(document.getCreatedDate())
                .modifiedBy(document.getModifiedBy())
                .modifiedDate(document.getModifiedDate())
                .build();
    }

    public Document toEntity(DocumentCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        return Document.builder()
                .documentNumber(dto.getDocumentNumber())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .documentType(dto.getDocumentType())
                .revision(dto.getRevision())
                .build();
    }
}
