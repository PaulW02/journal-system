package com.kth.journalsystem.controller;

import com.kth.journalsystem.domain.Patient;
import com.kth.journalsystem.dto.OrderDTO;
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

    @PostMapping("/orders")
    public ResponseEntity<String> createOrder(@RequestBody OrderDTO orderEvent) {
        // Process the received order event and send it to Kafka
        patientEventProducer.sendOrderEvent(orderEvent);
        return ResponseEntity.ok("Order created successfully");
    }

    @PostMapping("/patient")
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patient) {
        patientEventProducer.sendCreatePatientEvent(patient);
        PatientDTO createdPatientDTO = new PatientDTO(patient.getId(), patient.getFirstName(), patient.getLastName(), patient.getAge());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatientDTO);
    }

    @PostMapping("/order")
    public ResponseEntity<String> createOrderFixed() {
        // Process the received order event and send it to Kafka
        patientEventProducer.sendOrderEvent(new OrderDTO());
        return ResponseEntity.ok("Order created successfully");
    }
}