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
import org.pet.backendpetshelter.Repository.AnimalRepository;
import org.pet.backendpetshelter.Repository.BreedRepository;
import org.pet.backendpetshelter.Repository.SpeciesRepository;
import org.pet.backendpetshelter.Service.AnimalService;
import org.pet.backendpetshelter.Status;

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
        request.setStatus(Status.APPROVED);
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


        // Happy path :D

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
            assertEquals(1L, response.getId());
            assertEquals("Ox", response.getName());
            assertEquals("Male", response.getSex());
            assertEquals("Available", response.getStatus());
            assertEquals(499, response.getPrice());
            verify(animalRepository).save(any(Animal.class));


        }

        // ==================== INVALID PARTITIONS PARTITION ====================


        // Name Invalid
        @Test
        @DisplayName("Name is null - Throws Exception")
        void testCreateAnimalWithNullName() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setName("");


            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }


        @Test
        @DisplayName("Name contains non-alphabetic characters - Throws Exception")
        void testCreateAnimalWithNonAlphabeticName() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setName("Ox-");

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        @Test
        @DisplayName("Name too Long - Throws Exception")
        void testCreateAnimalWithTooLongName() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setName("Oxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }


        // Species Invalid
        @Test
        @DisplayName("Species is null - Throws Exception")
        void testCreateAnimalWithNullSpecies() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setSpecies(null);

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        @Test
        @DisplayName("Species with null id - Throws Exception")
        void testCreateAnimalWithSpeciesNullId() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            Species species = createValidSpecies();
            species.setId(null);
            request.setSpecies(species);

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        @Test
        @DisplayName("Species with null name - Throws Exception")
        void testCreateAnimalWithSpeciesNullName() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            Species species = createValidSpecies();
            species.setName(null);
            request.setSpecies(species);

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }


        @Test
        @DisplayName("Species with empty name - Throws Exception")
        void testCreateAnimalWithSpeciesEmptyName() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            Species species = createValidSpecies();
            species.setName("");
            request.setSpecies(species);

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }



        @Test
        @DisplayName("Species with blank name - Throws Exception")
        void testCreateAnimalWithSpeciesBlankName() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            Species species = createValidSpecies();
            species.setName("   ");
            request.setSpecies(species);

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }



        // Breed Invalid
        @Test
        @DisplayName("Breed is null - Throws Exception")
        void testCreateAnimalWithNullBreed() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setBreed(null);

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        @Test
        @DisplayName("Breed with null id - Throws Exception")
        void testCreateAnimalWithBreedNullId() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            Breed breed = createValidBreed();
            breed.setId(null);
            request.setBreed(breed);

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        @Test
        @DisplayName("Breed with null name - Throws Exception")
        void testCreateAnimalWithBreedNullName() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            Breed breed = createValidBreed();
            breed.setName(null);
            request.setBreed(breed);

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }


        @Test
        @DisplayName("Breed with empty name - Throws Exception")
        void testCreateAnimalWithBreedEmptyName() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            Breed breed = createValidBreed();
            breed.setName("");
            request.setBreed(breed);

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        @Test
        @DisplayName("Breed with blank name - Throws Exception")
        void testCreateAnimalWithBreedBlankName() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            Breed breed = createValidBreed();
            breed.setName("   ");
            request.setBreed(breed);

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }


        // Sex Invalid
        @Test
        @DisplayName("Sex is null - Throws Exception")
        void testCreateAnimalWithNullSex() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setSex(null);

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        @Test
        @DisplayName("Sex is empty - Throws Exception")
        void testCreateAnimalWithEmptySex() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setSex("");
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        @Test
        @DisplayName("Sex must be Male or Female - Throws Exception")
        void testCreateAnimalWithInvalidSex() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setSex("Unknown");
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }




        // Birthdate Invalid

        @Test
        @DisplayName("Birthdate is null - Throws Exception")
        void testCreateAnimalWithNullBirthdate() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setBirthDate(null);
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }


        @Test
        @DisplayName("Birthdate is in the future - Throws Exception")
        void testCreateAnimalWithFutureBirthdate() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setBirthDate(createFutureDate());

            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }


        // IntakeDate Invalid
        @Test
        @DisplayName("IntakeDate is null - Throws Exception")
        void testCreateAnimalWithNullIntakeDate() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setIntakeDate(null);
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        @Test
        @DisplayName("IntakeDate is in the future - Throws Exception")
        void testCreateAnimalWithFutureIntakeDate() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setIntakeDate(createFutureDate());
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        // Status Invalid
        @Test
        @DisplayName("Status is null - Throws Exception")
        void testCreateAnimalWithNullStatus() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setStatus(null);
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        /*

        @Test
        @DisplayName("Status is empty - Throws Exception")
        void testCreateAnimalWithEmptyStatus() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setStatus("");
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        @Test
        @DisplayName("Status is invalid - Throws Exception")
        void testCreateAnimalWithInvalidStatus() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setStatus("Unknown");
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        @Test
        @DisplayName("Should throw exception when status is invalid - 'PendingApproval'")
        void testCreateAnimalWithInvalidStatusPendingApproval() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setStatus(Status.PENDINGAPPROVAL);
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }


         */


        // Price Invalid
        @Test
        @DisplayName("Price is negative - Throws Exception")
        void testCreateAnimalWithNegativePrice() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setPrice(-100);
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        @Test
        @DisplayName("Price exceeds maximum - Throws Exception")
        void testCreateAnimalWithExcessivePrice() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setPrice(50000);
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        // isActive Invalid
        @Test
        @DisplayName("isActive is null - Throws Exception")
        void testCreateAnimalWithNullIsActive() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setIsActive(null);
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        // ImageUrl Invalid
        @Test
        @DisplayName("ImageUrl is null - Throws Exception")
        void testCreateAnimalWithNullImageUrl() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setImageUrl(null);
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }


        @Test
        @DisplayName("ImageUrl is empty - Throws Exception")
        void testCreateAnimalWithEmptyImageUrl() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setImageUrl("");
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }


        @Test
        @DisplayName("ImageUrl is invalid - Throws Exception")
        void testCreateAnimalWithInvalidImageUrl() {
            // Arrange
            AnimalDTORequest request = createValidRequest();
            request.setImageUrl("ftp://cutedog.com/image.jpg");
            assertThrows(IllegalArgumentException.class, () -> animalService.addAnimal(request));
            verify(animalRepository, never()).save(any(Animal.class));
        }

        }


    }



