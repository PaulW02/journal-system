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
    private KeycloakTokenExchangeService tokenExchangeService;

    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

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

        logger.warn("Token1: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        PatientDTO patientDTO = patientEventConsumer.consumeReadEvent(patientId);
        return ResponseEntity.ok(patientDTO);
    }

    private Map getLimitedScopeToken(String token) throws RestClientException, JsonProcessingException {
        String url = "http://localhost:8181/realms/Journal/protocol/openid-connect/token";
        String clientId = "journal";
        String clientSecret = "LjP8xAwif2mAy7UGw7GjJpc5sdPItayE";
        String scope = "patient"; // Replace with your specific scope

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("subject_token", token);
        body.add("subject_token_type", "urn:ietf:params:oauth:token-type:access_token");
        body.add("scope", scope);
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        return response.getBody();
    }
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<String> getAllPatient() {
        try {
            AccessTokenUser accessTokenUser = AccessTokenUser.convert(SecurityContextHolder.getContext());
            logger.warn("Token: " + accessTokenUser);
            Map response = getLimitedScopeToken(accessTokenUser.getToken());
            accessTokenUser.setScopes(Arrays.stream(response.get("scope").toString().split(" ")).toList());
            accessTokenUser.setToken(response.get("access_token").toString());
            logger.warn("Tokenexchagne : " + accessTokenUser);
            patientEventProducer.sendReadAllPatientsEvent();
            return ResponseEntity.status(HttpStatus.CREATED).body("Retrieving all patients");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @RequestMapping(value = "/retrieve/all", method = RequestMethod.GET)
    @PreAuthorize("hasRole('doctor')")
    public ResponseEntity<List<PatientDTO>> retrieveAllPatients() {
        AccessTokenUser accessTokenUser = AccessTokenUser.convert(SecurityContextHolder.getContext());
        logger.warn("Token2: " + accessTokenUser);
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