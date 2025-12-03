package org.pet.backendpetshelter.Reposiotry;


import org.pet.backendpetshelter.Entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    Animal findById(long id);
}
