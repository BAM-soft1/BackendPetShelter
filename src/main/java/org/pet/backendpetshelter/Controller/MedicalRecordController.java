package org.pet.backendpetshelter.Controller;


import org.pet.backendpetshelter.DTO.MedicalRecordDTORequest;
import org.pet.backendpetshelter.DTO.MedicalRecordDTOResponse;
import org.pet.backendpetshelter.Service.MedicalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-record")
@CrossOrigin
public class MedicalRecordController {


    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }


    @GetMapping
    public List<MedicalRecordDTOResponse> getAllMedicalRecords() {
        return medicalRecordService.getAllMedicalRecords();
    }

    @GetMapping("/{id}")
    public MedicalRecordDTOResponse getMedicalRecordById(Long id) {
        return medicalRecordService.getMedicalRecordById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<MedicalRecordDTOResponse> addMedicalRecord(@RequestBody MedicalRecordDTORequest medicalRecordDTORequest) {
        return ResponseEntity.status(201).body(medicalRecordService.addMedicalRecord(medicalRecordDTORequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MedicalRecordDTOResponse> updateMedicalRecord(@PathVariable Long id, @RequestBody MedicalRecordDTORequest medicalRecordDTORequest) {
        MedicalRecordDTOResponse updatedMedicalRecord = medicalRecordService.updateMedicalRecord(id, medicalRecordDTORequest);
        return ResponseEntity.ok(updatedMedicalRecord);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id){
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }
}