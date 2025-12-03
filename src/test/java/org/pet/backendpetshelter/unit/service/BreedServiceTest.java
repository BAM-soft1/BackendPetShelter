package org.pet.backendpetshelter.unit.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pet.backendpetshelter.DTO.BreedDTORequest;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Reposiotry.BreedRepository;
import org.pet.backendpetshelter.Reposiotry.SpeciesRepository;
import org.pet.backendpetshelter.Service.BreedService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Breed Tests")
public class BreedServiceTest {

    @Mock
    private BreedRepository breedRepository;

    @Mock
    private SpeciesRepository speciesRepository;

    @InjectMocks
    private BreedService breedService;


    private BreedDTORequest breedDTORequest;

    // ==================== TEST HELPERS ====================


    private BreedDTORequest createValidRequest() {
        BreedDTORequest request = new BreedDTORequest();
        request.setSpecies(createValidSpecies());
        request.setName("Labrador");

        return request;

    }

    private Species createValidSpecies() {
        Species species = new Species();
        species.setId(1L);
        species.setName("Dog");
        return species;
    }


    private Breed createValidBreed(BreedDTORequest request) {
        Breed breed = new Breed();
        breed.setId(1L);
        breed.setSpecies(request.getSpecies());
        breed.setName(request.getName());

        return breed;

    }


    // ==================== BLACKBOX TESTS ====================

    // ----------------------------- Create Breed -----------------------------\\

    @Nested
    @DisplayName("Create Breed Tests")
    void createBreed_ValidData_Success(){
        


    }


}
