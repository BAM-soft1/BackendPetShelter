package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Species;

import java.util.Date;

@Getter
@Setter
public class AnimalDTORequest {
    private String name;
    private Species species;
    private Date birthDate;
    private Date intakeDate;
    private int price;
    private Boolean isActive;
}
