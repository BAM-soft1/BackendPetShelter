package org.pet.backendpetshelter.Migration;

import org.pet.backendpetshelter.Entity.FosterCare;
import org.pet.backendpetshelter.Mongo.Entity.FosterCareDocument;
import org.pet.backendpetshelter.Mongo.Repository.FosterCareMongoRepository;
import org.pet.backendpetshelter.Repository.FosterCareRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FosterCareMigrator implements CommandLineRunner {

    private final FosterCareRepository fosterCareRepository;
    private final FosterCareMongoRepository fosterCareMongoRepository;

    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public FosterCareMigrator(FosterCareRepository fosterCareRepository,
                              FosterCareMongoRepository fosterCareMongoRepository) {
        this.fosterCareRepository = fosterCareRepository;
        this.fosterCareMongoRepository = fosterCareMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("FosterCare migration disabled. Set migration.enabled=true to run.");
            return;
        }

        System.out.println("Starting FosterCare migration from SQL to MongoDB...");

        var fosterCares = fosterCareRepository.findAll();

        var docs = fosterCares.stream()
                .map(this::toDocument)
                .toList();

        fosterCareMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " foster care records to MongoDB");
    }

    private FosterCareDocument toDocument(FosterCare f) {
        return FosterCareDocument.builder()
                .id(toStringOrNull(f.getId()))
                .animalId(f.getAnimal() != null ? toStringOrNull(f.getAnimal().getId()) : null)
                .fosterParentUserId(f.getFosterParent() != null ? toStringOrNull(f.getFosterParent().getId()) : null)
                .startDate(f.getStartDate())
                .endDate(f.getEndDate())
                .isActive(f.getIsActive())
                .build();
    }

    private String toStringOrNull(Long id) {
        return id != null ? id.toString() : null;
    }
}