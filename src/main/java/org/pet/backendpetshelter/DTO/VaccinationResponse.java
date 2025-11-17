package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.Vaccination;
import org.pet.backendpetshelter.Entity.Veterinarian;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
public class VaccinationResponse {
    private Animal animal;
    private Veterinarian veterinarian;
    private Date date_administered;
    private String vaccine_name;
    private Date next_due_date;


    public VaccinationResponse(Vaccination vaccination) {
        this.animal = vaccination.getAnimal();
        this.veterinarian = vaccination.getVeterinarian();
        this.date_administered = vaccination.getDate_administered();
        this.vaccine_name = vaccination.getVaccine_name();
        this.next_due_date = vaccination.getNext_due_date();

    }
}
