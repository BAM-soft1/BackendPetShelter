package org.pet.backendpetshelter.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Roles;

@Getter
@Setter
public class RegisterUserRequest {
    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 2, max = 80)
    private String firstName;

    @NotBlank @Size(min = 2, max = 80)
    private String lastName;

    @Pattern(regexp = "^[0-9+\\-() ]{7,32}$", message = "Invalid phone format")
    private String phone;

    @NotBlank @Size(min = 7, message = "Password must be at least 7 characters")
    @Pattern(regexp = ".*[!@#$%^&*()_+=\\-{}:;\"'<>,.?/|\\[\\]\\\\].*",
            message = "Password must include at least one special character")
    private String password;

    // Ignoreres ved register (vi sætter USER) – kun admin kan ændre senere
    private Roles role;
}
