package org.pet.backendpetshelter.Service;

import jakarta.persistence.EntityNotFoundException;
import org.pet.backendpetshelter.DTO.BreedDTORequest;
import org.pet.backendpetshelter.DTO.BreedDTOResponse;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Repository.BreedRepository;
import org.pet.backendpetshelter.Repository.SpeciesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BreedService {

    private final BreedRepository breedRepository;
    private final SpeciesRepository speciesRepository;

    public BreedService(BreedRepository breedRepository, SpeciesRepository speciesRepository) {
        this.breedRepository = breedRepository;
        this.speciesRepository = speciesRepository;
    }


    /**
     * Add a new breed
     * @param request BreedDTORequest containing breed information
     * @return BreedDTOResponse
     */

    public BreedDTOResponse addBreed(BreedDTORequest request) {

        // Validate Input Data

        validateName(request.getName());
        validateSpeciesId(request.getSpeciesId());

        Species species = speciesRepository.findById(request.getSpeciesId())
                .orElseThrow(() -> new EntityNotFoundException("Species not found"));

        Breed breed = new Breed();
        breed.setName(request.getName());
        breed.setSpecies(species);

        breedRepository.save(breed);

        return new BreedDTOResponse(breed);

    }

    // Validation Methods

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Breed name cannot be null or empty.");
        }

        if (breedRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Breed with name '" + name + "' already exists.");
        }

        if (name.length() > 50) {
            throw new IllegalArgumentException("Breed name cannot exceed 50 characters.");
        }

        if (name.length() < 3) {
            throw new IllegalArgumentException("Breed name must be at least 3 characters long.");
        }

        if (!name.matches("^[a-zA-Z\\s'-]+$")) {
            throw new IllegalArgumentException("Breed name contains invalid characters.");
        }
    }

    private void validateSpeciesId(Long speciesId) {
        if (speciesId == null) {
            throw new IllegalArgumentException("Species ID cannot be null.");
        }


        if (speciesId <= 0) {
            throw new IllegalArgumentException("Species ID must be a positive number.");
        }


        if (!speciesRepository.existsById(speciesId)) {
            throw new IllegalArgumentException("Species ID does not exist.");
        }

    }



    /**
     * Get all breeds
     * @return List of BreedDTOResponse
     * This method retrieves all breeds from the repository and maps them to BreedDTOResponse.
     */
    public List<BreedDTOResponse> getAllBreeds() {
        return breedRepository.findAll().stream()
                .map(BreedDTOResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get specific breed
     * @param id the ID of the breed to be retrieved
     * @return BreedDTOResponse
     * This method retrieves a specific breed by ID and maps it to BreedDTOResponse.
     */
    public BreedDTOResponse getBreedById(Long id) {
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breed not found with id: " + id));
        return new BreedDTOResponse(breed);
    }

    /**
     * Get breeds by species
     * @param speciesId the ID of the species
     * @return List of BreedDTOResponse
     * This method retrieves all breeds for a specific species.
     */
    public List<BreedDTOResponse> getBreedsBySpecies(Long speciesId) {
        return breedRepository.findAll().stream()
                .filter(breed -> breed.getSpecies() != null && breed.getSpecies().getId().equals(speciesId))
                .map(BreedDTOResponse::new)
                .collect(Collectors.toList());
    }



    /**
     * Update breed
     * @param id the ID of the breed to be updated
     * @param request BreedUpdateDTO containing updated information
     * @return BreedDTOResponse
     */
    public BreedDTOResponse updateBreed(Long id, BreedDTORequest request) {
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breed not found with id: " + id));

        breed.setName(request.getName());

        breedRepository.save(breed);
        return new BreedDTOResponse(breed);
    }

    /**
     * Delete breed
     * @param id the ID of the breed to be deleted
     */
    public void deleteBreed(Long id) {
        if (!breedRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Breed not found with id: " + id);
        }
        breedRepository.deleteById(id);
    }
}