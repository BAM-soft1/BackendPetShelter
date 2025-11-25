package org.pet.backendpetshelter.Controller;


import org.pet.backendpetshelter.DTO.BreedDTORequest;
import org.pet.backendpetshelter.DTO.BreedDTOResponse;
import org.pet.backendpetshelter.Service.BreedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/breed")
@CrossOrigin
public class BreedController {

    private final BreedService breedService;

    public BreedController(BreedService breedService) {
        this.breedService = breedService;
    }

    @GetMapping
    public List<BreedDTOResponse> getAllBreeds() {
        return breedService.getAllBreeds();
    }

    @GetMapping("/{id}")
    public BreedDTOResponse getBreedById(@PathVariable Long id) {
        return breedService.getBreedById(id);
    }

    @GetMapping("/species/{speciesId}")
    public List<BreedDTOResponse> getBreedsBySpecies(@PathVariable Long speciesId) {
        return breedService.getBreedsBySpecies(speciesId);
    }

    @PostMapping("/add")
    public ResponseEntity<BreedDTOResponse> addBreed(@RequestBody BreedDTORequest breedDTORequest) {
        return ResponseEntity.status(201).body(breedService.addBreed(breedDTORequest));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<BreedDTOResponse> updateBreed(@PathVariable Long id, @RequestBody BreedDTORequest breedDTORequest) {
        BreedDTOResponse updatedBreed = breedService.updateBreed(id, breedDTORequest);
        return ResponseEntity.ok(updatedBreed);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBreed(@PathVariable Long id) {
        breedService.deleteBreed(id);
        return ResponseEntity.noContent().build();
    }

}
