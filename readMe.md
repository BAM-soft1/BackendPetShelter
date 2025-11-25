1. Clone the Project

Start by cloning the repository from GitHub:

git clone https://github.com/BAM-soft1/BackendPetShelter.git
cd BackendPetShelter

2. Install Dependencies

You need:

Java 17

Maven 3+

MySQL 8.x

(Optional) IntelliJ IDEA or VS Code Spring Extension Pack

Install Maven dependencies:

mvn clean install

3. Set Up the MySQL Test Database

Open your MySQL client (Workbench, CLI, DBeaver, etc.).

Create the test database:

CREATE DATABASE pet_shelter1;


Make sure the MySQL user matches your configuration:

JDBC_DATABASE_URL=jdbc:mysql://localhost:3306/pet_shelter1
JDBC_USERNAME=root
JDBC_PASSWORD=(yourPassword)


The database schema will be generated automatically because your configuration uses:

spring.jpa.hibernate.ddl-auto=create


This means Hibernate creates the tables automatically at startup.

4. Configure Application Properties

# Application
spring.application.name=BackendPetShelter
spring.jpa.hibernate.ddl-auto=create

# Local MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/pet_shelter1
spring.datasource.username=root
spring.datasource.password=(yourPassword)

# Hibernate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# JWT (local only)
security.jwt.access-secret=this_is_a_very_long_random_access_secret_which_is_long_enough_123456
security.jwt.refresh-secret=this_is_a_very_long_random_refresh_secret_which_is_long_enough_654321
security.jwt.access-expiration-seconds=900
security.jwt.refresh-expiration-seconds=1209600

# CORS (local frontend)
cors.allowed-origins=http://localhost:5173


6. Run the Backend

You can run it using Maven:

mvn spring-boot:run

Backend will run on: http://localhost:8080

7. Testing the API

You can test the backend via:

Postman

Swagger UI (automatically generated via Springdoc):

http://localhost:8080/swagger-ui/index.html

8. Import Database Data

Database backup file:
https://drive.google.com/file/d/1sfUT6aVllgShfY-fTKNrPjVe3Oy3QubK/view?usp=sharing

Export SQL from another database

Import it into pet_shelter1 using MySQL Workbench or CLI:

mysql -u root -p pet_shelter1 < dump.sql
