package org.pet.backendpetshelter;


import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.Breed;
import org.pet.backendpetshelter.Entity.Species;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Reposiotry.AnimalRepository;
import org.pet.backendpetshelter.Reposiotry.BreedRepository;
import org.pet.backendpetshelter.Reposiotry.SpeciesRepository;
import org.pet.backendpetshelter.Reposiotry.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;


@Component
public class InitData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;

    public InitData(UserRepository userRepository, AnimalRepository animalRepository, SpeciesRepository speciesRepository, BreedRepository breedRepository) {
        this.userRepository = userRepository;
        this.animalRepository = animalRepository;
        this.speciesRepository = speciesRepository;
        this.breedRepository = breedRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("Henter data fra databasen");

        /* Users */
        User user1 = new User();
        user1.setEmail("ox1@gmail.com");
        user1.setPassword("test123!");
        user1.setFirstName("ox");
        user1.setLastName("woo");
        user1.setPhone("424242424");
        user1.setIsActive(true);
        user1.setRole(Roles.ADOPTER);


        User savedUser1 = userRepository.save(user1);


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
        Breed savedBreed1 = breedRepository.save(breed1);




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









    }
}



