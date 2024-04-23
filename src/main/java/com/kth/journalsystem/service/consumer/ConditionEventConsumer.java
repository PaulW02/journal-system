package com.kth.journalsystem.service.consumer;

import com.kth.journalsystem.domain.Condition;
import com.kth.journalsystem.domain.Patient;
import com.kth.journalsystem.dto.ConditionDTO;
import com.kth.journalsystem.dto.PatientDTO;
import com.kth.journalsystem.repository.ConditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConditionEventConsumer
{
    @Autowired
    private ConditionRepository repository;
    @KafkaListener(topics = "create_condition_event", groupId = "patient_group")
    public void handleCreatePatientEvent(ConditionDTO conditionDTO) {
        Condition condition = new Condition(conditionDTO.getConditionName(), new Patient(conditionDTO.getPatient().getFirstName(),conditionDTO.getPatient().getLastName(),conditionDTO.getPatient().getAge()));
        repository.save(condition);
    }

}
