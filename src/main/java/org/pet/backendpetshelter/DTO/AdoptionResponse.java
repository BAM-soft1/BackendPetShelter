package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Adoption;
import org.pet.backendpetshelter.Entity.AdoptionApplication;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.User;

import java.util.Date;

@Getter
@Setter
public class AdoptionResponse {
    private Long id;
    private User user;
    private Animal animal;
    private AdoptionApplication adoptionApplication;
    private Date adoptionDate;
    private Boolean isActive;

    public AdoptionResponse(Adoption adoption) {
        this.id = adoption.getId();
        this.user = adoption.getAdoptionUser();
        this.animal = adoption.getAnimal();
        this.adoptionApplication = adoption.getApplication();
        this.adoptionDate = adoption.getAdoptionDate();
        this.isActive = adoption.getIsActive();
    }
}

