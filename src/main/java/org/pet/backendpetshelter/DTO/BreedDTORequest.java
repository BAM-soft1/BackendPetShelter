package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Species;

@Getter
@Setter
public class BreedDTORequest {
    private Long speciesId;
    private String name;
}
