package org.pet.backendpetshelter.Repository;

import org.pet.backendpetshelter.Entity.FosterCare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FosterCareRepository extends JpaRepository<FosterCare, Long> {
    FosterCare findById(long id);
}
