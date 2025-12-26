package com.sam.mini_plm_backend.controller;

import com.sam.mini_plm_backend.Model.Part;
import com.sam.mini_plm_backend.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://mini-plm-frontend.onrender.com"
})
public class PartController {

    @Autowired
    private PartRepository partRepository;

    @GetMapping
    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    @PostMapping
    public Part createPart(@RequestBody Part part) {
        return partRepository.save(part);
    }

    @DeleteMapping("/{id}")
    public void deletePart(@PathVariable Long id) {
        partRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Part updatePart(@PathVariable Long id, @RequestBody Part part) {
        part.setId(id);
        return partRepository.save(part);
    }


}
