package org.pet.backendpetshelter.Service;


import org.pet.backendpetshelter.DTO.AdoptionApplicationRequest;
import org.pet.backendpetshelter.DTO.AdoptionApplicationRespons;
import org.pet.backendpetshelter.Entity.AdoptionApplication;
import org.pet.backendpetshelter.Repository.AdoptionApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdoptionApplicationService
{
    private final AdoptionApplicationRepository adoptionApplicationRepository;

    public AdoptionApplicationService(AdoptionApplicationRepository adoptionApplicationRepository) {
        this.adoptionApplicationRepository = adoptionApplicationRepository;
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

        AdoptionApplication application = new AdoptionApplication();
        application.setUser(request.getUser());
        application.setAnimal(request.getAnimal());
        application.setApplicationDate(request.getApplicationDate());
        application.setStatus(request.getStatus());
        application.setReviewedByUser(request.getReviewedByUser());
        application.setIsActive(request.getIsActive());

        adoptionApplicationRepository.save(application);
        return new AdoptionApplicationRespons(application);

    }

    /* Update Adoption Application */
    public AdoptionApplicationRespons updateAdoptionApplication(Long id, AdoptionApplicationRequest request) {
        AdoptionApplication application = adoptionApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find application with id: " + id));

        application.setUser(request.getUser());
        application.setAnimal(request.getAnimal());
        application.setApplicationDate(request.getApplicationDate());
        application.setStatus(request.getStatus());
        application.setReviewedByUser(request.getReviewedByUser());
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