package org.pet.backendpetshelter.Service;


import jakarta.persistence.EntityNotFoundException;
import org.pet.backendpetshelter.DTO.AnimalDTORequest;
import org.pet.backendpetshelter.DTO.AnimalDTOResponse;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Reposiotry.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }



    /* Get All Animals */
    public List<AnimalDTOResponse> GetAllAnimals() {
        return animalRepository.findAll().stream()
                .map(AnimalDTOResponse::new)
                .collect(Collectors.toList());
    }


    /* Get Specific Animal */
    public AnimalDTOResponse GetAnimalById(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + id));
        return new AnimalDTOResponse(animal);
    }

    /*Add Animal */
    public AnimalDTOResponse addAnimal(AnimalDTORequest request){

        Animal animal = new Animal();
        animal.setName(request.getName());
        animal.setSex(request.getSex());
        animal.setSpecies(request.getSpecies());
        animal.setBreed(request.getBreed());
        animal.setBirthDate(request.getBirthDate());
        animal.setIntakeDate(request.getIntakeDate());
        animal.setStatus(request.getStatus());
        animal.setPrice(request.getPrice());
        // Automatically set isActive based on status
        animal.setIsActive(isStatusActive(request.getStatus()));
        animal.setImageUrl(request.getImageUrl());

        animalRepository.save(animal);
        return new AnimalDTOResponse(animal);
    }


    /* Update Animal */
    public AnimalDTOResponse updateAnimal(Long id, AnimalDTORequest request) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + id));

        animal.setName(request.getName());
        animal.setSpecies(request.getSpecies());
        animal.setBreed(request.getBreed());
        animal.setSex(request.getSex());
        animal.setBirthDate(request.getBirthDate());
        animal.setIntakeDate(request.getIntakeDate());
        animal.setStatus(request.getStatus());
        animal.setPrice(request.getPrice());
        // Automatically set isActive based on status
        animal.setIsActive(isStatusActive(request.getStatus()));
        animal.setImageUrl(request.getImageUrl());

        animalRepository.save(animal);
        return new AnimalDTOResponse(animal);
    }


    /* Delete Animal  */
    public void deleteAnimal (Long id){
        if (!animalRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. User not found with id: " + id);
        }
        animalRepository.deleteById(id);
    }

    /**
     * Helper method to determine if an animal should be active based on status
     * @param status the status of the animal
     * @return true if the animal should be active, false otherwise
     */
    private boolean isStatusActive(String status) {
        // Animals are active when they are available or fostered
        // Animals are inactive when adopted or deceased
        return "available".equalsIgnoreCase(status) || "fostered".equalsIgnoreCase(status);
    }

}
