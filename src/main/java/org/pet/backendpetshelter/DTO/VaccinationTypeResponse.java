package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.VaccinationType;

@Getter
@Setter
@NoArgsConstructor
public class VaccinationTypeResponse {
    private Long id;
    private String vaccineName;
    private String description;
    private int durationMonths;
    private int requiredForAdoption;

    public VaccinationTypeResponse(VaccinationType vaccinationType) {
        this.id = vaccinationType.getId();
        this.vaccineName = vaccinationType.getVaccineName();
        this.description = vaccinationType.getDescription();
        this.durationMonths = vaccinationType.getDurationMonths();
        this.requiredForAdoption = vaccinationType.getRequiredForAdoption();
    }
}
