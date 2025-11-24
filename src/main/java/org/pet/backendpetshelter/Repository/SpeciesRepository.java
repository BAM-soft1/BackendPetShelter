package org.pet.backendpetshelter.Repository;


import org.pet.backendpetshelter.Entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {
    Species findById(long id);
    Optional<Species> findByName(String name);
}
