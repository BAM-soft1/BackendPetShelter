package org.pet.backendpetshelter.Service;


import org.pet.backendpetshelter.DTO.AdoptionRequest;
import org.pet.backendpetshelter.DTO.AdoptionResponse;
import org.pet.backendpetshelter.Entity.Adoption;
import org.pet.backendpetshelter.Entity.AdoptionApplication;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Repository.AdoptionApplicationRepository;
import org.pet.backendpetshelter.Repository.AdoptionRepository;
import org.pet.backendpetshelter.Repository.AnimalRepository;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;

    public AdoptionService(AdoptionRepository adoptionRepository, AdoptionApplicationRepository adoptionApplicationRepository, UserRepository userRepository, AnimalRepository animalRepository) {
        this.adoptionRepository = adoptionRepository;
        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.userRepository = userRepository;
        this.animalRepository = animalRepository;
    }



    public List<AdoptionResponse> GetAllAdoptions() {
        return adoptionRepository.findAll().stream()
                .map(AdoptionResponse::new)
                .collect(Collectors.toList());
    }


    public AdoptionResponse GetAdoptionById(Long id) {
        return adoptionRepository.findById(id)
                .map(AdoptionResponse::new)
                .orElseThrow(() -> new RuntimeException("Adoption not found with id: " + id));
    }

    public AdoptionResponse addAdoption(AdoptionRequest request) {


        validateUserId(request.getUserId());
        validateAnimalId(request.getAnimalId());
        validateApplication(request.getAdoptionApplicationId());
        validateAdoptionDate(request.getAdoptionDate());
        validateIsActive(request.getIsActive());


        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + request.getAnimalId()));

        AdoptionApplication application = adoptionApplicationRepository.findById(request.getAdoptionApplicationId())
                .orElseThrow(() -> new RuntimeException("Adoption Application not found with id: " + request.getAdoptionApplicationId()));



        Adoption adoption = new Adoption();
        adoption.setAdoptionUser(user);
        adoption.setAnimal(animal);
        adoption.setApplication(application);
        adoption.setAdoptionDate(request.getAdoptionDate());
        adoption.setIsActive(true);

        adoptionRepository.save(adoption);
        return new AdoptionResponse(adoption);
    }

        // Validation Methods
        private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        }

    private void validateAnimalId(Long animalId) {
        if (animalId == null) {
            throw new IllegalArgumentException("Animal ID cannot be null");
        }
    }

    private void validateApplication(Long AdoptionApplicationId) {
        if (AdoptionApplicationId == null) {
            throw new IllegalArgumentException("Adoption Application ID cannot be null");
        }

    }

    private void validateAdoptionDate(Date adoptionDate) {
        if (adoptionDate == null) {
            throw new IllegalArgumentException("Adoption date cannot be null");
        }

        if (adoptionDate.before(new Date())) {
            throw new IllegalArgumentException("Adoption date cannot be in the past.");
        }
    }


    private void validateIsActive(Boolean isActive) {
        if (isActive == null) {
            throw new IllegalArgumentException("IsActive cannot be null");
        }
    }


    /* Update Adoption */
    public AdoptionResponse updateAdoption(Long id, AdoptionRequest request) {
        Adoption adoption = adoptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adoption not found with id: " + id));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + request.getAnimalId()));

        AdoptionApplication application = adoptionApplicationRepository.findById(request.getAdoptionApplicationId())
                .orElseThrow(() -> new RuntimeException("Adoption Application not found with id: " + request.getAdoptionApplicationId()));



        adoption.setAdoptionUser(user);
        adoption.setAnimal(animal);
        adoption.setApplication(application);
        adoption.setAdoptionDate(request.getAdoptionDate());
        adoption.setIsActive(request.getIsActive());

        adoptionRepository.save(adoption);
        return new AdoptionResponse(adoption);
    }

    /* Delete Adoption */
    public void deleteAdoption(Long id) {
        Adoption adoption = adoptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adoption not found with id: " + id));
        adoptionRepository.delete(adoption);
    }



    }



