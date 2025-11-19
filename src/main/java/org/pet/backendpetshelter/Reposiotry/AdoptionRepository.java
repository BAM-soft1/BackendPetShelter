package org.pet.backendpetshelter.Reposiotry;

import org.pet.backendpetshelter.Entity.Adoption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
    Adoption findById(long id);
}
