package org.pet.backendpetshelter.Mongo.Repository;

import org.pet.backendpetshelter.Mongo.Entity.AdoptionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdoptionMongoRepository extends MongoRepository<AdoptionDocument, String> {
}