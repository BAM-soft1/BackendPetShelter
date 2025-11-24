package org.pet.backendpetshelter.Migration;

import org.pet.backendpetshelter.Entity.Veterinarian;
import org.pet.backendpetshelter.Mongo.Entity.VeterinarianDocument;
import org.pet.backendpetshelter.Mongo.Repository.VeterinarianMongoRepository;
import org.pet.backendpetshelter.Repository.VeterinarianRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VeterinarianMigrator implements CommandLineRunner {

    private final VeterinarianRepository veterinarianRepository;
    private final VeterinarianMongoRepository veterinarianMongoRepository;

    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public VeterinarianMigrator(VeterinarianRepository veterinarianRepository,
                                VeterinarianMongoRepository veterinarianMongoRepository) {
        this.veterinarianRepository = veterinarianRepository;
        this.veterinarianMongoRepository = veterinarianMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("Veterinarian migration disabled. Set migration.enabled=true to run.");
            return;
        }

        System.out.println("Starting Veterinarian migration from SQL to MongoDB...");

        var vets = veterinarianRepository.findAll();

        var docs = vets.stream()
                .map(this::toDocument)
                .toList();

        veterinarianMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " veterinarians to MongoDB");
    }

    private VeterinarianDocument toDocument(Veterinarian v) {
        return VeterinarianDocument.builder()
                .id(toStringOrNull(v.getId()))
                .userId(v.getUser() != null ? toStringOrNull(v.getUser().getId()) : null)
                .licenseNumber(v.getLicenseNumber())
                .clinicName(v.getClinicName())
                .isActive(v.getIsActive())
                .build();
    }

    private String toStringOrNull(Long id) {
        return id != null ? id.toString() : null;
    }
}