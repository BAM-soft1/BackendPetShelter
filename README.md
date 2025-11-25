## Data Migration: SQL → MongoDB

This project includes a simple migration setup that copies data from the relational database (MySQL/H2 via JPA) into MongoDB.

Follow these steps:

### 1. Run the application without migration (create the relational DB)

Make sure the relational database is configured and running (e.g. MySQL on `localhost:3306` as defined in `application.properties`).

In `application.properties`, disable migration:

```properties
migration.enabled=false
```

Run the application to create the schema and the initData should insert data in the schemas.

### 2. Start a MongoDB server (Docker)

Start a `MongoDB` instance using:

```
docker run -d --name mongo-pet-shelter -p 27017:27017 mongo:latest
```

This will run MongoDB locally on port 27017 without authentication.

### 3. Configure MongoDB in application.properties

Point Spring Data MongoDB to the running MongoDB instance:

```
spring.data.mongodb.uri=mongodb://localhost:27017/pet_shelter_mongo
```

### 4. Enable migration

Turn on the migration flag in `application.properties`:

```
migration.enabled=true
```

This tells the application to run the migrator components on startup.

### 5. Run the project again (perform migration)

Run the application again.

On startup, the migrators will:

Read all data from the relational database via JPA repositories.
Map the entities to MongoDB documents.
Save them into the pet_shelter_mongo database.

You can verify the migration by connecting to MongoDB (e.g. using mongosh or MongoDB Compass) and checking the collections such as users, animals, adoptions, etc.
