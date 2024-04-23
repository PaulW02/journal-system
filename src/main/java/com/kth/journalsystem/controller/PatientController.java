package com.kth.journalsystem.controller;

import com.kth.journalsystem.dto.PatientDTO;
import com.kth.journalsystem.service.producer.PatientEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {

    @Autowired
    private PatientEventProducer patientEventProducer;

    @PostMapping("/")
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patient) {
        patientEventProducer.sendCreatePatientEvent(patient);
        PatientDTO createdPatientDTO = new PatientDTO(patient.getId(), patient.getFirstName(), patient.getLastName(), patient.getAge());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatientDTO);
    }
}