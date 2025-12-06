package org.pet.backendpetshelter.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.DTO.AnimalDTORequest;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Repository.AnimalRepository;
import org.pet.backendpetshelter.Repository.BreedRepository;
import org.pet.backendpetshelter.Repository.SpeciesRepository;
import org.pet.backendpetshelter.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Animal Integration Tests")
public class AnimalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private SpeciesRepository speciesRepository;

    @Autowired
    private BreedRepository breedRepository;

    private Species species;
    private Breed breed;

    @BeforeEach
    void setUp() {
        animalRepository.deleteAll();
        speciesRepository.deleteAll();
        breedRepository.deleteAll();

        Species species = new Species();
        species.setName("Dog");
        this.species = speciesRepository.save(species);

        Breed breed = new Breed();
        breed.setName("Labrador");
        breed.setSpecies(this.species);
        this.breed = breedRepository.save(breed);
    }

    private AnimalDTORequest createValidRequest() {
        AnimalDTORequest request = new AnimalDTORequest();
        request.setName("Buddy");
        request.setSpeciesId(species.getId());
        request.setBreedId(breed.getId());
        request.setSex("Male");
        request.setBirthDate(createPastDate(2020, 1, 1));
        request.setIntakeDate(createPastDate(2023, 1, 1));
        request.setStatus(Status.APPROVED);
        request.setPrice(499);
        request.setIsActive(true);
        request.setImageUrl("http://example.com/buddy.jpg");
        return request;
    }

    private Date createPastDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/animals Add Animal - Success")
    void addAnimal_Success() throws Exception {
        AnimalDTORequest request = createValidRequest();

        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.sex").value("Male"))
                .andExpect(jsonPath("$.birthDate").value(containsString("2019-12-31")))
        .andExpect(jsonPath("$.intakeDate").value(containsString("2022-12-31")))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.price").value(499))
                .andExpect(jsonPath("$.species.name").value("Dog"))
                .andExpect(jsonPath("$.breed.name").value("Labrador"));
    }
}
