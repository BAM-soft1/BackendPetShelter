package org.pet.backendpetshelter.Mongo.Entity;


import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "foster_care")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FosterCareDocument {

    @Id
    private String id;

    private String animalId;
    private String fosterParentUserId;

    private Date startDate;
    private Date endDate;

    private Boolean isActive;
}
