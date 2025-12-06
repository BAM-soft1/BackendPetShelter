package org.pet.backendpetshelter.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.DTO.SpeciesDTORequest;
import org.pet.backendpetshelter.Repository.SpeciesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Species Integration Tests")
public class SpeciesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpeciesRepository speciesRepository;


    @BeforeEach
    void setUp() {
        speciesRepository.deleteAll();
    }

    private SpeciesDTORequest createValidRequest() {
        SpeciesDTORequest request = new SpeciesDTORequest();
        request.setName("Dog");
        return request;
    }


    @Test
    @DisplayName("POST /api/species - Add Species - Success")
    void testAddSpeciesSuccess() throws Exception {
        SpeciesDTORequest request = createValidRequest();

        mockMvc.perform(post("/api/species/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Dog"));
    }



    @Test
    @DisplayName("POST /api/species - Add Species - Duplicate Name")
    void testAddSpeciesDuplicateName() throws Exception {
        SpeciesDTORequest request = createValidRequest();


        mockMvc.perform(post("/api/species/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());


        mockMvc.perform(post("/api/species/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Species with name 'Dog' already exists."));
    }

    @Test
    @DisplayName("POST /api/species - Add Species - Missing Name")
    void testAddSpeciesMissingName() throws Exception {
        SpeciesDTORequest request = new SpeciesDTORequest();
        request.setName("");

        mockMvc.perform(post("/api/species/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Species name cannot be null or empty."));
    }


    @Test
    @DisplayName("POST /api/species - Add Species - Null Name")
    void testAddSpeciesNullName() throws Exception {
        SpeciesDTORequest request = new SpeciesDTORequest();
        request.setName(null);

        mockMvc.perform(post("/api/species/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Species name cannot be null or empty."));
    }


    @Test
    @DisplayName("POST /api/species - Add Species - Whitespace Name")
    void testAddSpeciesWhitespaceName() throws Exception {
        SpeciesDTORequest request = new SpeciesDTORequest();
        request.setName("   ");

        mockMvc.perform(post("/api/species/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Species name cannot be null or empty."));
    }

    @Test
    @DisplayName("POST /api/species - Add Species - Very Long Name")
    void testAddSpeciesVeryLongName() throws Exception {
        SpeciesDTORequest request = new SpeciesDTORequest();
        String longName = "A".repeat(50);
        request.setName(longName);

        mockMvc.perform(post("/api/species/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(longName));
    }

    @Test
    @DisplayName("POST /api/species - Add Species - Name Too Short")
    void testAddSpeciesNameTooShort() throws Exception {
        SpeciesDTORequest request = new SpeciesDTORequest();
        request.setName("A");

        mockMvc.perform(post("/api/species/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Species name must be at least 2 characters long."));
    }

    @Test
    @DisplayName("POST /api/species - Add Species - Invalid Characters in Name")
    void testAddSpeciesInvalidCharactersInName() throws Exception {
        SpeciesDTORequest request = new SpeciesDTORequest();
        request.setName("Dog123!");

        mockMvc.perform(post("/api/species/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Species name contains invalid characters."));
    }

}
