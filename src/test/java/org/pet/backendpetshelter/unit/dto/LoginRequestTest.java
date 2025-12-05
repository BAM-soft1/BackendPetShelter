package org.pet.backendpetshelter.unit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.DTO.LoginRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginRequest Validation Tests")
class LoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==== TEST HELPER ====
    
    private LoginRequest createValidRequest() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("SecurePassword123");
        return request;
    }

    @Nested
    @DisplayName("Valid Request Tests")
    class ValidRequestTests {

        // ==== EQUIVALENCE PARTITIONING - VALID PARTITION ====
        
        @Test
        @DisplayName("Should pass validation with all valid fields")
        void testValidRequestPassesValidation() {
            LoginRequest request = createValidRequest();

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION: BLANK EMAIL ====
        
        @Test
        @DisplayName("Should fail validation when email is null")
        void testEmailNull() {
            LoginRequest request = createValidRequest();
            request.setEmail(null);

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Should fail validation when email is empty")
        void testEmailEmpty() {
            LoginRequest request = createValidRequest();
            request.setEmail("");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Should fail validation when email is blank (whitespace only)")
        void testEmailBlank() {
            LoginRequest request = createValidRequest();
            request.setEmail("   ");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION: INVALID EMAIL FORMAT ====
        
        @Test
        @DisplayName("Should fail validation when email has invalid format (missing @)")
        void testEmailInvalidFormatMissingAt() {
            LoginRequest request = createValidRequest();
            request.setEmail("userexample.com");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Should fail validation when email has invalid format (missing domain)")
        void testEmailInvalidFormatMissingDomain() {
            LoginRequest request = createValidRequest();
            request.setEmail("user@");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Should fail validation when email has invalid format (no local part)")
        void testEmailInvalidFormatNoLocalPart() {
            LoginRequest request = createValidRequest();
            request.setEmail("@example.com");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }
    }

    @Nested
    @DisplayName("Password Validation Tests")
    class PasswordValidationTests {

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION: BLANK PASSWORD ====
        
        @Test
        @DisplayName("Should fail validation when password is null")
        void testPasswordNull() {
            LoginRequest request = createValidRequest();
            request.setPassword(null);

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        }

        @Test
        @DisplayName("Should fail validation when password is empty")
        void testPasswordEmpty() {
            LoginRequest request = createValidRequest();
            request.setPassword("");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        }

        @Test
        @DisplayName("Should fail validation when password is blank (whitespace only)")
        void testPasswordBlank() {
            LoginRequest request = createValidRequest();
            request.setPassword("   ");

            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        }

        // ==== EQUIVALENCE PARTITIONING - VALID PARTITION ====
        
        @Test
        @DisplayName("Should pass validation with any non-blank password")
        void testPasswordNonBlank() {
            LoginRequest request = createValidRequest();
            
            String[] validPasswords = {
                "a",
                "short",
                "NoSpecialChar",
                "VeryLongPasswordWithoutAnyConstraints123456789"
            };

            for (String password : validPasswords) {
                request.setPassword(password);
                Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
                assertTrue(violations.isEmpty(), "Password should be valid: " + password);
            }
        }
    }
}