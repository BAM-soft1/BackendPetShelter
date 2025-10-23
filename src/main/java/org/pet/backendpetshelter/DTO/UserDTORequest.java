package org.pet.backendpetshelter.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Roles;

@Getter
@Setter

public class UserDTORequest {
   @Email(message = "Email must be valid")
    private String email;

   @NotBlank(message = "Firstname cannot be blank")
   private String firstName;

   @NotBlank(message = "Lastname cannot be blank")
    private String lastName;

    private String password;
    private String phone;
    private Boolean isActive;
    private Roles role;


}
