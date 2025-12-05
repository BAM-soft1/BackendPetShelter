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
import org.pet.backendpetshelter.Roles;
import org.pet.backendpetshelter.Service.AdoptionService;
import org.pet.backendpetshelter.Status;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

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
        request.setUser(createValidUser());
        request.setAnimal(createValidAnimal());
        request.setAdoptionApplication(createValidApplication());
        request.setAdoptionDate(createPastDate(2023, 9, 15));
        request.setIsActive(true);
        return request;
    }


    private User createValidUser(){
        User user = new User();
        user.setId(1L);
        user.setFirstName("Ox");
        user.setLastName("W00");
        user.setEmail("ox@gmail.com");
        user.setPassword("W1ldC4tWoo123");
        user.setPhone("42424242");
        user.setRole(Roles.USER);
        user.setIsActive(true);

        return user;

    }

    private AdoptionApplication createValidApplication(){
        AdoptionApplication application = new AdoptionApplication();
        application.setId(1L);
        application.setUser(createValidUser());
        application.setStatus(Status.APPROVED);
        return application;
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

    private Animal createValidAnimal(){
        Animal animal = new Animal();
        animal.setId(1L);
        animal.setName("Buddy");
        animal.setSpecies(createValidSpecies());
        animal.setBreed(createValidBreed());
        animal.setBirthDate(Calendar.getInstance().getTime());
        animal.setSex("Male");
        animal.setIntakeDate(Calendar.getInstance().getTime());
        animal.setStatus(Status.APPROVED);
        animal.setPrice(499);
        animal.setIsActive(true);
        animal.setImageUrl("http://cuteDog.com/LabadrorDog.jpg");
        return animal;

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
            verify(adoptionRepository).save(any(Adoption.class));
        }



        // ==================== INVALID PARTITIONS PARTITION ====================

        @Test
        @DisplayName("Create Adoption - Null User")
        void createAdoption_NullUser_ThrowsException() {
            AdoptionRequest request = createValidRequest();
            request.setUser(null);

            try {
                adoptionService.addAdoption(request);
            } catch (IllegalArgumentException e) {
                assertEquals("User cannot be null", e.getMessage());
            }
            verify(adoptionRepository,  org.mockito.Mockito.never()).save(any(Adoption.class));

        }


        @Test
        @DisplayName("Create Adoption - Null Animal")
        void createAdoption_NullAnimal_ThrowsException() {
            AdoptionRequest request = createValidRequest();
            request.setAnimal(null);

            try {
                adoptionService.addAdoption(request);
            } catch (IllegalArgumentException e) {
                assertEquals("Animal cannot be null", e.getMessage());
            }
            verify(adoptionRepository,  org.mockito.Mockito.never()).save(any(Adoption.class));
        }


        @Test
        @DisplayName("Create Adoption - Null Adoption Application")
        void createAdoption_NullAdoptionApplication_ThrowsException() {
            AdoptionRequest request = createValidRequest();
            request.setAdoptionApplication(null);

            try {
                adoptionService.addAdoption(request);
            } catch (IllegalArgumentException e) {
                assertEquals("Adoption Application cannot be null", e.getMessage());
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
            request.getUser().setId(null);
            try {
                adoptionService.addAdoption(request);
            } catch (IllegalArgumentException e) {
                assertEquals("User cannot be null", e.getMessage());
            }
            verify(adoptionRepository, org.mockito.Mockito.never()).save(any(Adoption.class));
        }
        
    }

}
