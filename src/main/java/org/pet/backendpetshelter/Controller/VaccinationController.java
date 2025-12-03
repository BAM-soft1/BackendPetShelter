package org.pet.backendpetshelter.Controller;

import org.pet.backendpetshelter.DTO.VaccinationRequest;
import org.pet.backendpetshelter.DTO.VaccinationResponse;
import org.pet.backendpetshelter.Service.VaccinationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccination")
@CrossOrigin
public class VaccinationController {


private final VaccinationService vaccinationService;

    public VaccinationController(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }


    @GetMapping
    public List<VaccinationResponse> getAllVaccinations(){
        return vaccinationService.GetAllVaccinations();
    }


    @GetMapping("/{id}")
    public VaccinationResponse getVaccinationById(Long id){
        return vaccinationService.GetVaccinationById(id);
    }


    @PostMapping("/add")
    public ResponseEntity<VaccinationResponse> addVaccination(@RequestBody VaccinationRequest vaccinationRequest){
        return ResponseEntity.status(201).body(vaccinationService.addVaccination(vaccinationRequest));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<VaccinationResponse> updateVaccination(@PathVariable Long id, @RequestBody VaccinationRequest vaccinationRequest){
        VaccinationResponse updatedVaccination = vaccinationService.updateVaccination(id, vaccinationRequest);
        return ResponseEntity.ok(updatedVaccination);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVaccination(@PathVariable Long id){
        vaccinationService.deleteVaccination(id);
        return ResponseEntity.noContent().build();
    }


}
