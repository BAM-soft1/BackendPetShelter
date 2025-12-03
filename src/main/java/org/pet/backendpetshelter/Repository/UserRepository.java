package org.pet.backendpetshelter.Repository;


import org.pet.backendpetshelter.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findById(long id);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);


}
