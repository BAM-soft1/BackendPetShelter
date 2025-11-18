package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.AdoptionApplication;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Status;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
public class AdoptionApplicationRespons {
    private Long id;
    private User user;
    private Animal animal;
    private Date applicationDate;
    private Status status;
    private User reviewedByUser;
    private Boolean isActive;

    public AdoptionApplicationRespons(AdoptionApplication adoptionApplication) {
        this.id = adoptionApplication.getId();
        this.user = adoptionApplication.getUser();
        this.animal = adoptionApplication.getAnimal();
        this.applicationDate = adoptionApplication.getApplicationDate();
        this.status = adoptionApplication.getStatus();
        this.reviewedByUser = adoptionApplication.getReviewedByUser();
        this.isActive = adoptionApplication.getIsActive();
    }
}
