package org.pet.backendpetshelter.unit.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pet.backendpetshelter.DTO.AdoptionRequest;
import org.pet.backendpetshelter.DTO.AdoptionResponse;
import org.pet.backendpetshelter.Entity.*;
import org.pet.backendpetshelter.Repository.*;
import org.pet.backendpetshelter.Service.AdoptionService;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Adoption Tests")
public class AdoptionServiceTest {


   @Mock
   private AdoptionRepository adoptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private BreedRepository breedRepository;

    @Mock SpeciesRepository speciesRepository;


    @Mock
    private AdoptionApplicationRepository adoptionApplicationRepository;


    @InjectMocks
    private AdoptionService adoptionService;


    private AdoptionRequest adoptionRequest;


    // ==================== TEST HELPERS ====================

    private AdoptionRequest createValidRequest() {
        AdoptionRequest request = new AdoptionRequest();
        request.setUserId(createValidUserId());
        request.setAnimalId(createValidAnimalId());
        request.setAdoptionApplicationId(createValidApplicationId());
        request.setAdoptionDate(new Date());
        request.setIsActive(true);
        return request;
    }



    private Long createValidUserId() {
        return 1L;    }

    private Long createValidAnimalId() {
        return 1L;
    }

    private Long createValidApplicationId() {
        return 1L;
    }


    private Species createValidSpecies(){
        Species species = new Species();
        species.setId(1L);
        species.setName("Dog");
        return species;
    }

    private Breed createValidBreed(){
        Breed breed = new Breed();
        breed.setId(1L);
        breed.setName("Labrador");
        breed.setSpecies(createValidSpecies());
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

    // ==================== BLACKBOX TESTS ====================

    // ----------------------------- Create Adoption -----------------------------\\


    @Nested
    @DisplayName("Create Adoption Tests")
    class CreateAdoptionTests {

        @Test
        @DisplayName("Create Adoption - Valid Request")
        void createAdoption_ValidRequest_Success() {
            // Arrange
            AdoptionRequest request = createValidRequest();
            User user = new User();
            user.setId(request.getUserId());
            Animal animal = new Animal();
            animal.setId(request.getAnimalId());
            AdoptionApplication application = new AdoptionApplication();
            application.setId(request.getAdoptionApplicationId());

            when(userRepository.findById(request.getUserId())).thenReturn(java.util.Optional.of(user));
            when(animalRepository.findById(request.getAnimalId())).thenReturn(java.util.Optional.of(animal));
            when(adoptionApplicationRepository.findById(request.getAdoptionApplicationId())).thenReturn(java.util.Optional.of(application));





            when(adoptionRepository.save(any(Adoption.class))).thenAnswer(inv -> {
                Adoption a = inv.getArgument(0);
                a.setId(1L);
                return a;
            });

            // Act
            AdoptionResponse response = adoptionService.addAdoption(request);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(user.getId(), response.getUserId());
            assertEquals(animal.getId(), response.getAnimalId());
            assertEquals(application.getId(), response.getAdoptionApplicationId());

            assertEquals(request.getAdoptionDate(), response.getAdoptionDate());
            assertEquals(request.getIsActive(), response.getIsActive());

            verify(adoptionRepository).save(any(Adoption.class));
        }



        // ==================== INVALID PARTITIONS PARTITION ====================

        @Test
        @DisplayName("Create Adoption - Null User ID")
        void createAdoption_NullUserId_ThrowsException() {
            AdoptionRequest request = createValidRequest();
            request.setUserId(null);

            try {
                adoptionService.addAdoption(request);
            } catch (IllegalArgumentException e) {
                assertEquals("User ID cannot be null", e.getMessage());
            }
            verify(adoptionRepository,  org.mockito.Mockito.never()).save(any(Adoption.class));

        }


        @Test
        @DisplayName("Create Adoption - Null Animal")
        void createAdoption_NullAnimal_ThrowsException() {
            AdoptionRequest request = createValidRequest();
            request.setAnimalId(null);

            try {
                adoptionService.addAdoption(request);
            } catch (IllegalArgumentException e) {
                assertEquals("Animal ID cannot be null", e.getMessage());
            }
            verify(adoptionRepository,  org.mockito.Mockito.never()).save(any(Adoption.class));
        }


        @Test
        @DisplayName("Create Adoption - Null Adoption Application")
        void createAdoption_NullAdoptionApplication_ThrowsException() {
            AdoptionRequest request = createValidRequest();
            request.setAdoptionApplicationId(null);

            try {
                adoptionService.addAdoption(request);
            } catch (IllegalArgumentException e) {
                assertEquals("Adoption Application ID cannot be null", e.getMessage());
            }
            verify(adoptionRepository,  org.mockito.Mockito.never()).save(any(Adoption.class));
        }

        @Test
        @DisplayName("Create Adoption - Null Adoption Date")
        void createAdoption_NullAdoptionDate_ThrowsException() {
            AdoptionRequest request = createValidRequest();
            request.setAdoptionDate(null);
            try {
                adoptionService.addAdoption(request);
            } catch (IllegalArgumentException e) {
                assertEquals("Adoption date cannot be null", e.getMessage());
            }
            verify(adoptionRepository, org.mockito.Mockito.never()).save(any(Adoption.class));

        }


        @Test
        @DisplayName("Create Adoption - Null IsActive")
        void createAdoption_NullIsActive_ThrowsException() {
            AdoptionRequest request = createValidRequest();
            request.setIsActive(null);
            try {
                adoptionService.addAdoption(request);
            } catch (IllegalArgumentException e) {
                assertEquals("IsActive cannot be null", e.getMessage());
            }
            verify(adoptionRepository, org.mockito.Mockito.never()).save(any(Adoption.class));
        }

        @Test
        @DisplayName("Create Adoption - User with null ID")
        void createAdoption_UserWithNullId_ThrowsException() {
            AdoptionRequest request = createValidRequest();
            request.setUserId(null);
            try {
                adoptionService.addAdoption(request);
            } catch (IllegalArgumentException e) {
                assertEquals("User ID cannot be null", e.getMessage());
            }
            verify(adoptionRepository, org.mockito.Mockito.never()).save(any(Adoption.class));
        }

    }

}
