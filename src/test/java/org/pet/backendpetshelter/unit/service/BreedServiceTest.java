package org.pet.backendpetshelter.unit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pet.backendpetshelter.DTO.BreedDTORequest;
import org.pet.backendpetshelter.DTO.BreedDTOResponse;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Repository.BreedRepository;
import org.pet.backendpetshelter.Repository.SpeciesRepository;
import org.pet.backendpetshelter.Service.BreedService;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

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
        request.setName("Alfred");

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
    class CreateBreedTests {


        @Test
        @DisplayName("Create Breed - Valid Data")
        void createBreed_ValidData_Success() {
            BreedDTORequest request = createValidRequest();


            when(breedRepository.save(any(Breed.class))).thenAnswer(inv -> {
                Breed a = inv.getArgument(0);
                a.setId(1L);
                return a;
            });


            BreedDTOResponse response = breedService.addBreed(request);

            assertNotNull(response);
            assertEquals("Breed name should match", "Alfred", response.getName());
            assertEquals("Breed species should match", request.getSpecies().getId(), 1L);

        }

        // ==================== INVALID PARTITIONS PARTITION ====================

        @Test
        @DisplayName("Name is null - Throws Exception")
        void createBreed_NameIsNull_ThrowsException() {

            // Arrange
            BreedDTORequest request = createValidRequest();
            request.setName(null);

            assertThrows(IllegalArgumentException.class, () -> breedService.addBreed(request));
            verify(breedRepository, never()).save(any(Breed.class));

        }


        @Test
        @DisplayName("Name is empty - Throws Exception")
        void createBreed_NameIsEmpty_ThrowsException() {
            // Arrange
            BreedDTORequest request = createValidRequest();
            request.setName("   ");

            assertThrows(IllegalArgumentException.class, () -> breedService.addBreed(request));
            verify(breedRepository, never()).save(any(Breed.class));
        }


        @Test
        @DisplayName("Species is null - Throws Exception")
        void createBreed_SpeciesIsNull_ThrowsException() {
            // Arrange
            BreedDTORequest request = createValidRequest();
            request.setSpecies(null);
            assertThrows(IllegalArgumentException.class, () -> breedService.addBreed(request));
            verify(breedRepository, never()).save(any(Breed.class));
        }


    }
}