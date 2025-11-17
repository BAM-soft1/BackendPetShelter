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
    private int duration_months;
    private int required_for_adoption;

    public VaccinationTypeResponse(VaccinationType vaccinationType) {
        this.id = vaccinationType.getId();
        this.vaccineName = vaccinationType.getVaccineName();
        this.description = vaccinationType.getDescription();
        this.duration_months = vaccinationType.getDuration_months();
        this.required_for_adoption = vaccinationType.getRequired_for_adoption();
    }
}
