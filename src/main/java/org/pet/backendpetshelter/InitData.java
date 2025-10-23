package org.pet.backendpetshelter;


import org.pet.backendpetshelter.Entity.Animal;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Reposiotry.AnimalRepository;
import org.pet.backendpetshelter.Reposiotry.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class InitData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;

    public InitData(UserRepository userRepository, AnimalRepository animalRepository) {
        this.userRepository = userRepository;
        this.animalRepository = animalRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("Henter data fra databasen");

        /* Users */
        User user1 = new User();
        user1.setEmail("ox1@gmail.com");
        user1.setPassword("test");
        user1.setFirstName("ox");
        user1.setLastName("woo");
        user1.setPhone(424242424);
        user1.setIsActive(true);
        user1.setRole(Roles.ADOPTER);


        User savedUser1 = userRepository.save(user1);


        /* Animals */
        Animal animal1 = new Animal();
        animal1.setName("Buddy");
        animal1.setSpecies("Dog");
        animal1.setBirthDate(java.time.LocalDateTime.parse("2020-05-15 10:00:00", formatter));
        animal1.setIntakeDate(java.time.LocalDateTime.parse("2021-06-20 14:30:00", formatter));
        animal1.setPrice(300.0);
        animal1.setIsActive(true);


        Animal savedAnimal1 = animalRepository.save(animal1);


    }
}



