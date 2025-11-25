package org.pet.backendpetshelter.Reposiotry;

import org.pet.backendpetshelter.Entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface AnimalProceduresRepository extends JpaRepository<Animal, Long> {

    /**
     * Call GetAnimalAge function
     * Example: SELECT GetAnimalAge('2020-05-15')
     */
    @Query(value = "SELECT GetAnimalAge(:birthDate)", nativeQuery = true)
    Integer getAnimalAge(@Param("birthDate") Date birthDate);

    /**
     * Call HasRequiredVaccinations function
     * Example: SELECT HasRequiredVaccinations(1)
     */
    @Query(value = "SELECT HasRequiredVaccinations(:animalId)", nativeQuery = true)
    Boolean hasRequiredVaccinations(@Param("animalId") Long animalId);

    /**
     * Call GetTotalAdoptionCost function
     * Example: SELECT GetTotalAdoptionCost(1)
     */
    @Query(value = "SELECT GetTotalAdoptionCost(:animalId)", nativeQuery = true)
    Double getTotalAdoptionCost(@Param("animalId") Long animalId);

    /**
     * Call CompleteAdoption stored procedure
     * Example: CALL CompleteAdoption(1, '2025-11-25')
     */
    @Procedure(procedureName = "CompleteAdoption")
    void completeAdoption(
        @Param("p_application_id") Integer applicationId,
        @Param("p_adoption_date") Date adoptionDate
    );

    /**
     * Call VaccinateAnimalForAdoption stored procedure
     * Example: CALL VaccinateAnimalForAdoption(1, 1)
     */
    @Procedure(procedureName = "VaccinateAnimalForAdoption")
    void vaccinateAnimalForAdoption(
        @Param("p_animal_id") Integer animalId,
        @Param("p_vet_id") Integer vetId
    );
}
