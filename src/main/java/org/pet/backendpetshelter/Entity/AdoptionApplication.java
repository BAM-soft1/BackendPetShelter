package org.pet.backendpetshelter.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Status;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "adoption_application")
public class AdoptionApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adoption_application_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    private Date applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "reviewed_by_user_id")
    private User reviewedByUser;

    private Boolean isActive;




}
