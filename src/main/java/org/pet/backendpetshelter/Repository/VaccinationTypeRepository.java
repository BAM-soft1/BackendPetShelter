package org.pet.backendpetshelter.Repository;

import org.pet.backendpetshelter.Entity.VaccinationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccinationTypeRepository extends JpaRepository<VaccinationType, Long> {
    VaccinationType findById(long id);
}
