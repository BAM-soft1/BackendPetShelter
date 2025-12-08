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


    @Test
    @DisplayName("POST /api/animals Add Animal - Missing Name")
    void addAnimal_MissingName() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setName(null);

        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Name cannot be null or empty")));


    }

    @Test
    @DisplayName("POST /api/animals Add Animal - Name Contains Non-Alphabetic Characters")
    void addAnimal_InvalidName() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setName("Ox1!");
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Name must contain at least one non-alphabetic character")));
    }

    @Test
    @DisplayName("POST /api/animals Add Animal - Name Too Short")
    void addAnimal_NameTooShort() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setName("O");
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Name must be between 2 and 30 characters long")));
    }

    @Test
    @DisplayName("POST /api/animals Add Animal - Name Too Long")
    void addAnimal_NameTooLong() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setName("Ox".repeat(30));
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Name must be between 2 and 30 characters long")));
    }


    @Test
    @DisplayName("POST /api/animals Add Animal - Invalid Sex")
    void addAnimal_InvalidSex() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setSex("Unknown");
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Sex must be either 'Male' or 'Female'")));
    }


    @Test
    @DisplayName("POST /api/animals Add Animal - Future Birth Date")
    void addAnimal_FutureBirthDate() throws Exception {
        AnimalDTORequest request = createValidRequest();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        request.setBirthDate(cal.getTime());
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Birthdate cannot be in the future")));
    }


    @Test
    @DisplayName("POST /api/animals Add Animal - Intake Date Before Birth Date")
    void addAnimal_IntakeDateBeforeBirthDate() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setIntakeDate(createPastDate(2019, 1, 1));
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Intake date cannot be before birth date")));
    }


    @Test
    @DisplayName("POST /api/animals Add Animal - Future Intake Date")
    void addAnimal_FutureIntakeDate() throws Exception {
        AnimalDTORequest request = createValidRequest();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        request.setIntakeDate(cal.getTime());
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Intake date cannot be in the future")));
    }


    @Test
    @DisplayName("POST /api/animals Add Animal - Null Intake Date")
    void addAnimal_NullIntakeDate() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setIntakeDate(null);
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Intake date cannot be null")));
    }



    @Test
    @DisplayName("POST /api/animals Add Animal - Missing Species ID")
    void addAnimal_MissingSpeciesId() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setSpeciesId(null);
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Species ID cannot be null")));
    }

    @Test
    @DisplayName("POST /api/animals Add Animal - Negative Species ID")
    void addAnimal_NegativeSpeciesId() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setSpeciesId(-5L);
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Species ID must be a positive number")));
    }

    @Test
    @DisplayName("POST /api/animals Add Animal - Species ID does not exist")
    void addAnimal_NonExistentSpeciesId() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setSpeciesId(9999L);
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Species ID does not exist")));
    }



    @Test
    @DisplayName("POST /api/animals Add Animal - Missing Breed ID")
    void addAnimal_MissingBreedId() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setBreedId(null);
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Breed ID cannot be null")));
    }

    @Test
    @DisplayName("POST /api/animals Add Animal - Negative Breed ID")
    void addAnimal_NegativeBreedId() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setBreedId(-3L);
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Breed ID must be a positive number")));
    }

    @Test
    @DisplayName("POST /api/animals Add Animal - Breed ID does not exist")
    void addAnimal_NonExistentBreedId() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setBreedId(8888L);
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Breed ID does not exist")));

    }

    @Test
    @DisplayName("POST /api/animals Add Animal - Negative Price")
    void addAnimal_NegativePrice() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setPrice(-1);
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Price cannot be negative")));
    }


    @Test
    @DisplayName("POST /api/animals Add Animal - Price Exceeds Maximum")
    void addAnimal_PriceExceedsMaximum() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setPrice(30001);
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Price cannot exceed 30000")));
    }


    @Test
    @DisplayName("POST /api/animals Add Animal - Missing Status")
    void addAnimal_MissingStatus() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setStatus(null);
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Status cannot be null")));
    }

    @Test
    @DisplayName("POST /api/animals Add Animal - Missing IsActive")
    void addAnimal_MissingIsActive() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setIsActive(null);
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("isActive cannot be null")));
    }

    @Test
    @DisplayName("POST /api/animals Add Animal - Invalid Image URL")
    void addAnimal_InvalidImageUrl() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setImageUrl("invalid_url");
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid image URL format")));
    }


    @Test
    @DisplayName("POST /api/animals Add Animal - Missing Image URL")
    void addAnimal_MissingImageUrl() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setImageUrl(null);
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Image URL cannot be null or empty")));
    }



    @Test
    @DisplayName("POST /api/animals Add Animal - Empty Image URL")
    void addAnimal_EmptyImageUrl() throws Exception {
        AnimalDTORequest request = createValidRequest();
        request.setImageUrl("   ");
        mockMvc.perform(post("/api/animal/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Image URL cannot be null or empty")));
    }
}