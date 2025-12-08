package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Status;

import java.util.Date;

@Getter
@Setter
public class AnimalDTORequest {

    private String name;
    private Long speciesId;
    private Long breedId;
    private String sex;
    private Date birthDate;
    private Date intakeDate;
    private Status status;
    private int price;
    private Boolean isActive;
    private String imageUrl;
}
