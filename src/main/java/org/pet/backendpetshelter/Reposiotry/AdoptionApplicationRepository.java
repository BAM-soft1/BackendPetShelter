package org.pet.backendpetshelter.Reposiotry;

import org.pet.backendpetshelter.Entity.AdoptionApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, Long> {
    AdoptionApplication findById(long id);
}
