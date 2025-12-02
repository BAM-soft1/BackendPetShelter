package org.pet.backendpetshelter.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pet.backendpetshelter.DTO.AnimalDTORequest;
import org.pet.backendpetshelter.DTO.AnimalDTOResponse;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Reposiotry.AnimalRepository;
import org.pet.backendpetshelter.Reposiotry.BreedRepository;
import org.pet.backendpetshelter.Reposiotry.SpeciesRepository;
import org.pet.backendpetshelter.Service.AnimalService;

import java.util.Calendar;
import java.util.Date;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Animal Tests")
public class AnimalServiceTest {

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private BreedRepository breedRepository;


    @Mock
    private SpeciesRepository speciesRepository;

    @InjectMocks
    private AnimalService animalService;


private AnimalDTORequest animalDTORequest;

    // ==================== TEST HELPERS ====================


    private AnimalDTORequest createValidRequest() {
        AnimalDTORequest request = new AnimalDTORequest();
        request.setName("Ox");
        request.setSpecies(createValidSpecies());
        request.setBreed(createValidBreed());
        request.setSex("Male");
        request.setBirthDate(createPastDate(2020, 1, 1));
        request.setIntakeDate(createPastDate(2023, 1, 1));
        request.setStatus("Available");
        request.setPrice(499);
        request.setIsActive(true);
        request.setImageUrl("http://example.com/image.jpg");
        return request;
    }


        private Species createValidSpecies() {
            Species species = new Species();
            species.setId(1L);
            species.setName("Dog");
            return species;
        }

        private Breed createValidBreed() {
            Breed breed = new Breed();
            breed.setId(1L);
            breed.setName("Golden Retriever");
            return breed;
        }



    private Date createPastDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date createFutureDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }


    private Animal createSavedAnimal(AnimalDTORequest request) {
        Animal animal = new Animal();
        animal.setId(1L);
        animal.setName(request.getName());
        animal.setSpecies(request.getSpecies());
        animal.setBreed(request.getBreed());
        animal.setSex(request.getSex());
        animal.setBirthDate(request.getBirthDate());
        animal.setIntakeDate(request.getIntakeDate());
        animal.setStatus(request.getStatus());
        animal.setPrice(request.getPrice());
        animal.setIsActive(request.getIsActive());
        animal.setImageUrl(request.getImageUrl());
        return animal;
    }


    // ==================== BLACKBOX TESTS ====================

    // ----------------------------- Create Animal -----------------------------\\


@Nested
    @DisplayName("Create Animal Tests")
    class CreateAnimalTests {

    // ==================== VALID PARTITION ====================

    @Test
    @DisplayName("Create Animal - Valid Data")
    void createAnimal_ValidData_Success() {

        // Arrange
        AnimalDTORequest request = createValidRequest();


        when(animalRepository.save(any(Animal.class))).thenAnswer(inv -> {
            Animal a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        // Act
        AnimalDTOResponse response = animalService.addAnimal(request);

        // Assert
        assertNotNull(response);
        assertEquals("Ox", response.getName());
        assertEquals("Male", response.getSex());
        assertEquals("Available", response.getStatus());
        assertEquals(499, response.getPrice());
        verify(animalRepository).save(any(Animal.class));

        // ==================== INVALID PARTITIONS PARTITION ====================
    }

        @Test
        @DisplayName("Name is null - Throws Exception")
        void testCreateAnimalWithNullName() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setName("");


            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }



    }


}


