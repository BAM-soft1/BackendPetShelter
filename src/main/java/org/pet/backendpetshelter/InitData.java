package org.pet.backendpetshelter;


import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Reposiotry.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class InitData implements CommandLineRunner {
    private final UserRepository userRepository;

    public InitData(UserRepository userRepository) {
        this.userRepository = userRepository;
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


    }
}



