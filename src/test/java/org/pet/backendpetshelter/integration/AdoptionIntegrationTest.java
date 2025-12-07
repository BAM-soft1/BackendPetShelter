package org.pet.backendpetshelter.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.DTO.AdoptionRequest;
import org.pet.backendpetshelter.Entity.*;
import org.pet.backendpetshelter.Repository.*;
import org.pet.backendpetshelter.Roles;
import org.pet.backendpetshelter.Status;
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

import static org.hamcrest.Matchers.containsString;
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
    private BreedRepository breedRepository;

    @Autowired
    private SpeciesRepository speciesRepository;

    @Autowired
    private AdoptionApplicationRepository adoptionApplicationRepository;


    @BeforeEach
    public void setup() {
        adoptionRepository.deleteAll();
        userRepository.deleteAll();
        animalRepository.deleteAll();
        adoptionApplicationRepository.deleteAll();

        User user = new User();
        user.setFirstName("Ox");
        user.setLastName("W00");
        user.setEmail("ox@gmail.com");
        user.setPassword("secret");
        user.setRole(Roles.USER);
        user.setIsActive(true);
        userRepository.save(user);

        Species species = new Species();
        species.setName("Dog");
        species = speciesRepository.save(species);

        Breed breed = new Breed();
        breed.setName("Labrador");
        breed.setSpecies(species);
        breed = breedRepository.save(breed);

        Animal animal = new Animal();
        animal.setName("Buddy");
        animal.setSpecies(species);
        animal.setBreed(breed);
        animal.setSex("Male");
        animal.setBirthDate(Calendar.getInstance().getTime());
        animal.setIntakeDate(Calendar.getInstance().getTime());
        animal.setStatus(Status.APPROVED);
        animal.setPrice(499);
        animal.setIsActive(true);
        animalRepository.save(animal);

        AdoptionApplication app = new AdoptionApplication();
        app.setUser(user);
        app.setAnimal(animal);
        app.setApplicationDate(Calendar.getInstance().getTime());
        app.setStatus(Status.APPROVED);
        app.setIsActive(true);
        adoptionApplicationRepository.save(app);
    }

    private AdoptionRequest createValidRequest() {
        AdoptionRequest request = new AdoptionRequest();
        request.setUserId(userRepository.findAll().get(0).getId());
        request.setAnimalId(animalRepository.findAll().get(0).getId());
        request.setAdoptionApplicationId(adoptionApplicationRepository.findAll().get(0).getId());
        request.setAdoptionDate(new Date());
        request.setIsActive(true);
        return request;
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
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.userId").value(request.getUserId()))
                .andExpect(jsonPath("$.animalId").value(request.getAnimalId()))
                .andExpect(jsonPath("$.adoptionApplicationId").value(request.getAdoptionApplicationId()));


    }



    @Test
    @DisplayName("POST /api/adoptions - Add Adoption - Fail Null User ID")
    public void testAddAdoption_FailNullUserId() throws Exception {
        AdoptionRequest request = createValidRequest();
        request.setUserId(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/adoption/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User ID cannot be null")));
    }


    @Test
    @DisplayName("POST /api/adoptions - Add Adoption - Fail Null Animal ID")
    public void testAddAdoption_FailNullAnimalId() throws Exception {
        AdoptionRequest request = createValidRequest();
        request.setAnimalId(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/adoption/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Animal ID cannot be null")));
    }


    @Test
    @DisplayName("POST /api/adoptions - Add Adoption - Fail Null Adoption Application ID")
    public void testAddAdoption_FailNullAdoptionApplicationId() throws Exception {
        AdoptionRequest request = createValidRequest();
        request.setAdoptionApplicationId(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/adoption/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Adoption Application ID cannot be null")));

    }


    @Test
    @DisplayName("POST /api/adoptions - Add Adoption - Valid Adoption Date")
    public void testAddAdoption_FailNullAdoptionDate() throws Exception {
        AdoptionRequest request = createValidRequest();
        request.setAdoptionDate(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/adoption/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Adoption date cannot be null")));
    }


    @Test
    @DisplayName("POST /api/adoptions - Add Adoption - Adoption Date In The Past")
    public void testAddAdoption_FailAdoptionDateInThePast() throws Exception {
        AdoptionRequest request = createValidRequest();
        Calendar pastDate = Calendar.getInstance();
        pastDate.add(Calendar.DAY_OF_MONTH, -1);
        request.setAdoptionDate(pastDate.getTime());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/adoption/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Adoption date cannot be in the past.")));
    }



}
