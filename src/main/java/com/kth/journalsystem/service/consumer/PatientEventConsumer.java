package com.kth.journalsystem.service.consumer;

import com.kth.journalsystem.domain.Condition;
import com.kth.journalsystem.domain.Encounter;
import com.kth.journalsystem.domain.Observation;
import com.kth.journalsystem.domain.Patient;
import com.kth.journalsystem.dto.*;
import com.kth.journalsystem.repository.ConditionRepository;
import com.kth.journalsystem.repository.EncounterRepository;
import com.kth.journalsystem.repository.ObservationRepository;
import com.kth.journalsystem.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PatientEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PatientEventConsumer.class);

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ObservationRepository repository;
    @Autowired

    private EncounterRepository encounterRepository;
    @Autowired

    private ConditionRepository conditionRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @KafkaListener(topics = "read_patient_event", groupId = "patient_group")
    public PatientDetailsDTO consumeReadEvent(Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        List<Encounter> encounters = patient.getEncounters();
        List<Observation> observations = patient.getObservations();
        List<Condition> conditions = patient.getConditions();
        if (patient != null) {
            List<ConditionDTO> conditionDTOS = new ArrayList<>();
            List<ObservationDTO> observationDTOS = new ArrayList<>();
            List<EncounterDTO> encounterDTOS = new ArrayList<>();

            for (Condition condition : conditions) {
                conditionDTOS.add(ConditionEventConsumer.convertConditionToDTO(condition));
            }
            for (Encounter encounter : encounters) {
                encounterDTOS.add(EncounterEventConsumer.convertToDTO(encounter));
            }
            for (Observation observation : observations) {
                observationDTOS.add(ObservationEventConsumer.convertToDTO(observation));
            }

            PatientDetailsDTO patientDTO = new PatientDetailsDTO(patient.getId(), conditionDTOS,observationDTOS,encounterDTOS, patient.getFirstName(), patient.getLastName(), patient.getAge());
            logger.info("Getting patient: " + patientDTO.getFirstName() + patientDTO.getLastName());
            // Send patient data to a response topic
            //kafkaTemplate.send("patient_response_topic", patientDTO);
            return patientDTO;
        } else {
            // Handle not found scenario (log or send error response)
            System.out.println("Patient not found for ID: " + patientId);
        }
        return null;
    }


     static PatientDTO convertToDTO(Patient patient) {
        // Convert Patient entity to DTO
        // Implement this method based on your DTO structure
        return new PatientDTO(patient.getId(), patient.getFirstName(), patient.getLastName(), patient.getAge());
    }

    @KafkaListener(topics = "create_patient_event", groupId = "patient_group")
    public void handleCreatePatientEvent(PatientDTO patient) {
        Patient createdPatient = new Patient(patient.getFirstName(), patient.getLastName(), patient.getAge(), patient.getUserId());
        patientRepository.save(createdPatient);
    }

    @KafkaListener(topics = "delete_patient_event", groupId = "patient_group")
    public void handleDeletePatientEvent(Long id) {
        patientRepository.deleteById(id);
    }


    @KafkaListener(topics = "update_patient_event", groupId = "patient_group")
    public void handleUpdatePatientEvent(ConsumerRecord<String, PatientDTO> record) {
        String idString = record.key(); // Extract the patient ID string from the message key
        Long id = Long.parseLong(idString); // Convert the patient ID string to Long
        PatientDTO patientDTO = record.value(); // Extract the patient DTO from the message value

        Patient p = new Patient(patientDTO.getFirstName(), patientDTO.getLastName(), patientDTO.getAge());
        Optional<Patient> existing = patientRepository.findById(id);
        if (existing.isPresent()) {
            p.setId(id);
            patientRepository.save(p);
        } else {
            throw new RuntimeException("Can't update patient " + id);
        }
    }

    @KafkaListener(topics = "read_all_patients_event", groupId = "patient_group")
    public List<PatientDTO> consumeReadAllPatientsEvent() {
        List<Patient> patients = patientRepository.findAll();
        List<PatientDTO> patientDTOS = new ArrayList<>();
        if (patients != null) {
            for (Patient patient: patients) {
                patientDTOS.add(convertToDTO(patient));
            }
            logger.info("Getting patients: " + patients);
            // Send patient data to a response topic
            //kafkaTemplate.send("patient_response_topic", patientDTO);
            return patientDTOS;
        } else {
            // Handle not found scenario (log or send error response)
            System.out.println("Patients not found");
        }
        return null;
    }
}
