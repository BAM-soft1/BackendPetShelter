package org.pet.backendpetshelter.Reposiotry;


import org.pet.backendpetshelter.Entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {
    Species findById(long id);
}
