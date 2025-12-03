package org.pet.backendpetshelter.Migration;

import org.pet.backendpetshelter.Entity.Vaccination;
import org.pet.backendpetshelter.Mongo.Entity.VaccinationDocument;
import org.pet.backendpetshelter.Mongo.Repository.VaccinationMongoRepository;
import org.pet.backendpetshelter.Repository.VaccinationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VaccinationMigrator implements CommandLineRunner {

    private final VaccinationRepository vaccinationRepository;
    private final VaccinationMongoRepository vaccinationMongoRepository;

    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public VaccinationMigrator(VaccinationRepository vaccinationRepository,
                               VaccinationMongoRepository vaccinationMongoRepository) {
        this.vaccinationRepository = vaccinationRepository;
        this.vaccinationMongoRepository = vaccinationMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("Vaccination migration disabled. Set migration.enabled=true to run.");
            return;
        }

        System.out.println("Starting Vaccination migration from SQL to MongoDB...");

        var vaccinations = vaccinationRepository.findAll();

        var docs = vaccinations.stream()
                .map(this::toDocument)
                .toList();

        vaccinationMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " vaccinations to MongoDB");
    }

    private VaccinationDocument toDocument(Vaccination v) {
        return VaccinationDocument.builder()
                .id(toStringOrNull(v.getId()))
                .animalId(v.getAnimal() != null ? toStringOrNull(v.getAnimal().getId()) : null)
                .veterinarianId(v.getVeterinarian() != null ? toStringOrNull(v.getVeterinarian().getId()) : null)
                .vaccinationTypeId(v.getVaccinationType() != null ? toStringOrNull(v.getVaccinationType().getId()) : null)
                .dateAdministered(v.getDateAdministered())
                .nextDueDate(v.getNextDueDate())
                .build();
    }

    private String toStringOrNull(Long id) {
        return id != null ? id.toString() : null;
    }
}