package org.pet.backendpetshelter.DTO;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Roles;

@Getter
@Setter
@NoArgsConstructor
public class UserDTOResponse {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private int phone;
    private Boolean isActive;
    private Roles role;



    public UserDTOResponse(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
        this.isActive = user.getIsActive();
        this.role = user.getRole();
    }
}


