package org.pet.backendpetshelter.integration.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.pet.backendpetshelter.Roles;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userRepository.deleteAll();
        entityManager.clear();
    }

    // ==== TEST HELPER ====

    private User createValidUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("Federico");
        user.setLastName("Valverde");
        user.setPassword(password);
        user.setPhone("+45 12345678");
        return user;
    }

    @Nested
    @DisplayName("Save User Tests")
    class SaveUserTests {

        // ==== BASIC PERSISTENCE ====

        @Test
        @DisplayName("Should save user to database with all fields")
        void testSaveUserWithAllFields() {
            User user = createValidUser("user@example.com", "hashedPassword123");

            User savedUser = userRepository.save(user);

            assertNotNull(savedUser.getId());
            assertEquals("user@example.com", savedUser.getEmail());
            assertEquals("Federico", savedUser.getFirstName());
            assertEquals("Valverde", savedUser.getLastName());
            assertEquals("hashedPassword123", savedUser.getPassword());
            assertEquals("+45 12345678", savedUser.getPhone());
        }

        @Test
        @DisplayName("Should persist user and retrieve from database")
        void testSaveAndFlush() {
            User user = createValidUser("user@example.com", "hashedPassword123");

            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear(); 

            Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

            assertTrue(retrievedUser.isPresent());
            assertEquals("user@example.com", retrievedUser.get().getEmail());
            assertEquals("Federico", retrievedUser.get().getFirstName());
        }

        // ==== DEFAULT VALUES ====

        @Test
        @DisplayName("Should set isActive to true by default")
        void testDefaultIsActiveTrue() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            // Don't explicitly set isActive

            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear();

            Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

            assertTrue(retrievedUser.isPresent());
            assertTrue(retrievedUser.get().getIsActive());
        }

        @Test
        @DisplayName("Should set role to USER by default")
        void testDefaultRoleUser() {
            User user = createValidUser("user@example.com", "hashedPassword123");


            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear();

            Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

            assertTrue(retrievedUser.isPresent());
            assertEquals(Roles.USER, retrievedUser.get().getRole());
        }

        @Test
        @DisplayName("Should allow overriding default role")
        void testOverrideDefaultRole() {
            User user = createValidUser("admin@example.com", "hashedPassword123");
            user.setRole(Roles.ADMIN);

            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear();

            Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

            assertTrue(retrievedUser.isPresent());
            assertEquals(Roles.ADMIN, retrievedUser.get().getRole());
        }

        @Test
        @DisplayName("Should allow overriding default isActive")
        void testOverrideDefaultIsActive() {
            User user = createValidUser("inactive@example.com", "hashedPassword123");
            user.setIsActive(false);

            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear();

            Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

            assertTrue(retrievedUser.isPresent());
            assertFalse(retrievedUser.get().getIsActive());
        }

        // ==== BCRYPT PASSWORD STORAGE ====

        @Test
        @DisplayName("Should store BCrypt hashed password correctly")
        void testBCryptPasswordStorage() {
            String rawPassword = "MySecureP@ss123";
            String hashedPassword = passwordEncoder.encode(rawPassword);
            User user = createValidUser("user@example.com", hashedPassword);

            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear();

            Optional<User> retrievedUser = userRepository.findById(savedUser.getId());

            assertTrue(retrievedUser.isPresent());
            assertNotEquals(rawPassword, retrievedUser.get().getPassword()); // Should NOT be plain text
            assertTrue(passwordEncoder.matches(rawPassword, retrievedUser.get().getPassword())); // Should match BCrypt hash
        }

        @Test
        @DisplayName("Should store BCrypt hash with correct format")
        void testBCryptHashFormat() {
            String rawPassword = "AnotherP@ss456";
            String hashedPassword = passwordEncoder.encode(rawPassword);
            User user = createValidUser("user@example.com", hashedPassword);

            User savedUser = userRepository.save(user);

            // BCrypt hashes start with $2a$, $2b$, or $2y$ and are 60 characters long
            assertTrue(savedUser.getPassword().startsWith("$2"));
            assertEquals(60, savedUser.getPassword().length());
        }
    }

    @Nested
    @DisplayName("Find User By Email Tests")
    class FindUserByEmailTests {

        // ==== SUCCESSFUL RETRIEVAL ====

        @Test
        @DisplayName("Should find user by exact email match")
        void testFindByEmailExactMatch() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            userRepository.save(user);
            entityManager.flush();

            Optional<User> foundUser = userRepository.findByEmail("user@example.com");

            assertTrue(foundUser.isPresent());
            assertEquals("user@example.com", foundUser.get().getEmail());
            assertEquals("Federico", foundUser.get().getFirstName());
        }

        @Test
        @DisplayName("Should return empty when email does not exist")
        void testFindByEmailNotFound() {
            Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

            assertFalse(foundUser.isPresent());
        }

        // ==== CASE SENSITIVITY ====

        @Test
        @DisplayName("Should NOT find user with different case (case-sensitive)")
        void testFindByEmailCaseSensitive() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            userRepository.save(user);
            entityManager.flush();

            Optional<User> foundUser = userRepository.findByEmail("USER@EXAMPLE.COM");

            assertFalse(foundUser.isPresent()); // Repository is case-sensitive
        }

        @Test
        @DisplayName("Should find user when email is stored in lowercase")
        void testFindByEmailLowercaseStored() {
            // Simulating service layer behavior: email stored in lowercase
            User user = createValidUser("user@example.com".toLowerCase(), "hashedPassword123");
            userRepository.save(user);
            entityManager.flush();

            Optional<User> foundUser = userRepository.findByEmail("user@example.com");

            assertTrue(foundUser.isPresent());
            assertEquals("user@example.com", foundUser.get().getEmail());
        }

        // ==== MULTIPLE USERS ====

        @Test
        @DisplayName("Should find correct user when multiple users exist")
        void testFindByEmailWithMultipleUsers() {
            User user1 = createValidUser("user1@example.com", "hash1");
            User user2 = createValidUser("user2@example.com", "hash2");
            User user3 = createValidUser("user3@example.com", "hash3");
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            entityManager.flush();

            Optional<User> foundUser = userRepository.findByEmail("user2@example.com");

            assertTrue(foundUser.isPresent());
            assertEquals("user2@example.com", foundUser.get().getEmail());
        }
    }

    @Nested
    @DisplayName("Exists By Email Tests")
    class ExistsByEmailTests {

        // ==== EXISTENCE CHECK ====

        @Test
        @DisplayName("Should return true when email exists")
        void testExistsByEmailTrue() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            userRepository.save(user);
            entityManager.flush();

            boolean exists = userRepository.existsByEmail("user@example.com");

            assertTrue(exists);
        }

        @Test
        @DisplayName("Should return false when email does not exist")
        void testExistsByEmailFalse() {
            boolean exists = userRepository.existsByEmail("nonexistent@example.com");

            assertFalse(exists);
        }

        // ==== CASE SENSITIVITY ====

        @Test
        @DisplayName("Should return false for different case (case-sensitive)")
        void testExistsByEmailCaseSensitive() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            userRepository.save(user);
            entityManager.flush();

            boolean exists = userRepository.existsByEmail("USER@EXAMPLE.COM");

            assertFalse(exists); // Repository is case-sensitive
        }

        @Test
        @DisplayName("Should return true when email stored in lowercase matches query")
        void testExistsByEmailLowercaseStored() {
            // Simulating service layer behavior: email stored in lowercase
            User user = createValidUser("user@example.com".toLowerCase(), "hashedPassword123");
            userRepository.save(user);
            entityManager.flush();

            boolean exists = userRepository.existsByEmail("user@example.com");

            assertTrue(exists);
        }

        // ==== MULTIPLE USERS ====

        @Test
        @DisplayName("Should return true when checking existing email among multiple users")
        void testExistsByEmailWithMultipleUsers() {
            User user1 = createValidUser("user1@example.com", "hash1");
            User user2 = createValidUser("user2@example.com", "hash2");
            userRepository.save(user1);
            userRepository.save(user2);
            entityManager.flush();

            boolean exists = userRepository.existsByEmail("user2@example.com");

            assertTrue(exists);
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        // ==== FIELD UPDATES ====

        @Test
        @DisplayName("Should update user first name")
        void testUpdateFirstName() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear();

            savedUser.setFirstName("Jane");
            userRepository.save(savedUser);
            entityManager.flush();
            entityManager.clear();

            Optional<User> updatedUser = userRepository.findById(savedUser.getId());

            assertTrue(updatedUser.isPresent());
            assertEquals("Jane", updatedUser.get().getFirstName());
        }

        @Test
        @DisplayName("Should update user last name")
        void testUpdateLastName() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear();

            savedUser.setLastName("Smith");
            userRepository.save(savedUser);
            entityManager.flush();
            entityManager.clear();

            Optional<User> updatedUser = userRepository.findById(savedUser.getId());

            assertTrue(updatedUser.isPresent());
            assertEquals("Smith", updatedUser.get().getLastName());
        }

        @Test
        @DisplayName("Should update user password")
        void testUpdatePassword() {
            User user = createValidUser("user@example.com", "oldHashedPassword");
            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear();

            String newHashedPassword = passwordEncoder.encode("NewP@ss789");
            savedUser.setPassword(newHashedPassword);
            userRepository.save(savedUser);
            entityManager.flush();
            entityManager.clear();

            Optional<User> updatedUser = userRepository.findById(savedUser.getId());

            assertTrue(updatedUser.isPresent());
            assertTrue(passwordEncoder.matches("NewP@ss789", updatedUser.get().getPassword()));
        }

        @Test
        @DisplayName("Should update user phone")
        void testUpdatePhone() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear();

            savedUser.setPhone("+45 87654321");
            userRepository.save(savedUser);
            entityManager.flush();
            entityManager.clear();

            Optional<User> updatedUser = userRepository.findById(savedUser.getId());

            assertTrue(updatedUser.isPresent());
            assertEquals("+45 87654321", updatedUser.get().getPhone());
        }

        @Test
        @DisplayName("Should update user isActive status")
        void testUpdateIsActive() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear();

            savedUser.setIsActive(false);
            userRepository.save(savedUser);
            entityManager.flush();
            entityManager.clear();

            Optional<User> updatedUser = userRepository.findById(savedUser.getId());

            assertTrue(updatedUser.isPresent());
            assertFalse(updatedUser.get().getIsActive());
        }

        @Test
        @DisplayName("Should update user role")
        void testUpdateRole() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear();

            savedUser.setRole(Roles.ADMIN);
            userRepository.save(savedUser);
            entityManager.flush();
            entityManager.clear();

            Optional<User> updatedUser = userRepository.findById(savedUser.getId());

            assertTrue(updatedUser.isPresent());
            assertEquals(Roles.ADMIN, updatedUser.get().getRole());
        }

        @Test
        @DisplayName("Should update multiple fields at once")
        void testUpdateMultipleFields() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            User savedUser = userRepository.save(user);
            entityManager.flush();
            entityManager.clear();

            savedUser.setFirstName("Mohamed");
            savedUser.setLastName("Salah");
            savedUser.setPhone("+45 11111111");
            savedUser.setRole(Roles.ADMIN);
            userRepository.save(savedUser);
            entityManager.flush();
            entityManager.clear();

            Optional<User> updatedUser = userRepository.findById(savedUser.getId());

            assertTrue(updatedUser.isPresent());
            assertEquals("Mohamed", updatedUser.get().getFirstName());
            assertEquals("Salah", updatedUser.get().getLastName());
            assertEquals("+45 11111111", updatedUser.get().getPhone());
            assertEquals(Roles.ADMIN, updatedUser.get().getRole());
        }
    }

    @Nested
    @DisplayName("Delete User Tests")
    class DeleteUserTests {

        // ==== DELETION ====

        @Test
        @DisplayName("Should delete user by ID")
        void testDeleteUserById() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            User savedUser = userRepository.save(user);
            entityManager.flush();

            userRepository.deleteById(savedUser.getId());
            entityManager.flush();

            Optional<User> deletedUser = userRepository.findById(savedUser.getId());

            assertFalse(deletedUser.isPresent());
        }

        @Test
        @DisplayName("Should delete user by entity")
        void testDeleteUserByEntity() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            User savedUser = userRepository.save(user);
            entityManager.flush();

            userRepository.delete(savedUser);
            entityManager.flush();

            Optional<User> deletedUser = userRepository.findById(savedUser.getId());

            assertFalse(deletedUser.isPresent());
        }

        @Test
        @DisplayName("Should not find user by email after deletion")
        void testFindByEmailAfterDeletion() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            User savedUser = userRepository.save(user);
            entityManager.flush();

            userRepository.deleteById(savedUser.getId());
            entityManager.flush();

            Optional<User> deletedUser = userRepository.findByEmail("user@example.com");

            assertFalse(deletedUser.isPresent());
        }

        @Test
        @DisplayName("Should return false for existsByEmail after deletion")
        void testExistsByEmailAfterDeletion() {
            User user = createValidUser("user@example.com", "hashedPassword123");
            User savedUser = userRepository.save(user);
            entityManager.flush();

            userRepository.deleteById(savedUser.getId());
            entityManager.flush();

            boolean exists = userRepository.existsByEmail("user@example.com");

            assertFalse(exists);
        }

        @Test
        @DisplayName("Should delete only specified user when multiple users exist")
        void testDeleteSpecificUserWithMultipleUsers() {
            User user1 = createValidUser("user1@example.com", "hash1");
            User user2 = createValidUser("user2@example.com", "hash2");
            User user3 = createValidUser("user3@example.com", "hash3");
            User savedUser1 = userRepository.save(user1);
            User savedUser2 = userRepository.save(user2);
            User savedUser3 = userRepository.save(user3);
            entityManager.flush();

            userRepository.deleteById(savedUser2.getId());
            entityManager.flush();

            Optional<User> deletedUser = userRepository.findById(savedUser2.getId());
            Optional<User> remainingUser1 = userRepository.findById(savedUser1.getId());
            Optional<User> remainingUser3 = userRepository.findById(savedUser3.getId());

            assertFalse(deletedUser.isPresent());
            assertTrue(remainingUser1.isPresent());
            assertTrue(remainingUser3.isPresent());
        }
    }
}