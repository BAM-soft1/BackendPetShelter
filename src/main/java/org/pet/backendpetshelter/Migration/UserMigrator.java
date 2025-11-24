package org.pet.backendpetshelter.Migration;

import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Mongo.Entity.UserDocument;
import org.pet.backendpetshelter.Mongo.Repository.UserMongoRepository;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserMigrator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserMongoRepository userMongoRepository;

    @Value("${migration.enabled:false}")
    private boolean migrationEnabled;

    public UserMigrator(UserRepository userRepository,
                        UserMongoRepository userMongoRepository) {
        this.userRepository = userRepository;
        this.userMongoRepository = userMongoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        if (!migrationEnabled) {
            System.out.println("User migration disabled. Set migration.enabled=true to run.");
            return;
        }

        System.out.println("Starting User migration from SQL to MongoDB...");

        var users = userRepository.findAll();

        var docs = users.stream()
                .map(this::toDocument)
                .toList();

        userMongoRepository.saveAll(docs);

        System.out.println("Migrated " + docs.size() + " users to MongoDB");
    }

    private UserDocument toDocument(User u) {
        return UserDocument.builder()
                .id(u.getId() != null ? u.getId().toString() : null)
                .email(u.getEmail())
                .password(u.getPassword())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .phone(u.getPhone())
                .isActive(u.getIsActive())
                .role(u.getRole())
                .build();
    }
}