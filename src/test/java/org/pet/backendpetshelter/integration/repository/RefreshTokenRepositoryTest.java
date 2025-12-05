package org.pet.backendpetshelter.integration.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.Configuration.RefreshToken;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Roles;
import org.pet.backendpetshelter.Repository.RefreshTokenRepository;
import org.pet.backendpetshelter.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("RefreshTokenRepository Integration Tests")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        entityManager.clear();
    }

    // ==== TEST HELPERS ====

    private User createAndSaveUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("hashedPassword123");
        user.setPhone("+45 12345678");
        user.setRole(Roles.USER);
        user.setIsActive(true);
        return userRepository.save(user);
    }

    private RefreshToken createValidRefreshToken(User user, String tokenString) {
        RefreshToken token = new RefreshToken();
        token.setToken(tokenString);
        token.setUser(user);
        token.setExpiresAt(Instant.now().plusSeconds(86400)); // 24 hours
        token.setRevoked(false);
        return token;
    }

    @Nested
    @DisplayName("Save Refresh Token Tests")
    class SaveRefreshTokenTests {

        // ==== BASIC PERSISTENCE ====

        @Test
        @DisplayName("Should save refresh token to database with all fields")
        void testSaveRefreshTokenWithAllFields() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token = createValidRefreshToken(user, "refresh.token.abc123");

            RefreshToken savedToken = refreshTokenRepository.save(token);

            assertNotNull(savedToken.getId());
            assertEquals("refresh.token.abc123", savedToken.getToken());
            assertEquals(user.getId(), savedToken.getUser().getId());
            assertNotNull(savedToken.getExpiresAt());
            assertFalse(savedToken.getRevoked());
        }

        @Test
        @DisplayName("Should persist refresh token and retrieve from database")
        void testSaveAndFlush() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token = createValidRefreshToken(user, "refresh.token.xyz789");

            RefreshToken savedToken = refreshTokenRepository.save(token);
            entityManager.flush();
            entityManager.clear();

            Optional<RefreshToken> retrievedToken = refreshTokenRepository.findById(savedToken.getId());

            assertTrue(retrievedToken.isPresent());
            assertEquals("refresh.token.xyz789", retrievedToken.get().getToken());
            assertEquals(user.getId(), retrievedToken.get().getUser().getId());
        }

        // ==== DEFAULT VALUES ====

        @Test
        @DisplayName("Should set revoked to false by default")
        void testDefaultRevokedFalse() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token = createValidRefreshToken(user, "refresh.token.default");

            RefreshToken savedToken = refreshTokenRepository.save(token);
            entityManager.flush();
            entityManager.clear();

            Optional<RefreshToken> retrievedToken = refreshTokenRepository.findById(savedToken.getId());

            assertTrue(retrievedToken.isPresent());
            assertFalse(retrievedToken.get().getRevoked());
        }

        // ==== EXPIRATION TIMESTAMP STORAGE ====

        @Test
        @DisplayName("Should store expiration timestamp correctly")
        void testExpirationTimestampStorage() {
            User user = createAndSaveUser("user@example.com");
            Instant expirationTime = Instant.now().plusSeconds(3600); // 1 hour
            RefreshToken token = createValidRefreshToken(user, "refresh.token.expiry");
            token.setExpiresAt(expirationTime);

            RefreshToken savedToken = refreshTokenRepository.save(token);
            entityManager.flush();
            entityManager.clear();

            Optional<RefreshToken> retrievedToken = refreshTokenRepository.findById(savedToken.getId());

            assertTrue(retrievedToken.isPresent());
            assertEquals(expirationTime.getEpochSecond(), retrievedToken.get().getExpiresAt().getEpochSecond());
        }

        @Test
        @DisplayName("Should store future expiration timestamp")
        void testFutureExpirationTimestamp() {
            User user = createAndSaveUser("user@example.com");
            Instant futureTime = Instant.now().plusSeconds(604800); // 7 days
            RefreshToken token = createValidRefreshToken(user, "refresh.token.future");
            token.setExpiresAt(futureTime);

            RefreshToken savedToken = refreshTokenRepository.save(token);

            assertTrue(savedToken.getExpiresAt().isAfter(Instant.now()));
        }

        @Test
        @DisplayName("Should store past expiration timestamp")
        void testPastExpirationTimestamp() {
            User user = createAndSaveUser("user@example.com");
            Instant pastTime = Instant.now().minusSeconds(3600); // 1 hour ago
            RefreshToken token = createValidRefreshToken(user, "refresh.token.past");
            token.setExpiresAt(pastTime);

            RefreshToken savedToken = refreshTokenRepository.save(token);

            assertTrue(savedToken.getExpiresAt().isBefore(Instant.now()));
        }
    }

    @Nested
    @DisplayName("Find Refresh Token By Token String Tests")
    class FindByTokenTests {

        // ==== SUCCESSFUL RETRIEVAL ====

        @Test
        @DisplayName("Should find refresh token by exact token string match")
        void testFindByTokenExactMatch() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token = createValidRefreshToken(user, "refresh.token.find123");
            refreshTokenRepository.save(token);
            entityManager.flush();

            Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("refresh.token.find123");

            assertTrue(foundToken.isPresent());
            assertEquals("refresh.token.find123", foundToken.get().getToken());
            assertEquals(user.getId(), foundToken.get().getUser().getId());
        }

        @Test
        @DisplayName("Should return empty when token string does not exist")
        void testFindByTokenNotFound() {
            Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("nonexistent.token");

            assertFalse(foundToken.isPresent());
        }

        // ==== MULTIPLE TOKENS ====

        @Test
        @DisplayName("Should find correct token when multiple tokens exist")
        void testFindByTokenWithMultipleTokens() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token1 = createValidRefreshToken(user, "token.one");
            RefreshToken token2 = createValidRefreshToken(user, "token.two");
            RefreshToken token3 = createValidRefreshToken(user, "token.three");
            refreshTokenRepository.save(token1);
            refreshTokenRepository.save(token2);
            refreshTokenRepository.save(token3);
            entityManager.flush();

            Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("token.two");

            assertTrue(foundToken.isPresent());
            assertEquals("token.two", foundToken.get().getToken());
        }

        // ==== REVOKED TOKENS ====

        @Test
        @DisplayName("Should find revoked token by token string")
        void testFindRevokedToken() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token = createValidRefreshToken(user, "revoked.token");
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            entityManager.flush();

            Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("revoked.token");

            assertTrue(foundToken.isPresent());
            assertTrue(foundToken.get().getRevoked());
        }
    }

    @Nested
    @DisplayName("Update Token Revocation Status Tests")
    class UpdateRevocationStatusTests {

        // ==== REVOCATION STATUS UPDATES ====

        @Test
        @DisplayName("Should update token revocation status to true")
        void testRevokeToken() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token = createValidRefreshToken(user, "token.to.revoke");
            RefreshToken savedToken = refreshTokenRepository.save(token);
            entityManager.flush();
            entityManager.clear();

            savedToken.setRevoked(true);
            refreshTokenRepository.save(savedToken);
            entityManager.flush();
            entityManager.clear();

            Optional<RefreshToken> updatedToken = refreshTokenRepository.findById(savedToken.getId());

            assertTrue(updatedToken.isPresent());
            assertTrue(updatedToken.get().getRevoked());
        }

        @Test
        @DisplayName("Should update token revocation status to false")
        void testUnrevokeToken() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token = createValidRefreshToken(user, "token.to.unrevoke");
            token.setRevoked(true);
            RefreshToken savedToken = refreshTokenRepository.save(token);
            entityManager.flush();
            entityManager.clear();

            savedToken.setRevoked(false);
            refreshTokenRepository.save(savedToken);
            entityManager.flush();
            entityManager.clear();

            Optional<RefreshToken> updatedToken = refreshTokenRepository.findById(savedToken.getId());

            assertTrue(updatedToken.isPresent());
            assertFalse(updatedToken.get().getRevoked());
        }

        @Test
        @DisplayName("Should update revocation status without affecting other fields")
        void testRevokeTokenPreservesOtherFields() {
            User user = createAndSaveUser("user@example.com");
            Instant expirationTime = Instant.now().plusSeconds(3600);
            RefreshToken token = createValidRefreshToken(user, "token.preserve");
            token.setExpiresAt(expirationTime);
            RefreshToken savedToken = refreshTokenRepository.save(token);
            entityManager.flush();
            entityManager.clear();

            savedToken.setRevoked(true);
            refreshTokenRepository.save(savedToken);
            entityManager.flush();
            entityManager.clear();

            Optional<RefreshToken> updatedToken = refreshTokenRepository.findById(savedToken.getId());

            assertTrue(updatedToken.isPresent());
            assertTrue(updatedToken.get().getRevoked());
            assertEquals("token.preserve", updatedToken.get().getToken());
            assertEquals(user.getId(), updatedToken.get().getUser().getId());
            assertEquals(expirationTime.getEpochSecond(), updatedToken.get().getExpiresAt().getEpochSecond());
        }

        // ==== MULTIPLE TOKEN UPDATES ====

        @Test
        @DisplayName("Should update only specified token when multiple tokens exist")
        void testRevokeSpecificToken() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token1 = createValidRefreshToken(user, "token.1");
            RefreshToken token2 = createValidRefreshToken(user, "token.2");
            RefreshToken token3 = createValidRefreshToken(user, "token.3");
            RefreshToken savedToken1 = refreshTokenRepository.save(token1);
            RefreshToken savedToken2 = refreshTokenRepository.save(token2);
            RefreshToken savedToken3 = refreshTokenRepository.save(token3);
            entityManager.flush();
            entityManager.clear();

            savedToken2.setRevoked(true);
            refreshTokenRepository.save(savedToken2);
            entityManager.flush();
            entityManager.clear();

            Optional<RefreshToken> updatedToken2 = refreshTokenRepository.findById(savedToken2.getId());
            Optional<RefreshToken> unchangedToken1 = refreshTokenRepository.findById(savedToken1.getId());
            Optional<RefreshToken> unchangedToken3 = refreshTokenRepository.findById(savedToken3.getId());

            assertTrue(updatedToken2.isPresent());
            assertTrue(updatedToken2.get().getRevoked());
            assertTrue(unchangedToken1.isPresent());
            assertFalse(unchangedToken1.get().getRevoked());
            assertTrue(unchangedToken3.isPresent());
            assertFalse(unchangedToken3.get().getRevoked());
        }
    }

    @Nested
    @DisplayName("Delete Token Tests")
    class DeleteTokenTests {

        // ==== DELETION ====

        @Test
        @DisplayName("Should delete token by ID")
        void testDeleteTokenById() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token = createValidRefreshToken(user, "token.to.delete");
            RefreshToken savedToken = refreshTokenRepository.save(token);
            entityManager.flush();

            refreshTokenRepository.deleteById(savedToken.getId());
            entityManager.flush();

            Optional<RefreshToken> deletedToken = refreshTokenRepository.findById(savedToken.getId());

            assertFalse(deletedToken.isPresent());
        }

        @Test
        @DisplayName("Should delete token by entity")
        void testDeleteTokenByEntity() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token = createValidRefreshToken(user, "token.to.delete");
            RefreshToken savedToken = refreshTokenRepository.save(token);
            entityManager.flush();

            refreshTokenRepository.delete(savedToken);
            entityManager.flush();

            Optional<RefreshToken> deletedToken = refreshTokenRepository.findById(savedToken.getId());

            assertFalse(deletedToken.isPresent());
        }

        @Test
        @DisplayName("Should not find token by token string after deletion")
        void testFindByTokenAfterDeletion() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token = createValidRefreshToken(user, "token.deleted");
            RefreshToken savedToken = refreshTokenRepository.save(token);
            entityManager.flush();

            refreshTokenRepository.deleteById(savedToken.getId());
            entityManager.flush();

            Optional<RefreshToken> deletedToken = refreshTokenRepository.findByToken("token.deleted");

            assertFalse(deletedToken.isPresent());
        }

        @Test
        @DisplayName("Should delete only specified token when multiple tokens exist")
        void testDeleteSpecificToken() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token1 = createValidRefreshToken(user, "token.1");
            RefreshToken token2 = createValidRefreshToken(user, "token.2");
            RefreshToken token3 = createValidRefreshToken(user, "token.3");
            RefreshToken savedToken1 = refreshTokenRepository.save(token1);
            RefreshToken savedToken2 = refreshTokenRepository.save(token2);
            RefreshToken savedToken3 = refreshTokenRepository.save(token3);
            entityManager.flush();

            refreshTokenRepository.deleteById(savedToken2.getId());
            entityManager.flush();

            Optional<RefreshToken> deletedToken = refreshTokenRepository.findById(savedToken2.getId());
            Optional<RefreshToken> remainingToken1 = refreshTokenRepository.findById(savedToken1.getId());
            Optional<RefreshToken> remainingToken3 = refreshTokenRepository.findById(savedToken3.getId());

            assertFalse(deletedToken.isPresent());
            assertTrue(remainingToken1.isPresent());
            assertTrue(remainingToken3.isPresent());
        }

    }

    @Nested
    @DisplayName("Foreign Key Relationship Tests")
    class ForeignKeyRelationshipTests {

        // ==== USER RELATIONSHIP ====

        @Test
        @DisplayName("Should maintain foreign key relationship with User")
        void testForeignKeyRelationship() {
            User user = createAndSaveUser("user@example.com");
            RefreshToken token = createValidRefreshToken(user, "token.fk");
            RefreshToken savedToken = refreshTokenRepository.save(token);
            entityManager.flush();
            entityManager.clear();

            Optional<RefreshToken> retrievedToken = refreshTokenRepository.findById(savedToken.getId());

            assertTrue(retrievedToken.isPresent());
            assertNotNull(retrievedToken.get().getUser());
            assertEquals(user.getId(), retrievedToken.get().getUser().getId());
            assertEquals("user@example.com", retrievedToken.get().getUser().getEmail());
        }

        @Test
        @DisplayName("Should not allow saving token without user (foreign key constraint)")
        void testForeignKeyConstraintEnforced() {
            RefreshToken token = new RefreshToken();
            token.setToken("orphan.token");
            token.setExpiresAt(Instant.now().plusSeconds(3600));
            token.setRevoked(false);
            // No user set

            assertThrows(DataIntegrityViolationException.class, () -> {
                refreshTokenRepository.save(token);
                entityManager.flush();
            });
        }

        @Test
        @DisplayName("Should not allow same token string for different users")
        void testSameTokenStringDifferentUsers() {
            User user1 = createAndSaveUser("user1@example.com");
            User user2 = createAndSaveUser("user2@example.com");
            RefreshToken token1 = createValidRefreshToken(user1, "same.token.string");
            RefreshToken token2 = createValidRefreshToken(user2, "same.token.string");

            refreshTokenRepository.save(token1);

            assertThrows(DataIntegrityViolationException.class, () -> {
                refreshTokenRepository.save(token2);
                refreshTokenRepository.flush(); 
            });
        }
    }
}