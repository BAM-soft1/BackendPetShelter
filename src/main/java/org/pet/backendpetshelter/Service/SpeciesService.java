package org.pet.backendpetshelter.Service;


import jakarta.persistence.EntityNotFoundException;
import org.pet.backendpetshelter.DTO.SpeciesDTORequest;
import org.pet.backendpetshelter.DTO.SpeciesDTOResponse;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Repository.SpeciesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpeciesService {

    private final SpeciesRepository speciesRepository;

    public SpeciesService(SpeciesRepository speciesRepository) {
        this.speciesRepository = speciesRepository;
    }



    /* Get All Species */
    public List<SpeciesDTOResponse> getAllSpecies(){
        return speciesRepository.findAll().stream().map(SpeciesDTOResponse::new).collect(Collectors.toList());
    }

    /* Get Specific Species */
    public SpeciesDTOResponse GetSpeciesById(Long id) {
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Species not found with id: " + id));
        return new SpeciesDTOResponse(species);
    }

    /* Add Species */
    public SpeciesDTOResponse addSpecies(SpeciesDTORequest request){

        // Validate input data
        validateName(request.getName());



        Species newSpecies = new Species();
        newSpecies.setName(request.getName());
        speciesRepository.save(newSpecies);
        return new SpeciesDTOResponse(newSpecies);
    }


    // Validation Methods
    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Species name cannot be null or empty.");
        }

        if (speciesRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Species with name '" + name + "' already exists.");
        }

        if (name.length() > 50) {
            throw new IllegalArgumentException("Species name cannot exceed 30 characters.");
        }

        if (name.length() < 2) {
            throw new IllegalArgumentException("Species name must be at least 2 characters long.");
        }

        if (!name.matches("^[a-zA-Z\\s'-]+$")) {
            throw new IllegalArgumentException("Species name contains invalid characters.");
        }

    }



    /* Update Species */
    public SpeciesDTOResponse updateSpecies(Long id, SpeciesDTORequest request){
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Species not found with id: " + id));

        if (!species.getName().equals(request.getName())) {
            validateName(request.getName());
        }

        species.setName(request.getName());
        speciesRepository.save(species);
        return new SpeciesDTOResponse(species);
    }
    /* Delete Species */
    public void deleteSpecies(Long id){
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Species not found with id: " + id));
        speciesRepository.delete(species);
    }

}
