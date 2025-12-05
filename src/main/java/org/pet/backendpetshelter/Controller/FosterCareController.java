package org.pet.backendpetshelter.Controller;


import org.pet.backendpetshelter.DTO.FosterCareRespons;
import org.pet.backendpetshelter.Service.FosterCareService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foster-care")
@CrossOrigin
public class FosterCareController {

    private final FosterCareService fosterCareService;

    public FosterCareController(FosterCareService fosterCareService) {
        this.fosterCareService = fosterCareService;
    }


    @GetMapping
    public List<FosterCareRespons> getAllFosterCares() {
        return fosterCareService.GetAllFosterCares();
    }

    @GetMapping("/{id}")
    public FosterCareRespons getFosterCareById(Long id) {
        return fosterCareService.GetFosterCareById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<FosterCareRespons> addFosterCare(@RequestBody FosterCareRespons fosterCareRespons) {
        return ResponseEntity.status(201).body(fosterCareService.addFosterCare(fosterCareRespons));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<FosterCareRespons> updateFosterCare(@PathVariable Long id, @RequestBody FosterCareRespons fosterCareRespons) {
        FosterCareRespons updatedFosterCare = fosterCareService.updateFosterCare(id, fosterCareRespons);
        return ResponseEntity.ok(updatedFosterCare);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFosterCare(@PathVariable Long id) {
        fosterCareService.deleteFosterCare(id);
        return ResponseEntity.noContent().build();
    }



}
