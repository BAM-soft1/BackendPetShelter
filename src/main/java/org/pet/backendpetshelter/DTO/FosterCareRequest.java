package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.User;

import java.util.Date;

@Getter
@Setter
public class FosterCareRequest {
    private Animal animal;
    private User fosterParent;
    private Date startDate;
    private Date endDate;
    private Boolean isActive;
}
