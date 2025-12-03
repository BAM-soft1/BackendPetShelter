package org.pet.backendpetshelter.Controller;


import org.pet.backendpetshelter.DTO.AnimalDTORequest;
import org.pet.backendpetshelter.DTO.AnimalDTOResponse;
import org.pet.backendpetshelter.Service.AnimalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animal")
@CrossOrigin
public class AnimalController {

    private final AnimalService animalService;


    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }



    @GetMapping
    public List<AnimalDTOResponse> getAllAnimals() {
        return animalService.GetAllAnimals();
    }


    @GetMapping("/{id}")
    public AnimalDTOResponse getAnimalById(Long id) {
        return animalService.GetAnimalById(id);
    }


    @PostMapping("/add")
    public ResponseEntity<AnimalDTOResponse> addAnimal(@RequestBody AnimalDTORequest animalDTORequest) {
        return ResponseEntity.status(201).body(animalService.addAnimal(animalDTORequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AnimalDTOResponse> updateAnimal(@PathVariable Long id, @RequestBody AnimalDTORequest animalDTORequest) {
        AnimalDTOResponse updatedAnimal = animalService.updateAnimal(id, animalDTORequest);
        return ResponseEntity.ok(updatedAnimal);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id){
        animalService.deleteAnimal(id);
        return ResponseEntity.noContent().build();
    }


}
