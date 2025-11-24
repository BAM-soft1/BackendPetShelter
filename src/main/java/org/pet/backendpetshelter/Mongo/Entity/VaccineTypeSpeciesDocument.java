package org.pet.backendpetshelter.Mongo.Entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vaccine_type_species")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccineTypeSpeciesDocument {

    @Id
    private String id;

    private String speciesId;
    private String vaccinationTypeId;
}