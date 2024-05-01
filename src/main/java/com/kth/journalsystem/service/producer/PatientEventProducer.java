package com.kth.journalsystem.service.producer;

import com.kth.journalsystem.dto.OrderDTO;
import com.kth.journalsystem.dto.PatientDTO;
import com.kth.journalsystem.dto.PatientRequestDTO;
import com.kth.journalsystem.service.KeycloakTokenExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class PatientEventProducer {

    private static final String TOPIC = "order_events";
    private static final String CREATE_PATIENT_TOPIC = "create_patient_event";
    private static final String UPDATE_PATIENT_TOPIC = "update_patient_event";
    private static final String READ_PATIENT_TOPIC = "read_patient_event";
    private static final String READ_ALL_PATIENTS_TOPIC = "read_all_patients_event";
    private static final String DELETE_PATIENT_TOPIC = "delete_patient_event";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private KeycloakTokenExchangeService keycloakTokenExchangeService;

    public void sendCreatePatientEvent(PatientDTO patient) {
        patient.setAccessTokenUser(keycloakTokenExchangeService.getLimitedScopeToken(patient.getAccessTokenUser()));
        List<String> scopes = patient.getAccessTokenUser().getScopes();
        if(scopes.size() == 1 && scopes.get(0).equals("patient")){
            kafkaTemplate.send(CREATE_PATIENT_TOPIC, patient);

        }
    }

    public void sendReadPatientEvent(Long patientId) {
        kafkaTemplate.send(READ_PATIENT_TOPIC, patientId)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }


    public void sendReadAllPatientsEvent() {
        kafkaTemplate.send(READ_ALL_PATIENTS_TOPIC, "Retrieve all patients")
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }


    public void sendUpdatePatientEvent(Long patientId, PatientDTO updatedPatient) {
        kafkaTemplate.send(UPDATE_PATIENT_TOPIC, String.valueOf(patientId), updatedPatient)
                .whenComplete((sendResult, throwable) -> {
                    if (throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }

    public void sendDeletePatientEvent(Long patientId){
        kafkaTemplate.send(DELETE_PATIENT_TOPIC, patientId)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }


}