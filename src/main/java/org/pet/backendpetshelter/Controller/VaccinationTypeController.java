package org.pet.backendpetshelter.Controller;


import org.pet.backendpetshelter.DTO.VaccinationTypeResponse;
import org.pet.backendpetshelter.Service.VaccinationTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccination-type")
@CrossOrigin
public class VaccinationTypeController {


    private final VaccinationTypeService vaccinationTypeService;

    public VaccinationTypeController(VaccinationTypeService vaccinationTypeService) {
        this.vaccinationTypeService = vaccinationTypeService;
    }


    @GetMapping
    public List<VaccinationTypeResponse> getAllVaccinationTypes() {
        return vaccinationTypeService.GetAllVaccinationTypes();
    }

    @GetMapping("/{id}")
    public VaccinationTypeResponse getVaccinationTypeById(Long id) {
        return vaccinationTypeService.GetVaccinationTypeById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<VaccinationTypeResponse> addVaccinationType(@RequestBody VaccinationTypeResponse vaccinationTypeRequest) {
        return ResponseEntity.status(201).body(vaccinationTypeService.addVaccinationType(vaccinationTypeRequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<VaccinationTypeResponse> updateVaccinationType(@PathVariable Long id, @RequestBody VaccinationTypeResponse vaccinationTypeRequest) {
        VaccinationTypeResponse updatedVaccinationType = vaccinationTypeService.updateVaccinationType(id, vaccinationTypeRequest);
        return ResponseEntity.ok(updatedVaccinationType);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVaccinationType(@PathVariable Long id) {
        vaccinationTypeService.deleteVaccinationType(id);
        return ResponseEntity.noContent().build();
    }

}
