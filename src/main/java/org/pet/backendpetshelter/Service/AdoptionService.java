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


        validateUser(request.getUser());
        validateAnimal(request.getAnimal());
        validateApplication(request.getAdoptionApplication());
        validateAdoptionDate(request.getAdoptionDate());
        validateIsActive(request.getIsActive());




        Adoption adoption = new Adoption();
        adoption.setAdoptionUser(request.getUser());
        adoption.setAnimal(request.getAnimal());
        adoption.setApplication(request.getAdoptionApplication());
        adoption.setAdoptionDate(request.getAdoptionDate());
        adoption.setIsActive(true);

        adoptionRepository.save(adoption);
        return new AdoptionResponse(adoption);
    }

        // Validation Methods
        private void validateUser(User user) {
            if (user == null || user.getId() == null) {
                throw new IllegalArgumentException("User cannot be null");
            }

        }

    private void validateAnimal(Animal animal) {
        if (animal == null || animal.getId() == null) {
            throw new IllegalArgumentException("Animal cannot be null");
        }
    }

    private void validateApplication(AdoptionApplication application) {
        if (application == null || application.getId() == null) {
            throw new IllegalArgumentException("Adoption Application cannot be null");
        }

    }

    private void validateAdoptionDate(java.util.Date adoptionDate) {
        if (adoptionDate == null) {
            throw new IllegalArgumentException("Adoption date cannot be null");
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

        adoption.setAdoptionUser(request.getUser());
        adoption.setAnimal(request.getAnimal());
        adoption.setApplication(request.getAdoptionApplication());
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



