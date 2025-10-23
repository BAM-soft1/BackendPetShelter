package org.pet.backendpetshelter.Reposiotry;


import org.pet.backendpetshelter.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findById(long id);
    Optional<User> findByEmail(String email);



}
