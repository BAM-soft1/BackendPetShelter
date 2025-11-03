package org.pet.backendpetshelter.Controller;


import jakarta.validation.Valid;

import org.pet.backendpetshelter.DTO.VeterinianDTORequest;
import org.pet.backendpetshelter.DTO.VeterinianDTOResponse;
import org.pet.backendpetshelter.Service.VeterinianService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veterinian")
@CrossOrigin
public class VeterinianController {

    private final VeterinianService veterinianService;

    public VeterinianController(VeterinianService veterinianService) {
        this.veterinianService = veterinianService;
    }


    @GetMapping
    public List<VeterinianDTOResponse> getAllVeterinians() {
        return veterinianService.GetAllVeterinians();
    }

    @GetMapping("/{id}")
    public VeterinianDTOResponse getVeterinianById(@PathVariable Long id) {
        return veterinianService.GetVeterinianById(id);
    }



    @PostMapping("/add")
    public ResponseEntity<VeterinianDTOResponse> addVeterinian(@Valid @RequestBody VeterinianDTORequest veterinianDTORequest){
        return ResponseEntity.status(201).body(veterinianService.addVeterinian(veterinianDTORequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<VeterinianDTOResponse> updateVeterinian(@PathVariable Long id, @Valid @RequestBody VeterinianDTORequest veterinianDTORequest){
        VeterinianDTOResponse updatedVeterinian = veterinianService.updateVeterinian(id, veterinianDTORequest);
        return ResponseEntity.ok(updatedVeterinian);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVeterinian(@PathVariable Long id){
        veterinianService.deleteVeterinian(id);
        return ResponseEntity.noContent().build();
    }


}
