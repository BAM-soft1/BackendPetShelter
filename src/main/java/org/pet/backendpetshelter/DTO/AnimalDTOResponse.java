package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Entity.Species;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AnimalDTOResponse {
    private Long id;
    private String name;
    private String sex;
    private Species species;
    private Breed breed;
    private Date birthDate;
    private Date intakeDate;
    private String status;
    private int price;
    private Boolean isActive;
    private String imageUrl;


    public AnimalDTOResponse(Animal animal) {
        this.id = animal.getId();
        this.name = animal.getName();
        this.sex = animal.getSex();
        this.species = animal.getSpecies();
        this.breed = animal.getBreed();
        this.birthDate = animal.getBirthDate();
        this.intakeDate = animal.getIntakeDate();
        this.status = animal.getStatus();
        this.price = animal.getPrice();
        this.isActive = animal.getIsActive();
        this.imageUrl = animal.getImageUrl();
    }
}
