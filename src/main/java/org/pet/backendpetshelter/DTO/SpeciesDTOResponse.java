package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Species;

@Getter
@Setter
@NoArgsConstructor
public class SpeciesDTOResponse {
    private Long id;
    private String name;

    public SpeciesDTOResponse(Species species) {
        this.id = species.getId();
        this.name = species.getName();
    }
}
