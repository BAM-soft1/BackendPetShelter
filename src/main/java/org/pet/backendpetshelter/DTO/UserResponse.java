package org.pet.backendpetshelter.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pet.backendpetshelter.Roles;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Boolean isActive;
    private Roles role;
}
