package com.sam.mini_plm_backend.service;

import com.sam.mini_plm_backend.entity.Part;
import com.sam.mini_plm_backend.dto.BomDto;
import com.sam.mini_plm_backend.repository.PartRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BomService {

    private final PartRepository partRepository;

    public BomService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public BomDto getPartHierarchy(Long partId) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found: " + partId));
        return convertToBomDto(part);
    }

    private BomDto convertToBomDto(Part part) {
        List<BomDto> subParts = new ArrayList<>();

        if (part.getSubParts() != null && !part.getSubParts().isEmpty()) {
            subParts = part.getSubParts().stream()
                    .map(this::convertToBomDto)
                    .collect(Collectors.toList());
        }

        return BomDto.builder()
                .id(part.getId())
                .name(part.getName())
                .partNumber(part.getPartNumber())
                .quantityRequired(part.getQuantityRequired())
                .isAssembly(part.getIsAssembly())
                .subParts(subParts)
                .build();
    }

    public void addSubPart(Long parentPartId, Long subPartId, Double quantity) {
        Part parentPart = partRepository.findById(parentPartId)
                .orElseThrow(() -> new RuntimeException("Parent part not found: " + parentPartId));

        Part subPart = partRepository.findById(subPartId)
                .orElseThrow(() -> new RuntimeException("Sub part not found: " + subPartId));

        subPart.setParentPart(parentPart);
        subPart.setQuantityRequired(quantity);

        partRepository.save(subPart);
    }
}
