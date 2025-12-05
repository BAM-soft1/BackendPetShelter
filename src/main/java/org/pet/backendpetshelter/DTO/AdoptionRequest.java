package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class AdoptionRequest {
    private Long userId;
    private Long animalId;
    private Long applicationId;
    private Date adoptionDate;
    private Boolean isActive;
}
