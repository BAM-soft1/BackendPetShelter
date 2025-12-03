package org.pet.backendpetshelter.Controller;


import org.pet.backendpetshelter.DTO.VaccinationRequest;
import org.pet.backendpetshelter.DTO.VaccineTypeSpeciesResponse;
import org.pet.backendpetshelter.Service.VaccineTypeSpeciesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccine-type-species")
@CrossOrigin
public class VaccineTypeSpeciesController {

    private final VaccineTypeSpeciesService vaccineTypeSpeciesService;

    public VaccineTypeSpeciesController(VaccineTypeSpeciesService vaccineTypeSpeciesService) {
        this.vaccineTypeSpeciesService = vaccineTypeSpeciesService;
    }


    @GetMapping
    public List<VaccineTypeSpeciesResponse> getAllVaccineTypeSpecies() {
        return vaccineTypeSpeciesService.GetAllVaccineTypeSpecies();
    }


    @GetMapping("/{id}")
    public VaccineTypeSpeciesResponse getVaccineTypeSpeciesById(Long id) {
        return vaccineTypeSpeciesService.GetVaccineTypeSpeciesById(id);
    }


    @PostMapping("/add")
    public ResponseEntity<VaccineTypeSpeciesResponse> addVaccination(@RequestBody VaccinationRequest vaccinationRequest){
        return ResponseEntity.status(201).body(vaccineTypeSpeciesService.addVaccineTypeSpecies(vaccinationRequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<VaccineTypeSpeciesResponse> updateVaccineTypeSpecies(@PathVariable Long id, @RequestBody VaccinationRequest vaccinationRequest){
        VaccineTypeSpeciesResponse updatedVaccineTypeSpecies = vaccineTypeSpeciesService.updateVaccineTypeSpecies(id, vaccinationRequest);
        return ResponseEntity.ok(updatedVaccineTypeSpecies);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVaccineTypeSpecies(@PathVariable Long id){
        vaccineTypeSpeciesService.deleteVaccineTypeSpecies(id);
        return ResponseEntity.noContent().build();
    }
}
