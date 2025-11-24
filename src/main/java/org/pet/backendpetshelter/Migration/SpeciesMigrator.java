package org.pet.backendpetshelter.Migration;

import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Mongo.Entity.SpeciesDocument;
import org.pet.backendpetshelter.Mongo.Repository.SpeciesMongoRepository;
import org.pet.backendpetshelter.Repository.SpeciesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SpeciesMigrator implements CommandLineRunner {

    private final SpeciesRepository speciesRepository;
    private final SpeciesMongoRepository speciesMongoRepository;

    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public SpeciesMigrator(SpeciesRepository speciesRepository,
                           SpeciesMongoRepository speciesMongoRepository) {
        this.speciesRepository = speciesRepository;
        this.speciesMongoRepository = speciesMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("Species migration disabled. Set migration.enabled=true to run.");
            return;
        }

        System.out.println("Starting Species migration from SQL to MongoDB...");

        var speciesList = speciesRepository.findAll();

        var docs = speciesList.stream()
                .map(this::toDocument)
                .toList();

        speciesMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " species to MongoDB");
    }

    private SpeciesDocument toDocument(Species s) {
        return SpeciesDocument.builder()
                .id(s.getId() != null ? s.getId().toString() : null)
                .name(s.getName())
                .build();
    }
}