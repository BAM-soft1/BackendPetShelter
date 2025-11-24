package org.pet.backendpetshelter.Mongo.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vaccination_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationTypeDocument {

    @Id
    private String id;

    private String vaccineName;
    private String description;

    private int durationMonths;
    private int requiredForAdoption;
}