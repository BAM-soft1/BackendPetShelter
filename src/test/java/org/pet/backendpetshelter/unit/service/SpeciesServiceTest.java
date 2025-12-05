package org.pet.backendpetshelter.unit.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pet.backendpetshelter.DTO.SpeciesDTORequest;
import org.pet.backendpetshelter.DTO.SpeciesDTOResponse;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Repository.SpeciesRepository;
import org.pet.backendpetshelter.Service.SpeciesService;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Species Tests")
public class SpeciesServiceTest {

    @Mock
    private SpeciesRepository speciesRepository;

    @InjectMocks
    private SpeciesService speciesService;

    // ==================== TEST HELPERS ====================
private SpeciesDTORequest createValidRequest() {
    SpeciesDTORequest request = new SpeciesDTORequest();
    request.setName("Cat");

    return request;
}

private Species createSavedSpecies(SpeciesDTORequest request) {
    Species species = new Species();
    species.setId(1L);
    species.setName(request.getName());

    return species;
}


    // ==================== BLACKBOX TESTS ====================

    // ----------------------------- Create Species -----------------------------\\


    @Nested
    @DisplayName("Create Species Tests")
    class CreateSpeciesTests {


    @Test
    @DisplayName("Create Species - Valid Request")
    void createSpecies_ValidRequest_SpeciesCreated() {

        SpeciesDTORequest request = createValidRequest();

        when(speciesRepository.save(any(Species.class))).thenAnswer(inv -> {
            Species a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        SpeciesDTOResponse response = speciesService.addSpecies(request);

        assertNotNull(response);
        assertEquals("Cat", response.getName());
        verify(speciesRepository).save(any(Species.class));
    }

        // ==================== INVALID PARTITIONS PARTITION ====================

        @Test
        @DisplayName("Name is null - Throws Exception")
        void testCreateSpeciesWithNullName() {

        SpeciesDTORequest request = createValidRequest();
        request.setName(null);

        assertThrows(IllegalArgumentException.class, () -> speciesService.addSpecies(request));
            verify(speciesRepository, never()).save(any(Species.class));



        }


    }

}
