package org.pet.backendpetshelter.Migration;

import jakarta.persistence.*;
import org.pet.backendpetshelter.Entity.AdoptionApplication;
import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Mongo.Entity.AdoptionApplicationDocument;
import org.pet.backendpetshelter.Status;
import org.springframework.transaction.annotation.Transactional;
import org.pet.backendpetshelter.Mongo.Repository.AdoptionApplicationMongoRepository;
import org.pet.backendpetshelter.Repository.AdoptionApplicationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import java.util.Date;

public class AdoptionApplicationMigrator implements CommandLineRunner {

    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final AdoptionApplicationMongoRepository adoptionApplicationMongoRepository;


    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public AdoptionApplicationMigrator(AdoptionApplicationRepository adoptionApplicationRepository,
                                       AdoptionApplicationMongoRepository adoptionApplicationMongoRepository) {
        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.adoptionApplicationMongoRepository = adoptionApplicationMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("Adoptionapplication migration is disabled. Skipping...");
            return;
        }

        System.out.println("Starting adoptionapplication migration...");

        var adoptionsApplications = adoptionApplicationRepository.findAll();

        var docs = adoptionsApplications.stream()
                .map(this::toDocument)
                .toList();

        adoptionApplicationMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " adoptions to MongoDB");
    }

    private AdoptionApplicationDocument toDocument(AdoptionApplication a) {
        return AdoptionApplicationDocument.builder()
                .id(toStringOrNull(a.getId()))
                .userId(toStringOrNull(a.getUser().getId()))
                .animalId(toStringOrNull(a.getAnimal().getId()))
                .applicationDate(a.getApplicationDate())
                .status(a.getStatus())
                .reviewedByUserId(a.getReviewedByUser() != null ? toStringOrNull(a.getReviewedByUser().getId()) : null)
                .isActive(a.getIsActive())
                .build();
    }

    private String toStringOrNull(Long id) {
        return id != null ? id.toString() : null;
    }}
