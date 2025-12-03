package org.pet.backendpetshelter.Controller;


import org.pet.backendpetshelter.DTO.SpeciesDTORequest;
import org.pet.backendpetshelter.DTO.SpeciesDTOResponse;
import org.pet.backendpetshelter.Service.SpeciesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/species")
@CrossOrigin
public class SpeciesController {
    private final SpeciesService speciesService;

    public SpeciesController(SpeciesService speciesService) {
        this.speciesService = speciesService;
    }


    @GetMapping
    public List<SpeciesDTOResponse> getAllSpecies(){
        return speciesService.getAllSpecies();
    }

    @GetMapping("/{id}")
    public SpeciesDTOResponse getSpeciesById(@PathVariable Long id){
        return speciesService.GetSpeciesById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<SpeciesDTOResponse> addSpecies(@RequestBody SpeciesDTORequest speciesDTORequest){
        return ResponseEntity.status(201).body(speciesService.addSpecies(speciesDTORequest));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<SpeciesDTOResponse> updateSpecies(@PathVariable Long id, @RequestBody SpeciesDTORequest speciesDTORequest){
        SpeciesDTOResponse updatedSpecies = speciesService.updateSpecies(id, speciesDTORequest);
        return ResponseEntity.ok(updatedSpecies);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSpecies(@PathVariable Long id){
        speciesService.deleteSpecies(id);
        return ResponseEntity.noContent().build();
    }

}
