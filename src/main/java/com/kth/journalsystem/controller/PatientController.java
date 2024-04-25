package com.kth.journalsystem.controller;

import com.kth.journalsystem.domain.Patient;
import com.kth.journalsystem.dto.OrderDTO;
import com.kth.journalsystem.dto.PatientDTO;
import com.kth.journalsystem.dto.PatientRequestDTO;
import com.kth.journalsystem.service.producer.PatientEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientEventProducer patientEventProducer;

    @PostMapping("/")
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patient) {
        try {
            patientEventProducer.sendCreatePatientEvent(patient);
            PatientDTO createdPatientDTO = new PatientDTO(patient.getId(), patient.getFirstName(), patient.getLastName(), patient.getAge());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPatientDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/")
    public ResponseEntity<String> getPatient(@RequestParam("patientId") Long patientId) {
        try {
            patientEventProducer.sendReadPatientEvent(patientId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Retrieving patient with id: " + patientId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PatchMapping("/{id}")
    public ResponseEntity<String> updatePatient(@PathVariable Long id, @RequestBody PatientDTO patient){
        try {
            patientEventProducer.sendUpdatePatientEvent(id,patient);
            return ResponseEntity.status(HttpStatus.CREATED).body("Updating patient" + id + ".......");

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable Long id){
        try {
            patientEventProducer.sendDeletePatientEvent(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Deleting patient: " + id + ".......");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(" " + id);

        }
    }

}