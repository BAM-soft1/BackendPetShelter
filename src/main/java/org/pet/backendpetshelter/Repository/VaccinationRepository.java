package org.pet.backendpetshelter.Reposiotry;


import org.pet.backendpetshelter.Entity.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {
    Vaccination findById(long id);
}
