package org.pet.backendpetshelter.Service;


import org.pet.backendpetshelter.DTO.AdoptionRequest;
import org.pet.backendpetshelter.DTO.AdoptionResponse;
import org.pet.backendpetshelter.Entity.Adoption;
import org.pet.backendpetshelter.Entity.AdoptionApplication;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Reposiotry.AdoptionApplicationRepository;
import org.pet.backendpetshelter.Reposiotry.AdoptionRepository;
import org.pet.backendpetshelter.Reposiotry.AnimalRepository;
import org.pet.backendpetshelter.Reposiotry.UserRepository;
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

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal not found"));

        AdoptionApplication application = adoptionApplicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Adoption adoption = new Adoption();
        adoption.setAdoptionUser(user);
        adoption.setAnimal(animal);
        adoption.setApplication(application);
        adoption.setAdoptionDate(request.getAdoptionDate());
        adoption.setIsActive(true);

        adoptionRepository.save(adoption);

        return new AdoptionResponse(adoption);
    }



}
