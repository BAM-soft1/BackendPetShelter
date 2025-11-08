package org.pet.backendpetshelter.Service;

import jakarta.persistence.EntityNotFoundException;
import org.pet.backendpetshelter.DTO.BreedDTORequest;
import org.pet.backendpetshelter.DTO.BreedDTOResponse;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Reposiotry.BreedRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BreedService {

    private final BreedRepository breedRepository;

    public BreedService(BreedRepository breedRepository) {
        this.breedRepository = breedRepository;
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
     * Add a new breed
     * @param request BreedDTORequest containing breed information
     * @return BreedDTOResponse
     */
    public BreedDTOResponse addBreed(BreedDTORequest request) {
        if (breedRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Breed already exists: " + request.getName());
        }

        Breed newBreed = new Breed();
        newBreed.setName(request.getName());
        newBreed.setSpecies(request.getSpecies());

        breedRepository.save(newBreed);
        return new BreedDTOResponse(newBreed);
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