package com.sam.mini_plm_backend.service;

import com.sam.mini_plm_backend.entity.Part;
import com.sam.mini_plm_backend.repository.PartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public Page<Part> searchParts(String name, String partNumber, Pageable pageable) {

        boolean hasName = StringUtils.hasText(name);
        boolean hasPartNumber = StringUtils.hasText(partNumber);

        if (hasName && hasPartNumber) {
            return partRepository
                    .findByNameContainingIgnoreCaseAndPartNumberContainingIgnoreCaseAndIsDeletedFalse(
                            name, partNumber, pageable
                    );
        }

        if (hasName) {
            return partRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name, pageable);
        }

        if (hasPartNumber) {
            return partRepository.findByPartNumberContainingIgnoreCaseAndIsDeletedFalse(partNumber, pageable);
        }

        return partRepository.findByIsDeletedFalse(pageable);
    }
}
