package org.pet.backendpetshelter.Service;


import org.pet.backendpetshelter.DTO.AdoptionApplicationRequest;
import org.pet.backendpetshelter.DTO.AdoptionApplicationRespons;
import org.pet.backendpetshelter.Entity.AdoptionApplication;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Repository.AdoptionApplicationRepository;
import org.pet.backendpetshelter.Repository.AnimalRepository;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdoptionApplicationService
{
    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;


    public AdoptionApplicationService(AdoptionApplicationRepository adoptionApplicationRepository, UserRepository userRepository, AnimalRepository animalRepository) {
        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.userRepository = userRepository;
        this.animalRepository = animalRepository;
    }

    public List<AdoptionApplicationRespons> GetAllAdoptionApplications() {
        return adoptionApplicationRepository.findAll().stream()
                .map(AdoptionApplicationRespons::new)
                .toList();
    }

    public AdoptionApplicationRespons GetAdoptionApplicationById(Long id) {
        AdoptionApplication application = adoptionApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find application with id: " + id));
        return new AdoptionApplicationRespons(application);
    }


    public AdoptionApplicationRespons addAdoptionApplication(AdoptionApplicationRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal not found"));

        User reviewer = null;
        if (request.getReviewedByUserId() != null) {
            reviewer = userRepository.findById(request.getReviewedByUserId())
                    .orElseThrow(() -> new RuntimeException("Reviewer not found"));
        }

        AdoptionApplication application = new AdoptionApplication();
        application.setUser(user);
        application.setAnimal(animal);
        application.setApplicationDate(request.getApplicationDate());
        application.setStatus(request.getStatus());
        application.setReviewedByUser(reviewer);
        application.setIsActive(request.getIsActive());

        adoptionApplicationRepository.save(application);
        return new AdoptionApplicationRespons(application);

    }

    /* Update Adoption Application */
    public AdoptionApplicationRespons updateAdoptionApplication(Long id, AdoptionApplicationRequest request) {

        AdoptionApplication application = adoptionApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find application with id: " + id));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal not found with id: " + request.getAnimalId()));

        User reviewer = null;
        if (request.getReviewedByUserId() != null) {
            reviewer = userRepository.findById(request.getReviewedByUserId())
                    .orElseThrow(() -> new RuntimeException("Reviewer not found with id: " + request.getReviewedByUserId()));
        }

        application.setUser(user);
        application.setAnimal(animal);
        application.setApplicationDate(request.getApplicationDate());
        application.setStatus(request.getStatus());
        application.setReviewedByUser(reviewer);
        application.setIsActive(request.getIsActive());

        adoptionApplicationRepository.save(application);

        return new AdoptionApplicationRespons(application);
    }


    /* Delete Adoption Application */
    public void deleteAdoptionApplication(Long id) {
        AdoptionApplication application = adoptionApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find application with id: " + id));
        adoptionApplicationRepository.delete(application);
    }
}