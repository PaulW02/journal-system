package com.kth.journalsystem.service.consumer;

import com.kth.journalsystem.domain.Patient;
import com.kth.journalsystem.dto.OrderDTO;
import com.kth.journalsystem.dto.PatientDTO;
import com.kth.journalsystem.dto.PatientRequestDTO;
import com.kth.journalsystem.dto.PaymentDTO;
import com.kth.journalsystem.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PatientEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PatientEventConsumer.class);

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "read_patient_event", groupId = "patient_group")
    public void consumeReadEvent(Long patientId) {

        Patient patient = patientRepository.findById(patientId).orElse(null);

        if (patient != null) {
            // Process the retrieved patient data
            PatientDTO patientDTO = convertToDTO(patient);
            logger.info("Getting patient: "+ patientDTO.getFirstName() + patientDTO.getLastName());
            // Send patient data to the frontend using WebSocket
            messagingTemplate.convertAndSend("/topic/patient-data", patientDTO);
        } else {
            // Handle the case where the patient data could not be found
            // For example, log an error message or send an error response
            System.out.println("Patient not found for ID: " + patientId);
        }
    }

    private PatientDTO convertToDTO(Patient patient) {
        // Convert Patient entity to DTO
        // Implement this method based on your DTO structure
        return new PatientDTO(patient.getId(), patient.getFirstName(), patient.getLastName(), patient.getAge());
    }
    @KafkaListener(topics = "create_patient_event", groupId = "patient_group")
    public void handleCreatePatientEvent(PatientDTO patient) {
        Patient createdPatient = new Patient(patient.getFirstName(), patient.getLastName(), patient.getAge(), patient.getUserId());
        System.out.println(patient + " dwadaddad");
        patientRepository.save(createdPatient);
    }


}
