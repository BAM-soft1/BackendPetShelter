package org.pet.backendpetshelter.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.DTO.VeterinarianDTORequest;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.pet.backendpetshelter.Repository.VeterinarianRepository;
import org.pet.backendpetshelter.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Veterinarian Integration Tests")
public class VeterinarianIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VeterinarianRepository veterinarianRepository;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        veterinarianRepository.deleteAll();
        userRepository.deleteAll();


        User user = new User();
        user.setEmail("ox@gmail.com");
        user.setPassword("oxNyKodeIkLeakDen");
        user.setFirstName("Ox");
        user.setLastName("Woo");
        user.setPhone("1234567890");
        user.setIsActive(true);
        user.setRole(Roles.VETERINARIAN);
        userRepository.save(user);
    }

    private VeterinarianDTORequest createValidRequest() {
        VeterinarianDTORequest request = new VeterinarianDTORequest();
        request.setUserId(userRepository.findAll().get(0).getId());
        request.setLicenseNumber("VET123456");
        request.setClinicName("BAM Pet Shelter");
        request.setIsActive(true);
        return request;
    }


    @Test
    @DisplayName("POST /api/veterinarian Add Veterinarian - Success")
    void addVeterinarian_Success() throws Exception {
        VeterinarianDTORequest request = createValidRequest();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/veterinarian/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.clinicName").value("BAM Pet Shelter"))
                .andExpect(jsonPath("$.licenseNumber").value("VET123456"))
                .andExpect(jsonPath("$.isActive").value(true));


    }


    @Test
    @DisplayName("POST /api/veterinarian Add Veterinarian - User ID Cannot Be Null")
    void addVeterinarian_FailNullUserId() throws Exception {
        VeterinarianDTORequest request = createValidRequest();
        request.setUserId(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/veterinarian/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User ID cannot be null.")));
    }

    @Test
    @DisplayName("POST /api/veterinarian Add Veterinarian - Non-Existent User ID")
    void addVeterinarian_FailNonExistentUserId() throws Exception {
        VeterinarianDTORequest request = createValidRequest();
        request.setUserId(999L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/veterinarian/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User with ID 999 does not exist.")));
    }


    @Test
    @DisplayName("POST /api/veterinarian Add Veterinarian - Null License Number")
    void addVeterinarian_FailInvalidLicenseNumber() throws Exception {
        VeterinarianDTORequest request = createValidRequest();
        request.setLicenseNumber(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/veterinarian/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("License number cannot be null or empty.")));
    }

    @Test
    @DisplayName("POST /api/veterinarian Add Veterinarian - License Number Exceeds Max Length")
    void addVeterinarian_FailLicenseNumberExceedsMaxLength() throws Exception {
        VeterinarianDTORequest request = createValidRequest();
        request.setLicenseNumber("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/veterinarian/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("License number cannot exceed 20 characters.")));
    }

    @Test
    @DisplayName("POST /api/veterinarian Add Veterinarian - Invalid Characters in License")
    void addVeterinarian_FailInvalidCharactersInLicense() throws Exception {
        VeterinarianDTORequest request = createValidRequest();
        request.setLicenseNumber("VET@123!");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/veterinarian/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("License number contains invalid characters.")));
    }

    @Test
    @DisplayName("POST /api/veterinarian Add Veterinarian - Null Clinic Name")
    void addVeterinarian_FailNullClinicName() throws Exception {
        VeterinarianDTORequest request = createValidRequest();
        request.setClinicName(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/veterinarian/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Clinic name cannot be null or empty.")));
    }


    @Test
    @DisplayName("POST /api/veterinarian Add Veterinarian - Clinic Name Exceeds Max Length")
    void addVeterinarian_FailClinicNameExceedsMaxLength() throws Exception {
        VeterinarianDTORequest request = createValidRequest();
        request.setClinicName("A".repeat(70));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/veterinarian/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Clinic name cannot exceed 65 characters.")));
    }

    @Test
    @DisplayName("POST /api/veterinarian Add Veterinarian - Clinic Name Contains Invalid Characters")
    void addVeterinarian_FailClinicNameContainsInvalidCharacters() throws Exception {
        VeterinarianDTORequest request = createValidRequest();
        request.setClinicName("BAM Pet Shelter!@#");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/veterinarian/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Clinic name contains invalid characters.")));
    }


}





