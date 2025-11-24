package org.pet.backendpetshelter.Repository;

import org.pet.backendpetshelter.Entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    MedicalRecord findMedicalRecordById(Long id);
}