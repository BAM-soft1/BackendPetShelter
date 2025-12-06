package org.pet.backendpetshelter.Service;


import jakarta.persistence.EntityNotFoundException;
import org.pet.backendpetshelter.DTO.AnimalDTORequest;
import org.pet.backendpetshelter.DTO.AnimalDTOResponse;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Repository.AnimalRepository;
import org.pet.backendpetshelter.Repository.BreedRepository;
import org.pet.backendpetshelter.Repository.SpeciesRepository;
import org.pet.backendpetshelter.Status;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final BreedRepository breedRepository;
    private final SpeciesRepository speciesRepository;

    public AnimalService(AnimalRepository animalRepository, BreedRepository breedRepository, SpeciesRepository speciesRepository) {
        this.animalRepository = animalRepository;
        this.breedRepository = breedRepository;
        this.speciesRepository = speciesRepository;
    }

    /*Add Animal */
    public AnimalDTOResponse addAnimal(AnimalDTORequest request) {

        validateName(request.getName());
        validateSpeciesId(request.getSpeciesId());
        validateBreedId(request.getBreedId());
        validateSex(request.getSex());
        validateBirthDate(request.getBirthDate());
        validateIntakeDate(request.getIntakeDate(), request.getBirthDate());
        validateStatus(request.getStatus());
        validatePrice(request.getPrice());
        validateIsActive(request.getIsActive());
        validateImageUrl(request.getImageUrl());

        Species species = speciesRepository.findById(request.getSpeciesId())
                .orElseThrow(() -> new EntityNotFoundException("Species not found"));

        Breed breed = breedRepository.findById(request.getBreedId())
                .orElseThrow(() -> new EntityNotFoundException("Breed not found"));

        Animal animal = new Animal();
        animal.setName(request.getName());
        animal.setSex(request.getSex());
        animal.setSpecies(species);
        animal.setBreed(breed);
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

        if (!name.matches("^[a-zA-Z]+$")) {
            throw new IllegalArgumentException("Name must contain at least one non-alphabetic character");
        }

        if (name.length() < 2 || name.length() > 30) {
            throw new IllegalArgumentException("Name must be between 2 and 30 characters long");
        }
    }


    private void validateSpeciesId(Long speciesId) {
        if (speciesId == null) {
            throw new IllegalArgumentException("Species ID cannot be null");
        }

        if (speciesId <= 0) {
            throw new IllegalArgumentException("Species ID must be a positive number");
        }

        if (!speciesRepository.existsById(speciesId)) {
            throw new IllegalArgumentException("Species ID does not exist");
        }
    }


    private void validateBreedId(Long breedId) {
        if (breedId == null) {
            throw new IllegalArgumentException("Breed ID cannot be null");
        }

        if (breedId <= 0) {
            throw new IllegalArgumentException("Breed ID must be a positive number");
        }

        if (!breedRepository.existsById(breedId)) {
            throw new IllegalArgumentException("Breed ID does not exist");
        }
    }

    private void validateSex(String sex) {
        if (sex == null || sex.isBlank()) {
            throw new IllegalArgumentException("Sex cannot be null or empty");
        }

        if (!sex.equals("Male") && !sex.equals("Female")) {
            throw new IllegalArgumentException("Sex must be either 'Male' or 'Female'");
        }
    }

    private void validateBirthDate(Date birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
        Date today = new Date();
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Birthdate cannot be in the future");
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

    private void validateStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

    }

    private boolean isStatusActive(Status status) {
        return status == Status.APPROVED;
    }

    private void validatePrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        if (price > 30000) {
            throw new IllegalArgumentException("Price cannot exceed 30000");
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

        if (!imageUrl.matches("^(http|https)://.+")) {
            throw new IllegalArgumentException("Invalid image URL format"); }
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

        Breed breed = breedRepository.findById(request.getBreedId())
                .orElseThrow(() -> new EntityNotFoundException("Breed not found"));

        Species species = speciesRepository.findById(request.getSpeciesId())
                .orElseThrow(() -> new EntityNotFoundException("Species not found"));

        animal.setName(request.getName());
        animal.setSpecies(species);
        animal.setBreed(breed);
        animal.setSex(request.getSex());
        animal.setBirthDate(request.getBirthDate());
        animal.setIntakeDate(request.getIntakeDate());
        animal.setStatus(request.getStatus());
        animal.setPrice(request.getPrice());
        animal.setIsActive(isStatusActive(String.valueOf(request.getStatus())));
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
