package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.AdoptionApplication;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.User;

import java.util.Date;


@Getter
@Setter
public class AdoptionRequest {
    private User user;
    private Animal animal;
    private AdoptionApplication adoptionApplication;
    private Date adoptionDate;
    private Boolean isActive;
}
