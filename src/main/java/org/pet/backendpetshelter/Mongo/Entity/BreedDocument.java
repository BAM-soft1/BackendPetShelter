package org.pet.backendpetshelter.Mongo.Entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "breeds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreedDocument {

    @Id
    private String id;

    private String speciesId;
    private String name;
}
