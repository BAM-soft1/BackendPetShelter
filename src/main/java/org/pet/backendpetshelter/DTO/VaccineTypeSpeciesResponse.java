package org.pet.backendpetshelter.DTO;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Entity.VaccinationType;
import org.pet.backendpetshelter.Entity.VaccineTypeSpecies;

@Getter
@Setter
@NoArgsConstructor
public class VaccineTypeSpeciesResponse {
    private Long id;
    private Species species;
    private VaccinationType vaccinationType;

    public VaccineTypeSpeciesResponse(VaccineTypeSpecies vaccineTypeSpecies) {
        this.id = vaccineTypeSpecies.getId();
        this.species = vaccineTypeSpecies.getSpecies();
        this.vaccinationType = vaccineTypeSpecies.getVaccinationType();
    }
}
