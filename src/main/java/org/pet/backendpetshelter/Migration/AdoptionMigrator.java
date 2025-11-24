package org.pet.backendpetshelter.Migration;

import org.pet.backendpetshelter.Entity.Adoption;
import org.pet.backendpetshelter.Mongo.Entity.AdoptionDocument;
import org.pet.backendpetshelter.Mongo.Repository.AdoptionMongoRepository;
import org.pet.backendpetshelter.Repository.AdoptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdoptionMigrator implements CommandLineRunner {

    private final AdoptionRepository adoptionRepository;
    private final AdoptionMongoRepository adoptionMongoRepository;

    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public AdoptionMigrator(AdoptionRepository adoptionRepository,
                            AdoptionMongoRepository adoptionMongoRepository) {
        this.adoptionRepository = adoptionRepository;
        this.adoptionMongoRepository = adoptionMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("Adoption migration disabled. Set migration.enabled=true to run.");
            return;
        }

        System.out.println("Starting Adoption migration from SQL to MongoDB...");

        var adoptions = adoptionRepository.findAll();

        var docs = adoptions.stream()
                .map(this::toDocument)
                .toList();

        adoptionMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " adoptions to MongoDB");
    }

    private AdoptionDocument toDocument(Adoption a) {
        return AdoptionDocument.builder()
                .id(toStringOrNull(a.getId()))
                .adoptionUserId(toStringOrNull(a.getAdoptionUser() != null ? a.getAdoptionUser().getId() : null))
                .animalId(toStringOrNull(a.getAnimal() != null ? a.getAnimal().getId() : null))
                .applicationId(toStringOrNull(a.getApplication() != null ? a.getApplication().getId() : null))
                .adoptionDate(a.getAdoptionDate())
                .isActive(a.getIsActive())
                .build();
    }

    private String toStringOrNull(Long id) {
        return id != null ? id.toString() : null;
    }
}