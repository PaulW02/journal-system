package com.kth.journalsystem.controller;


import com.kth.journalsystem.dto.PatientDTO;
import com.kth.journalsystem.service.KeycloakTokenExchangeService;
import com.kth.journalsystem.service.consumer.PatientEventConsumer;
import com.kth.journalsystem.service.producer.PatientEventProducer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    private PatientEventProducer patientEventProducer;

    @Autowired
    private PatientEventConsumer patientEventConsumer;

    @Autowired
    private KeycloakTokenExchangeService tokenExchangeService;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
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

    @GetMapping("/retrieve/{patientId}")
    public ResponseEntity<PatientDTO> getPatientDetails(@PathVariable Long patientId) throws TimeoutException {
        PatientDTO patientDTO = patientEventConsumer.consumeReadEvent(patientId);
        return ResponseEntity.ok(patientDTO);
    }


    @GetMapping("/all")
    public ResponseEntity<String> getAllPatient() {
        try {
            patientEventProducer.sendReadAllPatientsEvent();
            return ResponseEntity.status(HttpStatus.CREATED).body("Retrieving all patients");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/retrieve/all")
    public ResponseEntity<List<PatientDTO>> retrieveAllPatients() throws TimeoutException {
        List<PatientDTO> patientDTOs = patientEventConsumer.consumeReadAllPatientsEvent();
        return ResponseEntity.ok(patientDTOs);
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

    public String exchangeToken(String currentToken, String targetAudience) {
        return tokenExchangeService.exchangeToken(currentToken, targetAudience);
    }

    private String extractTokenFromAuthorizationHeader(String authorizationHeader) {
        // Assuming the Authorization header follows the format "Bearer <token>"
        String[] parts = authorizationHeader.split(" ");
        if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
            return parts[1];
        } else {
            // Invalid or missing Authorization header
            throw new IllegalArgumentException("Invalid Authorization header");
        }
    }
}