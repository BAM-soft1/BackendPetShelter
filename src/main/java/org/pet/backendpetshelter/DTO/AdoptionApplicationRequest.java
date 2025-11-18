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
    private User user;
    private Animal animal;
    private Date applicationDate;
    private Status status;
    private User reviewedByUser;
    private Boolean isActive;
}
