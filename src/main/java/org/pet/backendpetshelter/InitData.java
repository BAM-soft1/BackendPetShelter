package org.pet.backendpetshelter;


import org.pet.backendpetshelter.Entity.*;
import org.pet.backendpetshelter.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;


@Component
@Order(1) // Run first, before DatabaseFeaturesInitializer
public class InitData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final MedicalRecordRepository medicalRecordReposiotry;
    private final VeterinarianRepository veterinarianRepository;
    private final VaccinationRepository vaccinationRepository;
    private final VaccinationTypeRepository vaccinationTypeRepository;
    private final VaccineTypeSpeciesRepository vaccineTypeSpeciesRepository;
    private final FosterCareRepository fosterCareRepository;
    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final AdoptionRepository adoptionRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public InitData(UserRepository userRepository, AnimalRepository animalRepository, SpeciesRepository speciesRepository, BreedRepository breedRepository, MedicalRecordRepository medicalRecordReposiotry, VeterinarianRepository veterinarianRepository, VaccinationRepository vaccinationRepository, VaccinationTypeRepository vaccinationTypeRepository, VaccineTypeSpeciesRepository vaccineTypeSpeciesRepository, FosterCareRepository fosterCareRepository, AdoptionApplicationRepository adoptionApplicationRepository, AdoptionRepository adoptionRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.animalRepository = animalRepository;
        this.speciesRepository = speciesRepository;
        this.breedRepository = breedRepository;
        this.medicalRecordReposiotry = medicalRecordReposiotry;
        this.veterinarianRepository = veterinarianRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.vaccinationTypeRepository = vaccinationTypeRepository;
        this.vaccineTypeSpeciesRepository = vaccineTypeSpeciesRepository;
        this.fosterCareRepository = fosterCareRepository;
        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.adoptionRepository = adoptionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Uncomment the line below to seed the database with initial data
        populateDatabase();
        System.out.println("Application started.");
    }

    public void populateDatabase() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("Seeding database with initial data...");

        /* Users */
        User user1 = new User();
        user1.setEmail("ox1@gmail.com");
        user1.setPassword(passwordEncoder.encode("test123!"));
        user1.setFirstName("ox");
        user1.setLastName("woo");
        user1.setPhone("424242424");
        user1.setIsActive(true);
        user1.setRole(Roles.ADMIN);


        userRepository.save(user1);


        Species species1 = new Species();
        species1.setName("Bird");
        Species savedSpecies1 = speciesRepository.save(species1);

        Species species2 = new Species();
        species2.setName("Dog");
        speciesRepository.save(species2);

        Breed breed2 = new Breed();
        breed2.setName("Bulldog");
        breed2.setSpecies(species2);
        breedRepository.save(breed2);

        Species dog = speciesRepository.findByName("Dog")
                .orElseThrow(() -> new RuntimeException("Species 'Dog' not found!"));

        Breed breed = breedRepository.findByName("Bulldog")
                .orElseThrow(() -> new RuntimeException("Breed 'Labrador' not found!"));


        Breed breed1 = new Breed();
        breed1.setName("Parrot");
        breed1.setSpecies(savedSpecies1);
        breedRepository.save(breed1);




        // Create more species and breeds
        Species cat = new Species();
        cat.setName("Cat");
        speciesRepository.save(cat);

        Species rabbit = new Species();
        rabbit.setName("Rabbit");
        speciesRepository.save(rabbit);

        Breed labrador = new Breed();
        labrador.setName("Labrador");
        labrador.setSpecies(species2);
        breedRepository.save(labrador);

        Breed goldenRetriever = new Breed();
        goldenRetriever.setName("Golden Retriever");
        goldenRetriever.setSpecies(species2);
        breedRepository.save(goldenRetriever);

        Breed siamese = new Breed();
        siamese.setName("Siamese");
        siamese.setSpecies(cat);
        breedRepository.save(siamese);

        Breed persian = new Breed();
        persian.setName("Persian");
        persian.setSpecies(cat);
        breedRepository.save(persian);

        Breed dutchRabbit = new Breed();
        dutchRabbit.setName("Dutch");
        dutchRabbit.setSpecies(rabbit);
        breedRepository.save(dutchRabbit);

        // Animal 1: Rex (Bulldog)
        Animal animal1 = new Animal();
        animal1.setName("Rex");
        animal1.setSpecies(dog);
        animal1.setBreed(breed);
        animal1.setBirthDate(dateFormat.parse("2021-03-14"));
        animal1.setSex("male");
        animal1.setIntakeDate(dateFormat.parse("2022-06-27"));
        animal1.setStatus("available");
        animal1.setPrice(250);
        animal1.setIsActive(true);
        animal1.setImageUrl("https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=800");
        animalRepository.save(animal1);

        // Animal 2: Polly (Parrot)
        Animal animal2 = new Animal();
        animal2.setName("Polly");
        animal2.setSpecies(savedSpecies1);
        animal2.setBreed(breed1);
        animal2.setBirthDate(dateFormat.parse("2020-05-20"));
        animal2.setSex("female");
        animal2.setIntakeDate(dateFormat.parse("2023-01-15"));
        animal2.setStatus("available");
        animal2.setPrice(150);
        animal2.setIsActive(true);
        animal2.setImageUrl("https://images.unsplash.com/photo-1552728089-57bdde30beb3?w=800");
        animalRepository.save(animal2);

        // Animal 3: Max (Labrador)
        Animal animal3 = new Animal();
        animal3.setName("Max");
        animal3.setSpecies(dog);
        animal3.setBreed(labrador);
        animal3.setBirthDate(dateFormat.parse("2019-08-10"));
        animal3.setSex("male");
        animal3.setIntakeDate(dateFormat.parse("2023-03-20"));
        animal3.setStatus("available");
        animal3.setPrice(300);
        animal3.setIsActive(true);
        animal3.setImageUrl("https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=800");
        animalRepository.save(animal3);

        // Animal 4: Luna (Siamese Cat)
        Animal animal4 = new Animal();
        animal4.setName("Luna");
        animal4.setSpecies(cat);
        animal4.setBreed(siamese);
        animal4.setBirthDate(dateFormat.parse("2022-01-15"));
        animal4.setSex("female");
        animal4.setIntakeDate(dateFormat.parse("2023-05-10"));
        animal4.setStatus("available");
        animal4.setPrice(200);
        animal4.setIsActive(true);
        animal4.setImageUrl("https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?w=800");
        animalRepository.save(animal4);

        // Animal 5: Bella (Golden Retriever)
        Animal animal5 = new Animal();
        animal5.setName("Bella");
        animal5.setSpecies(dog);
        animal5.setBreed(goldenRetriever);
        animal5.setBirthDate(dateFormat.parse("2020-11-05"));
        animal5.setSex("female");
        animal5.setIntakeDate(dateFormat.parse("2023-07-22"));
        animal5.setStatus("available");
        animal5.setPrice(350);
        animal5.setIsActive(true);
        animal5.setImageUrl("https://images.unsplash.com/photo-1633722715463-d30f4f325e24?w=800");
        animalRepository.save(animal5);

        // Animal 6: Whiskers (Persian Cat)
        Animal animal6 = new Animal();
        animal6.setName("Whiskers");
        animal6.setSpecies(cat);
        animal6.setBreed(persian);
        animal6.setBirthDate(dateFormat.parse("2021-06-18"));
        animal6.setSex("male");
        animal6.setIntakeDate(dateFormat.parse("2023-08-05"));
        animal6.setStatus("available");
        animal6.setPrice(250);
        animal6.setIsActive(true);
        animal6.setImageUrl("https://images.unsplash.com/photo-1495360010541-f48722b34f7d?w=800");
        animalRepository.save(animal6);

        // Animal 7: Charlie (Dog - no specific breed)
        Animal animal7 = new Animal();
        animal7.setName("Charlie");
        animal7.setSpecies(dog);
        animal7.setBreed(null);
        animal7.setBirthDate(dateFormat.parse("2018-04-22"));
        animal7.setSex("male");
        animal7.setIntakeDate(dateFormat.parse("2023-09-12"));
        animal7.setStatus("available");
        animal7.setPrice(200);
        animal7.setIsActive(true);
        animal7.setImageUrl("https://images.unsplash.com/photo-1568572933382-74d440642117?w=800");
        animalRepository.save(animal7);

        // Animal 8: Snowball (Dutch Rabbit)
        Animal animal8 = new Animal();
        animal8.setName("Snowball");
        animal8.setSpecies(rabbit);
        animal8.setBreed(dutchRabbit);
        animal8.setBirthDate(dateFormat.parse("2023-02-10"));
        animal8.setSex("female");
        animal8.setIntakeDate(dateFormat.parse("2023-10-01"));
        animal8.setStatus("available");
        animal8.setPrice(100);
        animal8.setIsActive(true);
        animal8.setImageUrl("https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=800");
        animalRepository.save(animal8);

        // Animal 9: Mittens (Cat - no specific breed)
        Animal animal9 = new Animal();
        animal9.setName("Mittens");
        animal9.setSpecies(cat);
        animal9.setBreed(null);
        animal9.setBirthDate(dateFormat.parse("2022-07-30"));
        animal9.setSex("female");
        animal9.setIntakeDate(dateFormat.parse("2023-10-15"));
        animal9.setStatus("available");
        animal9.setPrice(150);
        animal9.setIsActive(true);
        animal9.setImageUrl("https://images.unsplash.com/photo-1574158622682-e40e69881006?w=800");
        animalRepository.save(animal9);

        // Animal 10: Buddy (Labrador - adopted)
        Animal animal10 = new Animal();
        animal10.setName("Buddy");
        animal10.setSpecies(dog);
        animal10.setBreed(labrador);
        animal10.setBirthDate(dateFormat.parse("2019-12-01"));
        animal10.setSex("male");
        animal10.setIntakeDate(dateFormat.parse("2023-01-05"));
        animal10.setStatus("adopted");
        animal10.setPrice(300);
        animal10.setIsActive(false);
        animal10.setImageUrl("https://images.unsplash.com/photo-1558788353-f76d92427f16?w=800");
        animalRepository.save(animal10);


        /* Veterinarian */
        Veterinarian veterinarian1 = new Veterinarian();
        veterinarian1.setUser(user1);
        veterinarian1.setLicenseNumber("VET123456");
        veterinarian1.setClinicName("Happy Pets Clinic");
        veterinarian1.setIsActive(true);
        veterinarianRepository.save(veterinarian1);


        /* Medical Records */
        MedicalRecord medicalRecord1 = new MedicalRecord();
        medicalRecord1.setAnimal(animal1);
        medicalRecord1.setVeterinarian(veterinarian1);
        medicalRecord1.setDate(dateFormat.parse("2023-07-10"));
        medicalRecord1.setDiagnosis("Regular Checkup - Healthy");
        medicalRecord1.setTreatment("N/A");
        medicalRecord1.setCost(50);
        medicalRecordReposiotry.save(medicalRecord1);


        /* Vaccination Type */
        VaccinationType vaccinationType = new VaccinationType();
        vaccinationType.setVaccineName("Rabies Vaccine");
        vaccinationType.setDescription("Protects against rabies virus.");
        vaccinationType.setDurationMonths(12);
        vaccinationType.setRequiredForAdoption(1);
        vaccinationTypeRepository.save(vaccinationType);




        /* Vaccination */
        Vaccination vaccination1 = new Vaccination();
        vaccination1.setAnimal(animal1);
        vaccination1.setVeterinarian(veterinarian1);
        vaccination1.setVaccinationType(vaccinationType);
        vaccination1.setDateAdministered(dateFormat.parse("2023-07-10"));
        vaccination1.setNextDueDate(dateFormat.parse("2024-07-10"));
        vaccinationRepository.save(vaccination1);


        /* Vaccine Type Species */
        VaccineTypeSpecies vts1 = new VaccineTypeSpecies();
        vts1.setSpecies(dog);
        vts1.setVaccinationType(vaccinationType);
        vaccineTypeSpeciesRepository.save(vts1);


        /* Foster Care */
        FosterCare fosterCare1 = new FosterCare();
        fosterCare1.setFosterParent(user1);
        fosterCare1.setAnimal(animal2);
        fosterCare1.setStartDate(dateFormat.parse("2023-08-01"));
        fosterCare1.setEndDate(dateFormat.parse("2023-09-01"));
        fosterCare1.setIsActive(true);
        fosterCareRepository.save(fosterCare1);


        /* Adoption Application */
        AdoptionApplication adoptionApplication1 = new AdoptionApplication();
        adoptionApplication1.setUser(user1);
        adoptionApplication1.setAnimal(animal3);
        adoptionApplication1.setApplicationDate(dateFormat.parse("2023-09-15"));
        adoptionApplication1.setStatus(Status.PENDING);
        adoptionApplication1.setReviewedByUser(null);
        adoptionApplication1.setIsActive(true);
        adoptionApplicationRepository.save(adoptionApplication1);


        /* Adoption */
        Adoption adoption1 = new Adoption();
        adoption1.setAdoptionUser(user1);
        adoption1.setAnimal(animal10);
        adoption1.setApplication(adoptionApplication1);
        adoption1.setAdoptionDate(dateFormat.parse("2023-10-20"));
        adoption1.setIsActive(true);
        adoptionRepository.save(adoption1);




        System.out.println("Database seeded successfully with initial data!");
    }
}



