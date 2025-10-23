package org.pet.backendpetshelter.DTO;

import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Roles;

@Getter
@Setter
public class UserUpdateDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private int phone;
    private Boolean isActive;
    private Roles role;
}
