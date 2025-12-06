package org.pet.backendpetshelter.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.pet.backendpetshelter.Entity.AdoptionApplication;
import org.pet.backendpetshelter.Repository.AdoptionApplicationRepository;
import org.pet.backendpetshelter.Repository.AdoptionRepository;
import org.pet.backendpetshelter.Repository.AnimalRepository;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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







}
