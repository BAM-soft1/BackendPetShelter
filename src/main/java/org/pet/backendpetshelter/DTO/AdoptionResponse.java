package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Adoption;

import java.util.Date;

@Getter
@Setter
public class AdoptionResponse {
    private Long id;
    private Long userId;
    private Long animalId;
    private Long applicationId;
    private Date adoptionDate;
    private Boolean isActive;

    public AdoptionResponse(Adoption adoption) {
        this.id = adoption.getId();
        this.userId = adoption.getAdoptionUser().getId();
        this.animalId = adoption.getAnimal().getId();
        this.applicationId = adoption.getApplication().getId();
        this.adoptionDate = adoption.getAdoptionDate();
        this.isActive = adoption.getIsActive();
    }
}

