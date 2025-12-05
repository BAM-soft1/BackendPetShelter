package org.pet.backendpetshelter.Controller;


import jakarta.validation.Valid;

import org.pet.backendpetshelter.DTO.VeterinarianDTORequest;
import org.pet.backendpetshelter.DTO.VeterinarianDTOResponse;
import org.pet.backendpetshelter.Service.VeterinarianService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarian")
@CrossOrigin
public class VeterinarianController {

    private final VeterinarianService veterinarianService;

    public VeterinarianController(VeterinarianService veterinarianService) {
        this.veterinarianService = veterinarianService;
    }


    @GetMapping
    public List<VeterinarianDTOResponse> getAllVeterinians() {
        return veterinarianService.GetAllVeterinians();
    }

    @GetMapping("/{id}")
    public VeterinarianDTOResponse getVeterinianById(@PathVariable Long id) {
        return veterinarianService.GetVeterinianById(id);
    }



    @PostMapping("/add")
    public ResponseEntity<VeterinarianDTOResponse> addVeterinian(@Valid @RequestBody VeterinarianDTORequest veterinarianDTORequest){
        return ResponseEntity.status(201).body(veterinarianService.addVeterinian(veterinarianDTORequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<VeterinarianDTOResponse> updateVeterinian(@PathVariable Long id, @Valid @RequestBody VeterinarianDTORequest veterinarianDTORequest){
        VeterinarianDTOResponse updatedVeterinian = veterinarianService.updateVeterinian(id, veterinarianDTORequest);
        return ResponseEntity.ok(updatedVeterinian);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVeterinian(@PathVariable Long id){
        veterinarianService.deleteVeterinian(id);
        return ResponseEntity.noContent().build();
    }


}