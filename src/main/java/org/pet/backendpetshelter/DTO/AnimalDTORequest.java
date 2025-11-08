package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Entity.Species;

import java.util.Date;

@Getter
@Setter
public class AnimalDTORequest {
    private String name;
    private Species species;
    private Breed breed;
    private String sex;
    private Date birthDate;
    private Date intakeDate;
    private String status;
    private int price;
    private Boolean isActive;
    private String imageUrl;
}
