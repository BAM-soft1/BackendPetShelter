package org.pet.backendpetshelter.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.DTO.BreedDTORequest;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Repository.BreedRepository;
import org.pet.backendpetshelter.Repository.SpeciesRepository;
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
@DisplayName("Breed Integration Tests")
public class BreedIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BreedRepository breedRepository;

    @Autowired
    private SpeciesRepository speciesRepository;


    Species species;

    @BeforeEach
    void setUp() {
        breedRepository.deleteAll();
        speciesRepository.deleteAll();

        Species species = new Species();
        species.setName("Dog");
        this.species = speciesRepository.save(species);
    }

    private BreedDTORequest createValidRequest() {
        BreedDTORequest request = new BreedDTORequest();
        request.setName("Labrador");
        request.setSpeciesId(this.species.getId());
        return request;
    }

    @Test
    @DisplayName("POST /api/breeds - Add Breed - Success")
    void testAddBreedSuccess() throws Exception {
        BreedDTORequest request = createValidRequest();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/breed/add")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.name").value("Labrador"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.species.name").value("Dog"));
    }

    @Test
    @DisplayName("POST /api/breeds - Add Breed - Duplicate Name")
    void testAddBreedDuplicateName() throws Exception {
        BreedDTORequest request = createValidRequest();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/breed/add")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/breed/add")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Breed with name 'Labrador' already exists."));
    }

    @Test
    @DisplayName("POST /api/breeds - Add Breed - Missing Name")
    void testAddBreedMissingName() throws Exception {
        BreedDTORequest request = new BreedDTORequest();
        request.setName("");
        request.setSpeciesId(this.species.getId());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/breed/add")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Breed name cannot be null or empty."));
    }

    @Test
    @DisplayName("POST /api/breeds - Add Breed - Null Name")
    void testAddBreedNullName() throws Exception {
        BreedDTORequest request = new BreedDTORequest();
        request.setName(null);
        request.setSpeciesId(this.species.getId());


        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/breed/add")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Breed name cannot be null or empty."));
    }


    @Test
    @DisplayName("POST /api/breeds - Add Breed - Invalid Characters in Name")
    void testAddBreedInvalidCharactersInName() throws Exception {
        BreedDTORequest request = new BreedDTORequest();
        request.setName("Labr@dor123");
        request.setSpeciesId(this.species.getId());


        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/breed/add")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Breed name contains invalid characters."));
    }


    @Test
    @DisplayName("POST /api/breeds - Add Breed - Name Too Long")
    void testAddBreedNameTooLong() throws Exception {
        BreedDTORequest request = new BreedDTORequest();
        request.setName("L".repeat(51));
        request.setSpeciesId(this.species.getId());


        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/breed/add")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Breed name cannot exceed 50 characters."));
    }

    @Test
    @DisplayName("POST /api/breeds - Add Breed - Name Too Short")
    void testAddBreedNameTooShort() throws Exception {
        BreedDTORequest request = new BreedDTORequest();
        request.setName("La");
        request.setSpeciesId(this.species.getId());


        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/breed/add")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Breed name must be at least 3 characters long."));
    }

   @Test
    @DisplayName("POST /api/breeds - Add Breed - Null Species ID")
    void testAddBreedNullSpecies() throws Exception {
        BreedDTORequest request = new BreedDTORequest();
        request.setName("Labrador");
        request.setSpeciesId(null);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/breed/add")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Species ID cannot be null."));
    }



    @Test
    @DisplayName("POST /api/breeds - Add Breed - Negative Species ID")
    void testAddBreedNegativeSpeciesId() throws Exception {

        BreedDTORequest request = new BreedDTORequest();
        request.setName("Labrador");
        request.setSpeciesId(-1L);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/breed/add")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Species ID must be a positive number."));
    }


    @Test
    @DisplayName("POST /api/breeds - Add Breed - Non-Existent Species ID")
    void testAddBreedNonExistentSpecies() throws Exception {

        BreedDTORequest request = new BreedDTORequest();
        request.setName("Labrador");
        request.setSpeciesId(9999L);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/breed/add")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Species ID does not exist."));
    }








}
