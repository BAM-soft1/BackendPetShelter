package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.Vaccination;
import org.pet.backendpetshelter.Entity.VaccinationType;
import org.pet.backendpetshelter.Entity.Veterinarian;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
public class VaccinationResponse {
    private Long id;
    private Animal animal;
    private Veterinarian veterinarian;
    private Date dateAdministered;
    private VaccinationType vaccinationType;
    private Date nextDueDate;


    public VaccinationResponse(Vaccination vaccination) {
        this.id = vaccination.getId();
        this.animal = vaccination.getAnimal();
        this.veterinarian = vaccination.getVeterinarian();
        this.dateAdministered = vaccination.getDateAdministered();
        this.vaccinationType = vaccination.getVaccinationType();
        this.nextDueDate = vaccination.getNextDueDate();

    }
}
