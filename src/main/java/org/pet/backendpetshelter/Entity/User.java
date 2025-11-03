package org.pet.backendpetshelter.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Roles;

@Getter
@Setter
@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private Roles role;}
