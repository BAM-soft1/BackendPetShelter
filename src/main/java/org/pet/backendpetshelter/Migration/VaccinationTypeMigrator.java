package org.pet.backendpetshelter.Migration;

import org.pet.backendpetshelter.Entity.VaccinationType;
import org.pet.backendpetshelter.Mongo.Entity.VaccinationTypeDocument;
import org.pet.backendpetshelter.Mongo.Repository.VaccinationTypeMongoRepository;
import org.pet.backendpetshelter.Repository.VaccinationTypeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VaccinationTypeMigrator implements CommandLineRunner {

    private final VaccinationTypeRepository vaccinationTypeRepository;
    private final VaccinationTypeMongoRepository vaccinationTypeMongoRepository;

    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public VaccinationTypeMigrator(VaccinationTypeRepository vaccinationTypeRepository,
                                   VaccinationTypeMongoRepository vaccinationTypeMongoRepository) {
        this.vaccinationTypeRepository = vaccinationTypeRepository;
        this.vaccinationTypeMongoRepository = vaccinationTypeMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("VaccinationType migration disabled. Set migration.enabled=true to run.");
            return;
        }

        System.out.println("Starting VaccinationType migration from SQL to MongoDB...");

        var types = vaccinationTypeRepository.findAll();

        var docs = types.stream()
                .map(this::toDocument)
                .toList();

        vaccinationTypeMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " vaccination types to MongoDB");
    }

    private VaccinationTypeDocument toDocument(VaccinationType t) {
        return VaccinationTypeDocument.builder()
                .id(toStringOrNull(t.getId()))
                .vaccineName(t.getVaccineName())
                .description(t.getDescription())
                .durationMonths(t.getDurationMonths())
                .requiredForAdoption(t.getRequiredForAdoption())
                .build();
    }

    private String toStringOrNull(Long id) {
        return id != null ? id.toString() : null;
    }
}