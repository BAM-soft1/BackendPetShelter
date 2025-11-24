package org.pet.backendpetshelter.Migration;

import org.pet.backendpetshelter.Entity.MedicalRecord;
import org.pet.backendpetshelter.Mongo.Entity.MedicalRecordDocument;
import org.pet.backendpetshelter.Mongo.Repository.MedicalRecordMongoRepository;
import org.pet.backendpetshelter.Repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MedicalRecordMigrator implements CommandLineRunner {

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMongoRepository medicalRecordMongoRepository;

    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public MedicalRecordMigrator(MedicalRecordRepository medicalRecordRepository,
                                 MedicalRecordMongoRepository medicalRecordMongoRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalRecordMongoRepository = medicalRecordMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("MedicalRecord migration disabled. Set migration.enabled=true to run.");
            return;
        }

        System.out.println("Starting MedicalRecord migration from SQL to MongoDB...");

        var records = medicalRecordRepository.findAll();

        var docs = records.stream()
                .map(this::toDocument)
                .toList();

        medicalRecordMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " medical records to MongoDB");
    }

    private MedicalRecordDocument toDocument(MedicalRecord r) {
        return MedicalRecordDocument.builder()
                .id(toStringOrNull(r.getId()))
                .animalId(r.getAnimal() != null ? toStringOrNull(r.getAnimal().getId()) : null)
                .veterinarianId(r.getVeterinarian() != null ? toStringOrNull(r.getVeterinarian().getId()) : null)
                .date(r.getDate())
                .diagnosis(r.getDiagnosis())
                .treatment(r.getTreatment())
                .cost(r.getCost())
                .build();
    }

    private String toStringOrNull(Long id) {
        return id != null ? id.toString() : null;
    }
}