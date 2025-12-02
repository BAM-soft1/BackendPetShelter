package org.pet.backendpetshelter.Service;


import jakarta.persistence.EntityNotFoundException;
import org.pet.backendpetshelter.DTO.AnimalDTORequest;
import org.pet.backendpetshelter.DTO.AnimalDTOResponse;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Reposiotry.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    /*Add Animal */
    public AnimalDTOResponse addAnimal(AnimalDTORequest request) {


        // Validate input data

        validateName(request.getName());
        validateSpecies(request.getSpecies());
        validateBreed(request.getBreed());
        validateSex(request.getSex());
        validateBirthDate(request.getBirthDate());
        validateIntakeDate(request.getIntakeDate(), request.getBirthDate());
        validateStatus(request.getStatus());
        validatePrice(request.getPrice());
        validateIsActive(request.getIsActive());
        validateImageUrl(request.getImageUrl());

        Animal animal = new Animal();
        animal.setName(request.getName());
        animal.setSex(request.getSex());
        animal.setSpecies(request.getSpecies());
        animal.setBreed(request.getBreed());
        animal.setBirthDate(request.getBirthDate());
        animal.setIntakeDate(request.getIntakeDate());
        animal.setStatus(request.getStatus());
        animal.setPrice(request.getPrice());
        animal.setIsActive(request.getIsActive());
        animal.setImageUrl(request.getImageUrl());

        animalRepository.save(animal);
        return new AnimalDTOResponse(animal);

    }


    // Validation Methods

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
    }

    private void validateSpecies(Species species) {
        if (species == null) {
            throw new IllegalArgumentException("Species cannot be null");
        }
    }

    private void validateBreed(Breed breed) {
        if (breed == null) {
            throw new IllegalArgumentException("Breed cannot be null");
        }
    }

    private void validateSex(String sex) {
        if (sex == null || sex.isBlank()) {
            throw new IllegalArgumentException("Sex cannot be null or empty");
        }
    }

    private void validateBirthDate(Date birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
        Date today = new Date();
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
    }

    private void validateIntakeDate(Date intakeDate, Date birthDate) {
        if (intakeDate == null) {
            throw new IllegalArgumentException("Intake date cannot be null");
        }
        Date today = new Date();
        if (intakeDate.after(today)) {
            throw new IllegalArgumentException("Intake date cannot be in the future");
        }
        if (birthDate != null && intakeDate.before(birthDate)) {
            throw new IllegalArgumentException("Intake date cannot be before birth date");
        }
    }

    private void validateStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
    }

    private void validatePrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    private void validateIsActive(Boolean isActive) {
        if (isActive == null) {
            throw new IllegalArgumentException("isActive cannot be null");
        }
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }
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
