package org.pet.backendpetshelter.Migration;

import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Mongo.Entity.BreedDocument;
import org.pet.backendpetshelter.Mongo.Repository.BreedMongoRepository;
import org.pet.backendpetshelter.Repository.BreedRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BreedMigrator implements CommandLineRunner {

    private final BreedRepository breedRepository;
    private final BreedMongoRepository breedMongoRepository;

    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public BreedMigrator(BreedRepository breedRepository,
                         BreedMongoRepository breedMongoRepository) {
        this.breedRepository = breedRepository;
        this.breedMongoRepository = breedMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("Breed migration disabled. Set migration.enabled=true to run.");
            return;
        }

        System.out.println("Starting Breed migration from SQL to MongoDB...");

        var breeds = breedRepository.findAll();

        var docs = breeds.stream()
                .map(this::toDocument)
                .toList();

        breedMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " breeds to MongoDB");
    }

    private BreedDocument toDocument(Breed b) {
        return BreedDocument.builder()
                .id(toStringOrNull(b.getId()))
                .speciesId(b.getSpecies() != null ? toStringOrNull(b.getSpecies().getId()) : null)
                .name(b.getName())
                .build();
    }

    private String toStringOrNull(Long id) {
        return id != null ? id.toString() : null;
    }
}