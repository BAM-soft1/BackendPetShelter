package org.pet.backendpetshelter.Controller;


import org.pet.backendpetshelter.DTO.AdoptionRequest;
import org.pet.backendpetshelter.DTO.AdoptionResponse;
import org.pet.backendpetshelter.Service.AdoptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoption")
@CrossOrigin
public class AdoptionController {

    private final AdoptionService adoptionService;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }


    @GetMapping
    public List<AdoptionResponse> getAllAdoptions() {
        return adoptionService.GetAllAdoptions();
    }

    @GetMapping("/{id}")
    public AdoptionResponse getAdoptionById(Long id) {
        return adoptionService.GetAdoptionById(id);
    }


    @PostMapping("/add")
    public ResponseEntity<AdoptionResponse> createAdoption(@RequestBody AdoptionRequest adoptionRequest) {
        return ResponseEntity.status(201).body(adoptionService.addAdoption(adoptionRequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AdoptionResponse> updateAdoption(@PathVariable Long id , @RequestBody AdoptionRequest adoptionRequest) {
        AdoptionResponse updatedAdoption = adoptionService.updateAdoption(id, adoptionRequest);
        return ResponseEntity.ok(updatedAdoption);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAdoption(@PathVariable Long id) {
        adoptionService.deleteAdoption(id);
        return ResponseEntity.noContent().build();
    }

}
