package org.pet.backendpetshelter.Mongo.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "vaccinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationDocument {

    @Id
    private String id;

    private String animalId;
    private String veterinarianId;
    private String vaccinationTypeId;

    private Date dateAdministered;
    private Date nextDueDate;
}