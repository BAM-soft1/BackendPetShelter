package org.pet.backendpetshelter.DTO;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pet.backendpetshelter.Entity.User;
import org.pet.backendpetshelter.Entity.Veterinarian;

@Getter
@Setter
@NoArgsConstructor
public class VeterinarianDTOResponse {
    private Long id;
    private User user;
    private String licenseNumber;
    private String clinicName;
    private Boolean isActive;


    public VeterinarianDTOResponse(Veterinarian veterinarian) {
        this.id = veterinarian.getId();
        this.user = veterinarian.getUser();
        this.licenseNumber = veterinarian.getLicenseNumber();
        this.clinicName = veterinarian.getClinicName();
        this.isActive = veterinarian.getIsActive();
    }
}