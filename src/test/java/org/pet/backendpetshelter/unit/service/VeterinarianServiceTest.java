package org.pet.backendpetshelter.unit.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pet.backendpetshelter.DTO.VeterinarianDTORequest;
import org.pet.backendpetshelter.DTO.VeterinarianDTOResponse;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Entity.Veterinarian;
import org.pet.backendpetshelter.Repository.AnimalRepository;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.pet.backendpetshelter.Repository.VeterinarianRepository;
import org.pet.backendpetshelter.Service.VeterinarianService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Veterinarian Tests")
public class VeterinarianServiceTest {


    @Mock
    private VeterinarianRepository veterinarianRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private VeterinarianService veterinarianService;



    private VeterinarianDTORequest veterinarianDTORequest;

    // ==================== TEST HELPERS ====================


    private VeterinarianDTORequest createValidRequest(){
        VeterinarianDTORequest request = new VeterinarianDTORequest();
        request.setUser(createValidUser());
        request.setLicenseNumber("VET123456");
        request.setClinicName("Bam Pet Shelter");
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


    private Veterinarian createSavedVeterinarian(VeterinarianDTORequest request){
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setId(1L);
        veterinarian.setUser(request.getUser());
        veterinarian.setLicenseNumber(request.getLicenseNumber());
        veterinarian.setClinicName(request.getClinicName());
        veterinarian.setIsActive(request.getIsActive());
        return veterinarian;
    }


    // ==================== BLACKBOX TESTS ====================

    // ----------------------------- Create Veterinarian -----------------------------\\



    @Nested
    @DisplayName("Create Veterinarian Tests")


    class CreateVeterinarianTests {

        @Test
        @DisplayName("Create Veterinarian - Valid Data")
        void createVeterinarian_ValidData_Success() {
            VeterinarianDTORequest request = createValidRequest();

            when(veterinarianRepository.save(any(Veterinarian.class))).thenAnswer(inv -> {
                Veterinarian a = inv.getArgument(0);
                a.setId(1L);
                return a;
            });

            VeterinarianDTOResponse response = veterinarianService.addVeterinian(request);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals("VET123456", response.getLicenseNumber());
            assertEquals("Bam Pet Shelter", response.getClinicName());
            assertEquals(true, response.getIsActive());
            verify(veterinarianRepository).save(any(Veterinarian.class));


        }

        // ==================== INVALID PARTITIONS PARTITION ====================

        @Test
        @DisplayName("User ID is null - Throws Exception")
        void createVeterinarian_UserIdIsNull_ThrowsException() {
            VeterinarianDTORequest request = createValidRequest();
            request.getUser().setId(null);

            assertThrows(IllegalArgumentException.class, () -> veterinarianService.addVeterinian(request));
            verify(veterinarianRepository, never()).save(any(Veterinarian.class));

        }


        @Test
        @DisplayName("User is null - Throws Exception")
        void createVeterinarian_UserIsNull_ThrowsException() {
            VeterinarianDTORequest request = createValidRequest();
            request.setUser(null);

            assertThrows(IllegalArgumentException.class, () -> veterinarianService.addVeterinian(request));
            verify(veterinarianRepository, never()).save(any(Veterinarian.class));
        }




        @Test
        @DisplayName("LicenseNumber is null - Throws Exception")
        void createVeterinarian_LicenseNumberIsNull_ThrowsException() {
            VeterinarianDTORequest request = createValidRequest();
            request.setLicenseNumber(null);

            assertThrows(IllegalArgumentException.class, () -> veterinarianService.addVeterinian(request));
            verify(veterinarianRepository, never()).save(any(Veterinarian.class));

        }

        @Test
        @DisplayName("ClinicName is empty - Throws Exception")
        void createVeterinarian_ClinicNameIsEmpty_ThrowsException() {
            VeterinarianDTORequest request = createValidRequest();
            request.setClinicName("   ");

            assertThrows(IllegalArgumentException.class, () -> veterinarianService.addVeterinian(request));
            verify(veterinarianRepository, never()).save(any(Veterinarian.class));
        }

        @Test
        @DisplayName("isActive is null - Throws Exception")
        void createVeterinarian_IsActiveIsNull_ThrowsException() {
            VeterinarianDTORequest request = createValidRequest();
            request.setIsActive(null);

            assertThrows(IllegalArgumentException.class, () -> veterinarianService.addVeterinian(request));
            verify(veterinarianRepository, never()).save(any(Veterinarian.class));
        }



    }



}
