package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Breed;

@Getter
@Setter
@NoArgsConstructor
public class BreedDTOResponse {
    private Long id;
    private String name;


    public BreedDTOResponse(Breed breed) {
        this.id = breed.getId();
        this.name = breed.getName();
    }

}
