package org.pet.backendpetshelter.Mongo.Repository;

import org.pet.backendpetshelter.Mongo.Entity.AdoptionApplicationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdoptionApplicationMongoRepository extends MongoRepository<AdoptionApplicationDocument, String> {}