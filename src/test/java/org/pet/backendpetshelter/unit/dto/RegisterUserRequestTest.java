package org.pet.backendpetshelter.unit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.pet.backendpetshelter.DTO.RegisterUserRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RegisterUserRequest Validation Tests")
class RegisterUserRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==== TEST HELPER ====
    
    private RegisterUserRequest createValidRequest() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("user@example.com");
        request.setFirstName("Sergio");
        request.setLastName("Ramos");
        request.setPhone("+45 12345678");
        request.setPassword("SecureP@ss123");
        return request;
    }

    @Nested
    @DisplayName("Valid Request Tests")
    class ValidRequestTests {

        // ==== EQUIVALENCE PARTITIONING - VALID PARTITION ====
        
        @Test
        @DisplayName("Should pass validation with all valid fields")
        void testValidRequestPassesValidation() {
            RegisterUserRequest request = createValidRequest();

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should pass validation with phone as null (optional field)")
        void testValidRequestWithNullPhone() {
            RegisterUserRequest request = createValidRequest();
            request.setPhone(null);

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

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
            RegisterUserRequest request = createValidRequest();
            request.setEmail(null);

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Should fail validation when email is empty")
        void testEmailEmpty() {
            RegisterUserRequest request = createValidRequest();
            request.setEmail("");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Should fail validation when email is blank (whitespace only)")
        void testEmailBlank() {
            RegisterUserRequest request = createValidRequest();
            request.setEmail("   ");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION: INVALID EMAIL FORMAT ====
        
        @Test
        @DisplayName("Should fail validation when email has invalid format (missing @)")
        void testEmailInvalidFormatMissingAt() {
            RegisterUserRequest request = createValidRequest();
            request.setEmail("userexample.com");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }

        @Test
        @DisplayName("Should fail validation when email has invalid format (missing domain)")
        void testEmailInvalidFormatMissingDomain() {
            RegisterUserRequest request = createValidRequest();
            request.setEmail("user@");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }
    }

    @Nested
    @DisplayName("FirstName Validation Tests")
    class FirstNameValidationTests {

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION: BLANK FIRSTNAME ====
        
        @Test
        @DisplayName("Should fail validation when firstName is null")
        void testFirstNameNull() {
            RegisterUserRequest request = createValidRequest();
            request.setFirstName(null);

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
        }

        @Test
        @DisplayName("Should fail validation when firstName is empty")
        void testFirstNameEmpty() {
            RegisterUserRequest request = createValidRequest();
            request.setFirstName("");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
        }

        // ==== BOUNDARY VALUE ANALYSIS: MIN LENGTH (2) ====
        
        @Test
        @DisplayName("Should fail validation when firstName is below min length (1 char)")
        void testFirstNameBelowMinLength() {
            RegisterUserRequest request = createValidRequest();
            request.setFirstName("J");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
        }

        @Test
        @DisplayName("Should pass validation when firstName is at min length (2 chars)")
        void testFirstNameAtMinLength() {
            RegisterUserRequest request = createValidRequest();
            request.setFirstName("Jo");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        // ==== BOUNDARY VALUE ANALYSIS: MAX LENGTH (80) ====
        
        @Test
        @DisplayName("Should pass validation when firstName is at max length (80 chars)")
        void testFirstNameAtMaxLength() {
            RegisterUserRequest request = createValidRequest();
            request.setFirstName("J".repeat(80));

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when firstName is above max length (81 chars)")
        void testFirstNameAboveMaxLength() {
            RegisterUserRequest request = createValidRequest();
            request.setFirstName("J".repeat(81));

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("firstName")));
        }
    }

    @Nested
    @DisplayName("LastName Validation Tests")
    class LastNameValidationTests {

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION: BLANK LASTNAME ====
        
        @Test
        @DisplayName("Should fail validation when lastName is null")
        void testLastNameNull() {
            RegisterUserRequest request = createValidRequest();
            request.setLastName(null);

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
        }

        @Test
        @DisplayName("Should fail validation when lastName is empty")
        void testLastNameEmpty() {
            RegisterUserRequest request = createValidRequest();
            request.setLastName("");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
        }

        // ==== BOUNDARY VALUE ANALYSIS: MIN LENGTH (2) ====
        
        @Test
        @DisplayName("Should fail validation when lastName is below min length (1 char)")
        void testLastNameBelowMinLength() {
            RegisterUserRequest request = createValidRequest();
            request.setLastName("D");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
        }

        @Test
        @DisplayName("Should pass validation when lastName is at min length (2 chars)")
        void testLastNameAtMinLength() {
            RegisterUserRequest request = createValidRequest();
            request.setLastName("Do");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        // ==== BOUNDARY VALUE ANALYSIS: MAX LENGTH (80) ====
        
        @Test
        @DisplayName("Should pass validation when lastName is at max length (80 chars)")
        void testLastNameAtMaxLength() {
            RegisterUserRequest request = createValidRequest();
            request.setLastName("D".repeat(80));

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when lastName is above max length (81 chars)")
        void testLastNameAboveMaxLength() {
            RegisterUserRequest request = createValidRequest();
            request.setLastName("D".repeat(81));

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("lastName")));
        }
    }

    @Nested
    @DisplayName("Phone Validation Tests")
    class PhoneValidationTests {

        // ==== EQUIVALENCE PARTITIONING - VALID PARTITION ====
        
        @Test
        @DisplayName("Should pass validation with valid phone formats")
        void testValidPhoneFormats() {
            RegisterUserRequest request = createValidRequest();
            
            String[] validPhones = {
                "+45 12345678",
                "12345678",
                "+1-234-567-8901",
                "(123) 456-7890",
                "123-456-7890"
            };

            for (String phone : validPhones) {
                request.setPhone(phone);
                Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
                assertTrue(violations.isEmpty(), "Phone should be valid: " + phone);
            }
        }

        // ==== BOUNDARY VALUE ANALYSIS: MIN LENGTH (7) ====
        
        @Test
        @DisplayName("Should fail validation when phone is below min length (6 chars)")
        void testPhoneBelowMinLength() {
            RegisterUserRequest request = createValidRequest();
            request.setPhone("123456");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
        }

        @Test
        @DisplayName("Should pass validation when phone is at min length (7 chars)")
        void testPhoneAtMinLength() {
            RegisterUserRequest request = createValidRequest();
            request.setPhone("1234567");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        // ==== BOUNDARY VALUE ANALYSIS: MAX LENGTH (32) ====
        
        @Test
        @DisplayName("Should pass validation when phone is at max length (32 chars)")
        void testPhoneAtMaxLength() {
            RegisterUserRequest request = createValidRequest();
            request.setPhone("1".repeat(32));

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when phone is above max length (33 chars)")
        void testPhoneAboveMaxLength() {
            RegisterUserRequest request = createValidRequest();
            request.setPhone("1".repeat(33));

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
        }

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION: INVALID PATTERN ====
        
        @Test
        @DisplayName("Should fail validation when phone contains invalid characters")
        void testPhoneInvalidCharacters() {
            RegisterUserRequest request = createValidRequest();
            
            String[] invalidPhones = {
                "12345abc",
                "phone123",
                "123@456",
                "123#456"
            };

            for (String phone : invalidPhones) {
                request.setPhone(phone);
                Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
                assertFalse(violations.isEmpty(), "Phone should be invalid: " + phone);
            }
        }
    }

    @Nested
    @DisplayName("Password Validation Tests")
    class PasswordValidationTests {

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION: BLANK PASSWORD ====
        
        @Test
        @DisplayName("Should fail validation when password is null")
        void testPasswordNull() {
            RegisterUserRequest request = createValidRequest();
            request.setPassword(null);

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        }

        @Test
        @DisplayName("Should fail validation when password is empty")
        void testPasswordEmpty() {
            RegisterUserRequest request = createValidRequest();
            request.setPassword("");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        }

        // ==== BOUNDARY VALUE ANALYSIS: MIN LENGTH (7) ====
        
        @Test
        @DisplayName("Should fail validation when password is below min length (6 chars)")
        void testPasswordBelowMinLength() {
            RegisterUserRequest request = createValidRequest();
            request.setPassword("Pass@1");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        }

        @Test
        @DisplayName("Should pass validation when password is at min length (7 chars) with special char")
        void testPasswordAtMinLength() {
            RegisterUserRequest request = createValidRequest();
            request.setPassword("Pass@12");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        // ==== EQUIVALENCE PARTITIONING - INVALID PARTITION: MISSING SPECIAL CHAR ====
        
        @Test
        @DisplayName("Should fail validation when password lacks special character")
        void testPasswordMissingSpecialCharacter() {
            RegisterUserRequest request = createValidRequest();
            request.setPassword("Password123");

            Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
        }

        // ==== EQUIVALENCE PARTITIONING - VALID PARTITION: VARIOUS SPECIAL CHARS ====
        
        @Test
        @DisplayName("Should pass validation with various special characters")
        void testPasswordWithVariousSpecialCharacters() {
            RegisterUserRequest request = createValidRequest();
            
            String[] validPasswords = {
                "Pass!123",
                "Pass@123",
                "Pass#123",
                "Pass$123",
                "Pass%123",
                "Pass^123",
                "Pass&123",
                "Pass*123"
            };

            for (String password : validPasswords) {
                request.setPassword(password);
                Set<ConstraintViolation<RegisterUserRequest>> violations = validator.validate(request);
                assertTrue(violations.isEmpty(), "Password should be valid: " + password);
            }
        }
    }
}