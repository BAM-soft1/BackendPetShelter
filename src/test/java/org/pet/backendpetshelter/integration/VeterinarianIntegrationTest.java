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
    @DisplayName("Add Veterinarian - Success")
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

}
