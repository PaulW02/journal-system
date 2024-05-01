package com.kth.journalsystem.service.consumer;

import com.kth.journalsystem.domain.Condition;
import com.kth.journalsystem.domain.Observation;
import com.kth.journalsystem.domain.Patient;
import com.kth.journalsystem.dto.ConditionDTO;
import com.kth.journalsystem.dto.ObservationDTO;
import com.kth.journalsystem.dto.PatientDTO;
import com.kth.journalsystem.repository.ConditionRepository;
import com.kth.journalsystem.repository.ObservationRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ObservationEventConsumer
{
    private static final Logger logger = LoggerFactory.getLogger(ObservationEventConsumer.class);
    @Autowired
    private ObservationRepository repository;


    @KafkaListener(topics = "create_observation_event", groupId = "observation_group")
    public void handleCreateObservationEvent(ObservationDTO observationDTO) {
        Patient p = new Patient(observationDTO.getPatientDTO().getId(),observationDTO.getPatientDTO().getFirstName(),observationDTO.getPatientDTO().getLastName(),observationDTO.getPatientDTO().getAge());
        Observation observation = new Observation(observationDTO.getType(),observationDTO.getValue(),p);
        repository.save(observation);
    }
    @KafkaListener(topics = "update_observation_event", groupId = "observation_group")
    public void handleUpdateObservationEvent(ConsumerRecord<String, ObservationDTO> record)
    {
        String idString = record.key(); // Extract the patient ID string from the message key
        Long id = Long.parseLong(idString); // Convert the patient ID string to Long
        ObservationDTO observationDTO = record.value(); // Extract the patient DTO from the message value

        Patient p = new Patient(observationDTO.getPatientDTO().getId(),observationDTO.getPatientDTO().getFirstName(),observationDTO.getPatientDTO().getLastName(),observationDTO.getPatientDTO().getAge());
        Observation observation = new Observation(observationDTO.getType(), observationDTO.getValue(),p);
        Optional<Observation> existingCondition = repository.findById(id);
        if (existingCondition.isPresent()) {
            observation.setId(id);
            repository.save(observation);
        } else {
            throw new RuntimeException("Can't update observation " + id);
        }
    }
    static ObservationDTO convertToDTO(Observation observation) {
        // Convert Patient entity to DTO
        // Implement this method based on your DTO structure
        return new ObservationDTO(observation.getId().toString(), observation.getValue(), PatientEventConsumer.convertToDTO(observation.getPatient()));
    }


    @KafkaListener(topics = "read_observation_event", groupId = "observation_group")
    public void consumeReadEvent(Long id) {

        Observation observation = repository.findById(id).orElse(null);

        if (observation != null) {
            // Process the retrieved patient data
            ObservationDTO observationDTO = convertToDTO(observation);
            logger.info("Getting observation: "+ observation.getId() );
            // Send patient data to the frontend using WebSocket
            //messagingTemplate.convertAndSend("/topic/condition-data", observationDTO);
        } else {
            // Handle the case where the patient data could not be found
            // For example, log an error message or send an error response
            System.out.println("Condition not found for ID: " + id);
        }
    }

    @KafkaListener(topics = "delete_observation_event", groupId = "observation_group")
    public void handleDeleteObservationEvent(Long id) {
        repository.deleteById(id);
    }
}
