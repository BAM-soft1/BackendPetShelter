package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Entity.Species;

@Getter
@Setter
@NoArgsConstructor
public class BreedDTOResponse {
    private Long id;
    private String name;
    private Species species;


    public BreedDTOResponse(Breed breed) {
        this.id = breed.getId();
        this.name = breed.getName();
        this.species = breed.getSpecies();
    }

}
