package org.pet.backendpetshelter.Migration;

import org.pet.backendpetshelter.Configuration.RefreshToken;
import org.pet.backendpetshelter.Mongo.Entity.RefreshTokenDocument;
import org.pet.backendpetshelter.Mongo.Repository.RefreshTokenMongoRepository;
import org.pet.backendpetshelter.Repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RefreshTokenMigrator implements CommandLineRunner {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMongoRepository refreshTokenMongoRepository;

    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public RefreshTokenMigrator(RefreshTokenRepository refreshTokenRepository,
                                RefreshTokenMongoRepository refreshTokenMongoRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenMongoRepository = refreshTokenMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("RefreshToken migration disabled. Set migration.enabled=true to run.");
            return;
        }

        System.out.println("Starting RefreshToken migration from SQL to MongoDB...");

        var tokens = refreshTokenRepository.findAll();

        var docs = tokens.stream()
                .map(this::toDocument)
                .toList();

        refreshTokenMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " refresh tokens to MongoDB");
    }

    private RefreshTokenDocument toDocument(RefreshToken t) {
        return RefreshTokenDocument.builder()
                .id(t.getId() != null ? t.getId().toString() : null)
                .token(t.getToken())
                .userId(t.getUser() != null ? t.getUser().getId().toString() : null)
                .expiresAt(t.getExpiresAt())
                .revoked(t.getRevoked())
                .build();
    }
}