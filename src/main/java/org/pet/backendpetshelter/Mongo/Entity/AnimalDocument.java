package org.pet.backendpetshelter.Mongo.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "animals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalDocument {

    @Id
    private String id;

    private String name;

    // store foreign keys as strings (id values from SQL)
    private String speciesId;
    private String breedId;

    private Date birthDate;
    private String sex;
    private Date intakeDate;

    private String status;
    private int price;
    private Boolean isActive;
    private String imageUrl;
}