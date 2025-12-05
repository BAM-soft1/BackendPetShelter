package org.pet.backendpetshelter.Controller;


import org.pet.backendpetshelter.DTO.AdoptionApplicationRequest;
import org.pet.backendpetshelter.DTO.AdoptionApplicationRespons;
import org.pet.backendpetshelter.Service.AdoptionApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoption-application")
@CrossOrigin
public class AdoptionApplicationController {

    private final AdoptionApplicationService adoptionApplicationService;

    public AdoptionApplicationController(AdoptionApplicationService adoptionApplicationService) {
        this.adoptionApplicationService = adoptionApplicationService;
    }

    @GetMapping
    public List<AdoptionApplicationRespons> getAllAdoptionApplications() {
        return adoptionApplicationService.GetAllAdoptionApplications();
    }


    @GetMapping("/{id}")
    public AdoptionApplicationRespons getAdoptionApplicationById(Long id) {
        return adoptionApplicationService.GetAdoptionApplicationById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<AdoptionApplicationRespons> addAdoptionApplication(@RequestBody AdoptionApplicationRequest request) {
        return ResponseEntity.status(201).body(adoptionApplicationService.addAdoptionApplication(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AdoptionApplicationRespons> updateAdoptionApplication(@PathVariable Long id , @RequestBody AdoptionApplicationRequest request) {
        AdoptionApplicationRespons updatedApplication = adoptionApplicationService.updateAdoptionApplication(id, request);
        return ResponseEntity.ok(updatedApplication);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAdoptionApplication(@PathVariable Long id){
        adoptionApplicationService.deleteAdoptionApplication(id);
        return ResponseEntity.noContent().build();
    }


}
