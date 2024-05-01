package com.kth.journalsystem.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.kth.journalsystem.config.AccessTokenUser;
import com.kth.journalsystem.dto.PatientDTO;
import com.kth.journalsystem.service.KeycloakTokenExchangeService;
import com.kth.journalsystem.service.consumer.PatientEventConsumer;
import com.kth.journalsystem.service.producer.PatientEventProducer;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient")
public class PatientController {
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @Autowired
    private PatientEventProducer patientEventProducer;

    @Autowired
    private PatientEventConsumer patientEventConsumer;

    @Autowired
    private KeycloakTokenExchangeService keycloakTokenExchangeService;
    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_doctor')")
    public ResponseEntity<String> createPatient(@RequestBody PatientDTO patient) {
        try {
            AccessTokenUser accessTokenUser = AccessTokenUser.convert(SecurityContextHolder.getContext());
            patient.setAccessTokenUser(accessTokenUser);
            patientEventProducer.sendCreatePatientEvent(patient);
            return ResponseEntity.status(HttpStatus.CREATED).body("Creating the patient");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_doctor')")
    public ResponseEntity<String> getPatient(@RequestParam("patientId") Long patientId) {
        try {
            patientEventProducer.sendReadPatientEvent(patientId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Retrieving patient with id: " + patientId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/retrieve/{patientId}")
    @PreAuthorize("hasRole('ROLE_doctor')")
    public ResponseEntity<PatientDTO> getPatientDetails(@PathVariable Long patientId) throws TimeoutException {

        logger.warn("Token1: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        PatientDTO patientDTO = patientEventConsumer.consumeReadEvent(patientId);
        return ResponseEntity.ok(patientDTO);
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_doctor')")
    public ResponseEntity<String> getAllPatient() {
        try {
            AccessTokenUser accessTokenUser = AccessTokenUser.convert(SecurityContextHolder.getContext());
            logger.warn("Token: " + accessTokenUser);
            accessTokenUser = keycloakTokenExchangeService.getLimitedScopeToken(accessTokenUser);
            logger.warn("Tokenexchagne : " + accessTokenUser);
            patientEventProducer.sendReadAllPatientsEvent();
            return ResponseEntity.status(HttpStatus.CREATED).body("Retrieving all patients");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/retrieve/all")
    @PreAuthorize("hasRole('ROLE_doctor')")
    public ResponseEntity<List<PatientDTO>> retrieveAllPatients() {
        AccessTokenUser accessTokenUser = AccessTokenUser.convert(SecurityContextHolder.getContext());
        logger.warn("Token2: " + accessTokenUser);
        List<PatientDTO> patientDTOs = patientEventConsumer.consumeReadAllPatientsEvent();
        return ResponseEntity.ok(patientDTOs);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_doctor')")
    public ResponseEntity<String> updatePatient(@PathVariable Long id, @RequestBody PatientDTO patient){
        try {
            patientEventProducer.sendUpdatePatientEvent(id,patient);
            return ResponseEntity.status(HttpStatus.CREATED).body("Updating patient" + id + ".......");

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_doctor')")
    public ResponseEntity<String> deletePatient(@PathVariable Long id){
        try {
            patientEventProducer.sendDeletePatientEvent(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Deleting patient: " + id + ".......");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(" " + id);

        }
    }
}