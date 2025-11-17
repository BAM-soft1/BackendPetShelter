package org.pet.backendpetshelter.Reposiotry;

import org.pet.backendpetshelter.Entity.VaccineTypeSpecies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccineTypeSpeciesRepository extends JpaRepository<VaccineTypeSpecies, Long> {
    VaccineTypeSpecies findById(long id);
}
