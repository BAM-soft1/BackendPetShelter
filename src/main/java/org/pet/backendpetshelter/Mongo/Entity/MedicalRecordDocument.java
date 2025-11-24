package org.pet.backendpetshelter.Mongo.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordDocument {

    @Id
    private String id;

    private String animalId;
    private String veterinarianId;

    private Date date;
    private String diagnosis;
    private String treatment;
    private int cost;
}