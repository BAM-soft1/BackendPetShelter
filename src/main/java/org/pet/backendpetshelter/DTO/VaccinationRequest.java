package org.pet.backendpetshelter.DTO;


import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.Veterinarian;

import java.util.Date;


@Getter
@Setter
public class VaccinationRequest {
    private Animal animal;
    private Veterinarian veterinarian;
    private Date date_administered;
    private String vaccine_name;
    private Date next_due_date;
}
