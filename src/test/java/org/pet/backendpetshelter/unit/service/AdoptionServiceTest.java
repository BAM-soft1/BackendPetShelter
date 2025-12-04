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
import org.pet.backendpetshelter.Entity.Adoption;
import org.pet.backendpetshelter.Entity.AdoptionApplication;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Reposiotry.AdoptionApplicationRepository;
import org.pet.backendpetshelter.Reposiotry.AdoptionRepository;
import org.pet.backendpetshelter.Reposiotry.AnimalRepository;
import org.pet.backendpetshelter.Service.AdoptionService;
import org.pet.backendpetshelter.Status;

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
    private AnimalRepository animalRepository;

    @Mock
    private AdoptionApplicationRepository adoptionApplicationRepository;


    @InjectMocks
    private AdoptionService adoptionService;


    private AdoptionRequest adoptionRequest;


    // ==================== TEST HELPERS ====================

    private AdoptionRequest createValidRequest(){
        AdoptionRequest request = new AdoptionRequest();
        request.setUserId(createValidRequest().getUserId());
        request.setAnimalId(createValidAnimal().getId());
        request.setApplicationId(createValidApplication().getId());
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
        return user;

    }

    private AdoptionApplication createValidApplication(){
        AdoptionApplication application = new AdoptionApplication();
        application.setId(1L);
        application.setUser(createValidUser());
        application.setStatus(Status.APPROVED);
        return application;
    }

    private Animal createValidAnimal(){
        Animal animal = new Animal();
        animal.setId(1L);
        animal.setName("Buddy");
        return animal;

    }

    private Date createPastDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
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

            AdoptionRequest request = createValidRequest();

            when(adoptionRepository.save(any(Adoption.class))).thenAnswer(inv -> {
                Adoption a = inv.getArgument(0);
                a.setId(1L);
                return a;
            });

            AdoptionResponse response = adoptionService.addAdoption(request);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            verify(adoptionRepository).save(any(Adoption.class));


        }
    }

}
