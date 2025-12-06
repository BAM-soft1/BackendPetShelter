package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Status;

import java.util.Date;


@Getter
@Setter
public class AdoptionApplicationRequest {
    private Long userId;
    private Long animalId;
    private Date applicationDate;
    private Status status;
    private Long reviewedByUserId;
    private Boolean isActive;
}

