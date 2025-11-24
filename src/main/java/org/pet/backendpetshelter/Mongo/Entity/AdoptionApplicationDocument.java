package org.pet.backendpetshelter.Mongo.Entity;

import lombok.*;
import org.pet.backendpetshelter.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "adoption_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionApplicationDocument {

    @Id
    private String id;

    private String userId;
    private String animalId;
    private Date applicationDate;
    private Status status;
    private String reviewedByUserId;
    private Boolean isActive;
}