package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VaccinationTypeRequest {
    private Long id;
    private String vaccineName;
    private String description;
    private int durationMonths;
    private int requiredForAdoption;
}
