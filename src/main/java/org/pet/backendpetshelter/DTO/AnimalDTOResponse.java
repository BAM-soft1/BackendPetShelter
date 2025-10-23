package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.Species;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AnimalDTOResponse {
    private String name;
    private Species species;
    private Date birthDate;
    private Date intakeDate;
    private int price;
    private Boolean isActive;


    public AnimalDTOResponse(Animal animal) {
        this.name = animal.getName();
        this.species = animal.getSpecies();
        this.birthDate = animal.getBirthDate();
        this.intakeDate = animal.getIntakeDate();
        this.price = animal.getPrice();
        this.isActive = animal.getIsActive();
    }
}
