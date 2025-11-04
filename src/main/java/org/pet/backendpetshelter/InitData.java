package org.pet.backendpetshelter;

import org.pet.backendpetshelter.Entity.*;
import org.pet.backendpetshelter.Reposiotry.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class InitData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final MedicalRecordReposiotry medicalRecordReposiotry;

    public InitData(UserRepository userRepository, AnimalRepository animalRepository,
                    SpeciesRepository speciesRepository, BreedRepository breedRepository,
                    VeterinarianRepository veterinarianRepository, MedicalRecordReposiotry medicalRecordReposiotry) {
        this.userRepository = userRepository;
        this.animalRepository = animalRepository;
        this.speciesRepository = speciesRepository;
        this.breedRepository = breedRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.medicalRecordReposiotry = medicalRecordReposiotry;
    }

    @Override
    public void run(String... args) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("Initialiserer data...");



        /* Users */
        User user1 = new User();
        user1.setEmail("ox1@gmail.com");
        user1.setPassword("test");
        user1.setFirstName("ox");
        user1.setLastName("woo");
        user1.setPhone("424242424");
        user1.setIsActive(true);
        user1.setRole(Roles.ADOPTER);
        User savedUser1 = userRepository.save(user1);

        /* Species */
        Species species1 = new Species();
        species1.setName("Bird");
        Species savedSpecies1 = speciesRepository.save(species1);

        Species dog = speciesRepository.findByName("Dog")
                .orElseThrow(() -> new RuntimeException("Species 'Dog' not found!"));

        /* Breeds */
        Breed breed = breedRepository.findByName("Bulldog")
                .orElseThrow(() -> new RuntimeException("Breed 'Bulldog' not found!"));

        Breed breed1 = new Breed();
        breed1.setName("Parrot");
        breed1.setSpecies(savedSpecies1);
        Breed savedBreed1 = breedRepository.save(breed1);

        /* Animals */
        Animal animal1 = new Animal();
        animal1.setName("rex");
        animal1.setSpecies(dog);
        animal1.setBreed(breed);
        animal1.setBirthDate(dateFormat.parse("2021-03-14"));
        animal1.setSex("male");
        animal1.setIntakeDate(dateFormat.parse("2022-06-27"));
        animal1.setStatus("available");
        animal1.setPrice(100);
        animal1.setIsActive(true);

        Animal animal2 = new Animal();
        animal2.setName("polly");
        animal2.setSpecies(savedSpecies1);
        animal2.setBreed(breed1);
        animal2.setBirthDate(dateFormat.parse("2020-05-20"));
        animal2.setSex("female");
        animal2.setIntakeDate(dateFormat.parse("2023-01-15"));
        animal2.setStatus("available");
        animal2.setPrice(150);
        animal2.setIsActive(true);

        animalRepository.save(animal1);
        animalRepository.save(animal2);

        /* Veterinarian */
        Veterinarian veterinarian1 = new Veterinarian();
        veterinarian1.setUser(savedUser1);
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


        System.out.println("Data initialiseret succesfuldt!");
    }
}