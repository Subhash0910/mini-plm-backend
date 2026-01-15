package com.sam.mini_plm_backend.service;

import com.sam.mini_plm_backend.dto.*;
import com.sam.mini_plm_backend.entity.*;
import com.sam.mini_plm_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BomService {

    private final BOMRepository bomRepository;
    private final BOMLineRepository bomLineRepository;
    private final PartRepository partRepository;

    // Create new BOM
    public BOMResponse createBOM(CreateBOMRequest request, String userId) {
        Part parentPart = partRepository.findById(request.getParentPartId())
                .orElseThrow(() -> new RuntimeException("Parent part not found"));

        // Deactivate previous BOMs for this part
        List<BOM> existingBOMs = bomRepository.findByParentPartAndIsActiveTrue(parentPart);
        existingBOMs.forEach(b -> {
            b.setIsActive(false);
            bomRepository.save(b);
        });

        // Create new BOM
        BOM bom = BOM.builder()
                .parentPart(parentPart)
                .bomName(request.getBomName())
                .bomVersion(request.getBomVersion())
                .description(request.getDescription())
                .createdBy(userId)
                .isActive(true)
                .build();

        BOM savedBOM = bomRepository.save(bom);

        // Add BOM lines
        for (CreateBOMLineRequest lineReq : request.getBomLines()) {
            Part componentPart = partRepository.findById(lineReq.getComponentPartId())
                    .orElseThrow(() -> new RuntimeException("Component part not found"));

            BOMLine line = BOMLine.builder()
                    .bom(savedBOM)
                    .componentPart(componentPart)
                    .lineNumber(lineReq.getLineNumber())
                    .quantity(lineReq.getQuantity())
                    .unitOfMeasure(lineReq.getUnitOfMeasure())
                    .referenceDesignator(lineReq.getReferenceDesignator())
                    .notes(lineReq.getNotes())
                    .sequenceNumber(lineReq.getSequenceNumber())
                    .build();

            bomLineRepository.save(line);
        }

        return mapToResponse(bomRepository.findById(savedBOM.getId()).get());
    }

    // Get BOM by ID
    public BOMResponse getBOMById(Long bomId) {
        BOM bom = bomRepository.findById(bomId)
                .orElseThrow(() -> new RuntimeException("BOM not found"));
        return mapToResponse(bom);
    }

    // Get active BOM for a part
    public BOMResponse getActiveBOMForPart(Long parentPartId) {
        Part part = partRepository.findById(parentPartId)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        BOM activeBOM = bomRepository.findByParentPartAndIsActiveTrue(part)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active BOM found for this part"));

        return mapToResponse(activeBOM);
    }

    // Get all BOMs for a part (including inactive)
    public List<BOMResponse> getAllBOMsForPart(Long parentPartId) {
        Part part = partRepository.findById(parentPartId)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        return bomRepository.findByParentPart(part)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Update BOM (create new version)
    public BOMResponse updateBOM(Long bomId, CreateBOMRequest request, String userId) {
        BOM oldBOM = bomRepository.findById(bomId)
                .orElseThrow(() -> new RuntimeException("BOM not found"));

        oldBOM.setIsActive(false);
        bomRepository.save(oldBOM);

        return createBOM(request, userId);
    }

    // Get flattened BOM (all components recursively)
    public List<BOMLineDto> getFlattenedBOM(Long bomId) {
        BOM bom = bomRepository.findById(bomId)
                .orElseThrow(() -> new RuntimeException("BOM not found"));

        List<BOMLineDto> result = new java.util.ArrayList<>();
        flattenBOM(bom, result, 0);
        return result;
    }

    private void flattenBOM(BOM bom, List<BOMLineDto> result, int level) {
        for (BOMLine line : bom.getBomLines()) {
            BOMLineDto dto = BOMLineDto.builder()
                    .id(line.getId())
                    .componentPartId(line.getComponentPart().getId())
                    .componentPartNumber(line.getComponentPart().getPartNumber())
                    .componentPartName(line.getComponentPart().getName())
                    .lineNumber(line.getLineNumber())
                    .quantity(line.getQuantity())
                    .unitOfMeasure(line.getUnitOfMeasure())
                    .referenceDesignator(line.getReferenceDesignator())
                    .notes(line.getNotes())
                    .sequenceNumber(line.getSequenceNumber())
                    .build();
            result.add(dto);

            // If component is assembly, recursively add its BOM
            Part component = line.getComponentPart();
            if (component.getIsAssembly() && level < 10) {  // Prevent infinite recursion
                List<BOM> subBOMs = bomRepository.findByParentPartAndIsActiveTrue(component);
                if (!subBOMs.isEmpty()) {
                    flattenBOM(subBOMs.get(0), result, level + 1);
                }
            }
        }
    }

    // Helper: Map to DTO
    private BOMResponse mapToResponse(BOM bom) {
        List<BOMLineDto> lines = bom.getBomLines()
                .stream()
                .map(line -> BOMLineDto.builder()
                        .id(line.getId())
                        .componentPartId(line.getComponentPart().getId())
                        .componentPartNumber(line.getComponentPart().getPartNumber())
                        .componentPartName(line.getComponentPart().getName())
                        .lineNumber(line.getLineNumber())
                        .quantity(line.getQuantity())
                        .unitOfMeasure(line.getUnitOfMeasure())
                        .referenceDesignator(line.getReferenceDesignator())
                        .notes(line.getNotes())
                        .sequenceNumber(line.getSequenceNumber())
                        .build())
                .collect(Collectors.toList());

        return BOMResponse.builder()
                .id(bom.getId())
                .parentPartId(bom.getParentPart().getId())
                .parentPartNumber(bom.getParentPart().getPartNumber())
                .bomName(bom.getBomName())
                .bomVersion(bom.getBomVersion())
                .description(bom.getDescription())
                .isActive(bom.getIsActive())
                .createdBy(bom.getCreatedBy())
                .createdAt(bom.getCreatedAt())
                .bomLines(lines)
                .build();
    }
}
