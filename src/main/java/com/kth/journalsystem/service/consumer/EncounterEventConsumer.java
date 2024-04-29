package com.kth.journalsystem.service.consumer;

import com.kth.journalsystem.domain.Encounter;
import com.kth.journalsystem.domain.Observation;
import com.kth.journalsystem.domain.Patient;
import com.kth.journalsystem.dto.EncounterDTO;
import com.kth.journalsystem.dto.ObservationDTO;
import com.kth.journalsystem.repository.EncounterRepository;
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
public class EncounterEventConsumer
{
    private static final Logger logger = LoggerFactory.getLogger(EncounterEventConsumer.class);
    @Autowired
    private EncounterRepository repository;

    @KafkaListener(topics = "create_encounter_event", groupId = "encounter_group")
    public void handleCreateEncounterEvent(EncounterDTO encounterDTO) {
        Patient p = new Patient(encounterDTO.getPatientDTO().getId(),encounterDTO.getPatientDTO().getFirstName(),encounterDTO.getPatientDTO().getLastName(),encounterDTO.getPatientDTO().getAge());
        Encounter encounter = new Encounter(encounterDTO.getVisitDate(),p);
        repository.save(encounter);
    }
    @KafkaListener(topics = "update_encounter_event", groupId = "encounter_group")
    public void handleUpdateEncounterEvent(ConsumerRecord<String, EncounterDTO> record)
    {
        String idString = record.key(); // Extract the patient ID string from the message key
        Long id = Long.parseLong(idString); // Convert the patient ID string to Long
        EncounterDTO encounterDTO = record.value(); // Extract the patient DTO from the message value

        Patient p = new Patient(encounterDTO.getPatientDTO().getId(),encounterDTO.getPatientDTO().getFirstName(),encounterDTO.getPatientDTO().getLastName(),encounterDTO.getPatientDTO().getAge());
        Encounter encounter = new Encounter(encounterDTO.getVisitDate(),p);
        Optional<Encounter> exisitionEncounter = repository.findById(id);
        if (exisitionEncounter.isPresent()) {
            encounter.setId(id);
            repository.save(encounter);
        } else {
            throw new RuntimeException("Can't update observation " + id);
        }
    }
    private EncounterDTO convertToDTO(Encounter encounter) {
        // Convert Patient entity to DTO
        // Implement this method based on your DTO structure
        return new EncounterDTO(encounter.getId(),encounter.getVisitDate(), PatientEventConsumer.convertToDTO(encounter.getPatient()));
    }


    @KafkaListener(topics = "read_encounter_event", groupId = "encounter_group")
    public void consumeReadEvent(Long id) {

        Encounter encounter = repository.findById(id).orElse(null);

        if (encounter != null) {
            // Process the retrieved patient data
            EncounterDTO encounterDTO = this.convertToDTO(encounter);
            logger.info("Getting encounter: "+ encounterDTO.getId() );
            // Send patient data to the frontend using WebSocket
            //messagingTemplate.convertAndSend("/topic/condition-data", encounterDTO);
        } else {
            // Handle the case where the patient data could not be found
            // For example, log an error message or send an error response
            System.out.println("Condition not found for ID: " + id);
        }
    }

    @KafkaListener(topics = "delete_observation_event", groupId = "observation_group")
    public void handleDeleteEncounterEvent(Long id) {
        repository.deleteById(id);
    }
}
