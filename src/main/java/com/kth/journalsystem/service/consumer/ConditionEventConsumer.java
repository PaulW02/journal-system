package com.kth.journalsystem.service.consumer;

import com.kth.journalsystem.domain.Condition;
import com.kth.journalsystem.domain.Patient;
import com.kth.journalsystem.dto.ConditionDTO;
import com.kth.journalsystem.repository.ConditionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConditionEventConsumer
{

    private static final Logger logger = LoggerFactory.getLogger(ConditionEventConsumer.class);
    @Autowired
    private ConditionRepository repository;


    @KafkaListener(topics = "create_condition_event", groupId = "condition_group")
    public void handleCreateConditionEvent(ConditionDTO conditionDTO) {
        Condition condition = new Condition(conditionDTO.getConditionName(), new Patient(conditionDTO.getPatient().getFirstName(),conditionDTO.getPatient().getLastName(),conditionDTO.getPatient().getAge()));
        repository.save(condition);
    }
    @KafkaListener(topics = "update_condition_event", groupId = "condition_group")
    public void handleUpdateConditionEvent(Long id,ConditionDTO conditionDTO)
    {
        Patient p = new Patient(conditionDTO.getPatient().getId(),conditionDTO.getPatient().getFirstName(),conditionDTO.getPatient().getLastName(),conditionDTO.getPatient().getAge());
        Condition condition = new Condition(conditionDTO.getConditionName(), p);
        Optional<Condition> existingCondition = repository.findById(id);
        if (existingCondition.isPresent()) {
            condition.setId(id);
            repository.save(condition);
        } else {
            throw new RuntimeException("Can't update condition " + id);
        }
    }
    private ConditionDTO convertToDTO(Condition condition) {
        // Convert Patient entity to DTO
        // Implement this method based on your DTO structure
        return new ConditionDTO(condition.getId(), condition.getConditionName(), PatientEventConsumer.convertToDTO(condition.getPatient()));
    }
    @KafkaListener(topics = "read_condition_event", groupId = "condition_group")
    public void consumeReadEvent(Long id) {

        Condition condition = repository.findById(id).orElse(null);

        if (condition != null) {
            // Process the retrieved patient data
            ConditionDTO conditionDTO = this.convertToDTO(condition);
            logger.info("Getting condition: "+ condition.getId() +condition.getConditionName());
            // Send patient data to the frontend using WebSocket
//            messagingTemplate.convertAndSend("/topic/condition-data", conditionDTO);
        } else {
            // Handle the case where the patient data could not be found
            // For example, log an error message or send an error response
            System.out.println("Condition not found for ID: " + id);
        }
    }
    @KafkaListener(topics = "delete_condition_event", groupId = "condition_group")
    public void handleDeleteConditionEvent(Long id) {
        repository.deleteById(id);
    }



}
