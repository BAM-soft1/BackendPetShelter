package org.pet.backendpetshelter.Reposiotry;


import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Entity.Veterinian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinianRepository extends JpaRepository<Veterinian, Long> {
    Veterinian findById(long id);

}
