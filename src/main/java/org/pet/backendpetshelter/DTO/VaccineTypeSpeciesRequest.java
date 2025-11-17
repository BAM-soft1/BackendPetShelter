package org.pet.backendpetshelter.DTO;


import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Entity.VaccinationType;

@Getter
@Setter
public class VaccineTypeSpeciesRequest {
    private Species species;
    private VaccinationType vaccinationType;

}
