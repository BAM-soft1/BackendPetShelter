package org.pet.backendpetshelter.unit.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.Configuration.JwtProperties;
import org.pet.backendpetshelter.Configuration.JwtService;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private JwtProperties jwtProperties;
    private static final String TEST_ACCESS_SECRET = "test-access-secret-key-minimum-256-bits-required-for-hs256-algorithm";
    private static final String TEST_REFRESH_SECRET = "test-refresh-secret-key-minimum-256-bits-required-for-hs256-algorithm";
    private static final long ACCESS_EXPIRATION_SECONDS = 900L; // 15 minutes

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setAccessSecret(TEST_ACCESS_SECRET);
        jwtProperties.setRefreshSecret(TEST_REFRESH_SECRET);
        jwtProperties.setAccessExpirationSeconds(ACCESS_EXPIRATION_SECONDS);
        jwtProperties.setRefreshExpirationSeconds(86400L);
        jwtService = new JwtService(jwtProperties);
    }

    // ==================== TEST HELPER ====================

    private Map<String, Object> createValidClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");
        claims.put("uid", 123L);
        return claims;
    }

    @Nested
    @DisplayName("Token Generation & Parsing Tests - generateAccessToken() & parseAccessToken()")
    class TokenGenerationAndParsingTests {

        // ==================== EQUIVALENCE PARTITIONING - VALID PARTITION
        // ====================

        @Test
        @DisplayName("Should generate access token with valid claims")
        void testGenerateAccessTokenWithValidClaims() {
            String subject = "user@example.com";
            Map<String, Object> claims = createValidClaims();

            String token = jwtService.generateAccessToken(subject, claims);

            assertNotNull(token);
            assertFalse(token.isEmpty());
            // JWT format: header.payload.signature
            assertEquals(3, token.split("\\.").length);
        }

        @Test
        @DisplayName("Should parse valid access token successfully")
        void testParseValidAccessToken() {
            String subject = "user@example.com";
            Map<String, Object> claims = createValidClaims();

            String token = jwtService.generateAccessToken(subject, claims);
            Jws<Claims> parsedToken = jwtService.parseAccessToken(token);

            assertNotNull(parsedToken);
            assertNotNull(parsedToken.getBody());
        }

        @Test
        @DisplayName("Should verify token contains correct claims (sub, role, uid, iat, exp)")
        void testTokenContainsCorrectClaims() {
            String subject = "user@example.com";
            Map<String, Object> claims = createValidClaims();

            Instant beforeGeneration = Instant.now();
            String token = jwtService.generateAccessToken(subject, claims);
            Instant afterGeneration = Instant.now();

            Jws<Claims> parsedToken = jwtService.parseAccessToken(token);
            Claims body = parsedToken.getBody();

            // Verify subject (sub)
            assertEquals(subject, body.getSubject());

            // Verify custom claims (role, uid)
            assertEquals("USER", body.get("role"));
            assertEquals(123, body.get("uid", Integer.class));

            // Verify issuedAt (iat)
            Date issuedAt = body.getIssuedAt();
            assertNotNull(issuedAt);
            assertTrue(issuedAt.toInstant().isAfter(beforeGeneration.minusSeconds(1)));
            assertTrue(issuedAt.toInstant().isBefore(afterGeneration.plusSeconds(1)));

            // Verify expiration (exp)
            Date expiration = body.getExpiration();
            assertNotNull(expiration);
        }

        @Test
        @DisplayName("Should verify token expiration is set correctly (15 min for access)")
        void testAccessTokenExpirationSetCorrectly() {
            String subject = "user@example.com";
            Map<String, Object> claims = createValidClaims();

            Instant beforeGeneration = Instant.now();
            String token = jwtService.generateAccessToken(subject, claims);
            Instant afterGeneration = Instant.now();

            Jws<Claims> parsedToken = jwtService.parseAccessToken(token);
            Date expiration = parsedToken.getBody().getExpiration();

            // Verify expiration is ~15 minutes (900 seconds) from now
            Instant expectedExpiration = beforeGeneration.plusSeconds(ACCESS_EXPIRATION_SECONDS);
            Instant expectedExpirationMax = afterGeneration.plusSeconds(ACCESS_EXPIRATION_SECONDS);

            assertTrue(expiration.toInstant().isAfter(expectedExpiration.minusSeconds(2)));
            assertTrue(expiration.toInstant().isBefore(expectedExpirationMax.plusSeconds(2)));
        }

        // ==================== EQUIVALENCE PARTITIONING - INVALID PARTITION 1: EXPIRED
        // TOKEN ====================

        @Test
        @DisplayName("Should throw ExpiredJwtException when parsing expired token")
        void testParseExpiredToken() {
            // Create a JwtService with negative expiration to generate expired token
            JwtProperties expiredProps = new JwtProperties();
            expiredProps.setAccessSecret(TEST_ACCESS_SECRET);
            expiredProps.setRefreshSecret(TEST_REFRESH_SECRET);
            expiredProps.setAccessExpirationSeconds(-1L);
            expiredProps.setRefreshExpirationSeconds(86400L);
            JwtService expiredJwtService = new JwtService(expiredProps);

            String subject = "user@example.com";
            Map<String, Object> claims = createValidClaims();
            String expiredToken = expiredJwtService.generateAccessToken(subject, claims);

            assertThrows(ExpiredJwtException.class, () -> {
                jwtService.parseAccessToken(expiredToken);
            });
        }

        // ==================== EQUIVALENCE PARTITIONING - INVALID PARTITION 2: INVALID
        // SIGNATURE ====================

        @Test
        @DisplayName("Should throw SignatureException when parsing token with invalid signature")
        void testParseTokenWithInvalidSignature() {
            // Create a JwtService with different secret key
            JwtProperties differentProps = new JwtProperties();
            differentProps.setAccessSecret("different-secret-key-minimum-256-bits-required-for-hs256-algorithm-xyz");
            differentProps.setRefreshSecret(TEST_REFRESH_SECRET);
            differentProps.setAccessExpirationSeconds(ACCESS_EXPIRATION_SECONDS);
            differentProps.setRefreshExpirationSeconds(86400L);
            JwtService differentJwtService = new JwtService(differentProps);

            String subject = "user@example.com";
            Map<String, Object> claims = createValidClaims();
            String tokenWithDifferentSignature = differentJwtService.generateAccessToken(subject, claims);

            assertThrows(SignatureException.class, () -> {
                jwtService.parseAccessToken(tokenWithDifferentSignature);
            });
        }

        // ==================== EQUIVALENCE PARTITIONING - INVALID PARTITION 3:
        // MALFORMED TOKEN ====================

        @Test
        @DisplayName("Should throw MalformedJwtException when parsing malformed token")
        void testParseMalformedToken() {
            String malformedToken = "invalid.token.format";

            assertThrows(MalformedJwtException.class, () -> {
                jwtService.parseAccessToken(malformedToken);
            });
        }
    }


        // ==================== WHITEBOX STATEMENT and DECISION COVERAGE TESTS ====================
    @Nested
    @DisplayName("Phase 2: Whitebox Statement & Decision Coverage Tests")
    class WhiteboxCoverageTests {

        // ==== STATEMENT COVERAGE: Refresh Token Generation Path ====

        @Test
        @DisplayName("Should generate refresh token using refresh key and expiration")
        void testGenerateRefreshTokenUsesRefreshKeyAndExpiration() {
            String subject = "user@example.com";
            Map<String, Object> claims = createValidClaims();

            String refreshToken = jwtService.generateRefreshToken(subject, claims);

            assertNotNull(refreshToken);
            assertFalse(refreshToken.isEmpty());
            assertEquals(3, refreshToken.split("\\.").length);
        }

        // ==== STATEMENT COVERAGE: Refresh Token Parsing Path ====

        @Test
        @DisplayName("Should parse refresh token using refresh key")
        void testParseRefreshTokenUsesRefreshKey() {
            String subject = "user@example.com";
            Map<String, Object> claims = createValidClaims();

            String refreshToken = jwtService.generateRefreshToken(subject, claims);
            Jws<Claims> parsedToken = jwtService.parseRefreshToken(refreshToken);

            assertNotNull(parsedToken);
            assertEquals(subject, parsedToken.getBody().getSubject());
            assertEquals("USER", parsedToken.getBody().get("role"));
        }

        // ==== STATEMENT COVERAGE: Refresh Token Expiration Path ====

        @Test
        @DisplayName("Should verify refresh token expiration is set correctly (24 hours)")
        void testRefreshTokenExpirationSetCorrectly() {
            String subject = "user@example.com";
            Map<String, Object> claims = createValidClaims();

            Instant beforeGeneration = Instant.now();
            String refreshToken = jwtService.generateRefreshToken(subject, claims);
            Instant afterGeneration = Instant.now();

            Jws<Claims> parsedToken = jwtService.parseRefreshToken(refreshToken);
            Date expiration = parsedToken.getBody().getExpiration();

            // Verify expiration is ~24 hours (86400 seconds) from now
            long refreshExpSeconds = 86400L;
            Instant expectedExpiration = beforeGeneration.plusSeconds(refreshExpSeconds);
            Instant expectedExpirationMax = afterGeneration.plusSeconds(refreshExpSeconds);

            assertTrue(expiration.toInstant().isAfter(expectedExpiration.minusSeconds(2)));
            assertTrue(expiration.toInstant().isBefore(expectedExpirationMax.plusSeconds(2)));
        }

        // ==== DECISION COVERAGE: Refresh Token Signature Validation (Invalid Branch)
        // ====

        @Test
        @DisplayName("Should throw SignatureException when parsing refresh token with access key")
        void testParseRefreshTokenWithWrongKey() {
            String subject = "user@example.com";
            Map<String, Object> claims = createValidClaims();

            // Generate refresh token (signed with refresh key)
            String refreshToken = jwtService.generateRefreshToken(subject, claims);

            // Try to parse with access key parser - should fail signature validation
            assertThrows(SignatureException.class, () -> {
                jwtService.parseAccessToken(refreshToken);
            });
        }

        // ==== DECISION COVERAGE: Access Token Signature Validation (Invalid Branch)
        // ====

        @Test
        @DisplayName("Should throw SignatureException when parsing access token with refresh key")
        void testParseAccessTokenWithWrongKey() {
            String subject = "user@example.com";
            Map<String, Object> claims = createValidClaims();

            // Generate access token (signed with access key)
            String accessToken = jwtService.generateAccessToken(subject, claims);

            // Try to parse with refresh key parser - should fail signature validation
            assertThrows(SignatureException.class, () -> {
                jwtService.parseRefreshToken(accessToken);
            });
        }

        // ==== DECISION COVERAGE: Refresh Token Expiration Check (Expired Branch) ====

        @Test
        @DisplayName("Should throw ExpiredJwtException when parsing expired refresh token")
        void testParseExpiredRefreshToken() {
            // Create a JwtService with negative expiration for refresh token
            JwtProperties expiredProps = new JwtProperties();
            expiredProps.setAccessSecret(TEST_ACCESS_SECRET);
            expiredProps.setRefreshSecret(TEST_REFRESH_SECRET);
            expiredProps.setAccessExpirationSeconds(ACCESS_EXPIRATION_SECONDS);
            expiredProps.setRefreshExpirationSeconds(-1L);
            JwtService expiredJwtService = new JwtService(expiredProps);

            String subject = "user@example.com";
            Map<String, Object> claims = createValidClaims();
            String expiredRefreshToken = expiredJwtService.generateRefreshToken(subject, claims);

            assertThrows(ExpiredJwtException.class, () -> {
                jwtService.parseRefreshToken(expiredRefreshToken);
            });
        }

        // ==== DECISION COVERAGE: Refresh Token Parsing (Malformed Branch) ====

        @Test
        @DisplayName("Should throw MalformedJwtException when parsing malformed refresh token")
        void testParseMalformedRefreshToken() {
            String malformedToken = "invalid.refresh.token";

            assertThrows(MalformedJwtException.class, () -> {
                jwtService.parseRefreshToken(malformedToken);
            });
        }
    }
}