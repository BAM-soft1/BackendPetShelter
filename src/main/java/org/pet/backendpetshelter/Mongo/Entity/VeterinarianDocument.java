package org.pet.backendpetshelter.Mongo.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "veterinarians")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeterinarianDocument {

    @Id
    private String id;

    private String userId;
    private String licenseNumber;
    private String clinicName;
    private Boolean isActive;
}