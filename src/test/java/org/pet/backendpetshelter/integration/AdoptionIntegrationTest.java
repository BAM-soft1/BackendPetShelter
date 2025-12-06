package org.pet.backendpetshelter.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.DTO.AdoptionRequest;
import org.pet.backendpetshelter.Repository.AdoptionApplicationRepository;
import org.pet.backendpetshelter.Repository.AdoptionRepository;
import org.pet.backendpetshelter.Repository.AnimalRepository;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Adoption Integration Tests")
public class AdoptionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdoptionRepository adoptionRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private AdoptionApplicationRepository adoptionApplicationRepository;


    @BeforeEach
    public void setup() {
        adoptionRepository.deleteAll();
        userRepository.deleteAll();
        animalRepository.deleteAll();
        adoptionApplicationRepository.deleteAll();
    }


    private AdoptionRequest createValidRequest() {
        AdoptionRequest request = new AdoptionRequest();

        request.setUserId(1L);
        request.setAnimalId(1L);
        request.setAdoptionApplicationId(1L);
        request.setAdoptionDate(new java.util.Date("2024-01-01"));
        request.setIsActive(true);
        return request;

    }

    private Date createPastDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date createFutureDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    // ==================== POST TEST ROUTES ====================

    @Test
    @DisplayName("POST /api/adoptions - Add Adoption - Success")
    public void testAddAdoption_Success() throws Exception {

        AdoptionRequest request = createValidRequest();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/adoption/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(request.getUserId()))
                .andExpect(jsonPath("$.animalId").value(request.getAnimalId()))
                .andExpect(jsonPath("$.adoptionApplicationId").value(request.getAdoptionApplicationId()))
                .andExpect(jsonPath("$.adoptionDate").value(request.getAdoptionDate().toString()))
                .andExpect(jsonPath("$.isActive").value(request.getIsActive()));


    }




}
