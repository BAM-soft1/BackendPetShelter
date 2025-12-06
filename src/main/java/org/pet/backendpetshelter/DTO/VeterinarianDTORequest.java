package org.pet.backendpetshelter.DTO;


import lombok.Getter;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.User;

@Getter
@Setter
public class VeterinarianDTORequest {
    private Long userId;
    private String licenseNumber;
    private String clinicName;
    private Boolean isActive;
}