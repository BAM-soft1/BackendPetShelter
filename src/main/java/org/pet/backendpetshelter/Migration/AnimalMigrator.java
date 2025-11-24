package org.pet.backendpetshelter.Migration;

import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Mongo.Entity.AnimalDocument;
import org.pet.backendpetshelter.Mongo.Repository.AnimalMongoRepository;
import org.pet.backendpetshelter.Repository.AnimalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AnimalMigrator implements CommandLineRunner {

    private final AnimalRepository animalRepository;
    private final AnimalMongoRepository animalMongoRepository;

    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public AnimalMigrator(AnimalRepository animalRepository,
                          AnimalMongoRepository animalMongoRepository) {
        this.animalRepository = animalRepository;
        this.animalMongoRepository = animalMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("Animal migration disabled. Set migration.enabled=true to run.");
            return;
        }

        System.out.println("Starting Animal migration from SQL to MongoDB...");

        var animals = animalRepository.findAll();

        var docs = animals.stream()
                .map(this::toDocument)
                .toList();

        animalMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " animals to MongoDB");
    }

    private AnimalDocument toDocument(Animal a) {
        return AnimalDocument.builder()
                .id(toStringOrNull(a.getId()))
                .name(a.getName())
                .speciesId(a.getSpecies() != null ? toStringOrNull(a.getSpecies().getId()) : null)
                .breedId(a.getBreed() != null ? toStringOrNull(a.getBreed().getId()) : null)
                .birthDate(a.getBirthDate())
                .sex(a.getSex())
                .intakeDate(a.getIntakeDate())
                .status(a.getStatus())
                .price(a.getPrice())
                .isActive(a.getIsActive())
                .imageUrl(a.getImageUrl())
                .build();
    }

    private String toStringOrNull(Long id) {
        return id != null ? id.toString() : null;
    }
}