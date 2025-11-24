package org.pet.backendpetshelter.Mongo.Entity;

import lombok.*;
import org.pet.backendpetshelter.Roles;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDocument {

    @Id
    private String id;

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Boolean isActive;
    private Roles role;
}