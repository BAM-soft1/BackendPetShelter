package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.FosterCare;
import org.pet.backendpetshelter.Entity.User;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class FosterCareRespons {
    private Animal animal;
    private User fosterParent;
    private Date startDate;
    private Date endDate;
    private Boolean isActive;

    public FosterCareRespons(FosterCare fosterCare) {
        this.animal = fosterCare.getAnimal();
        this.fosterParent = fosterCare.getFosterParent();
        this.startDate = fosterCare.getStartDate();
        this.endDate = fosterCare.getEndDate();
        this.isActive = fosterCare.getIsActive();
    }
}
