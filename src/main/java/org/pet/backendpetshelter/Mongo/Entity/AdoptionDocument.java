package org.pet.backendpetshelter.Mongo.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "adoptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionDocument {

    @Id
    private String id;

    private String adoptionUserId;
    private String animalId;
    private String applicationId;
    private Date adoptionDate;
    private Boolean isActive;
}
