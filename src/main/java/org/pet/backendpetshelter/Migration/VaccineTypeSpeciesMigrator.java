package org.pet.backendpetshelter.Migration;

import org.pet.backendpetshelter.Entity.VaccineTypeSpecies;
import org.pet.backendpetshelter.Mongo.Entity.VaccineTypeSpeciesDocument;
import org.pet.backendpetshelter.Mongo.Repository.VaccineTypeSpeciesMongoRepository;
import org.pet.backendpetshelter.Repository.VaccineTypeSpeciesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VaccineTypeSpeciesMigrator implements CommandLineRunner {

    private final VaccineTypeSpeciesRepository vtsRepository;
    private final VaccineTypeSpeciesMongoRepository vtsMongoRepository;

    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public VaccineTypeSpeciesMigrator(VaccineTypeSpeciesRepository vtsRepository,
                                      VaccineTypeSpeciesMongoRepository vtsMongoRepository) {
        this.vtsRepository = vtsRepository;
        this.vtsMongoRepository = vtsMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("VaccineTypeSpecies migration disabled. Set migration.enabled=true to run.");
            return;
        }

        System.out.println("Starting VaccineTypeSpecies migration from SQL to MongoDB...");

        var links = vtsRepository.findAll();

        var docs = links.stream()
                .map(this::toDocument)
                .toList();

        vtsMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " vaccine-type-species links to MongoDB");
    }

    private VaccineTypeSpeciesDocument toDocument(VaccineTypeSpecies vts) {
        return VaccineTypeSpeciesDocument.builder()
                .id(toStringOrNull(vts.getId()))
                .speciesId(vts.getSpecies() != null ? toStringOrNull(vts.getSpecies().getId()) : null)
                .vaccinationTypeId(vts.getVaccinationType() != null ? toStringOrNull(vts.getVaccinationType().getId()) : null)
                .build();
    }

    private String toStringOrNull(Long id) {
        return id != null ? id.toString() : null;
    }
}