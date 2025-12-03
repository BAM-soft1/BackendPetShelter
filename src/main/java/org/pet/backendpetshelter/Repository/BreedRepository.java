package org.pet.backendpetshelter.Reposiotry;


import org.pet.backendpetshelter.Entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {
    Breed findById(long id);
    Optional<Breed> findByName(String name);


}

