package com.kth.journalsystem.service.producer;


import com.kth.journalsystem.dto.ConditionDTO;
import com.kth.journalsystem.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConditionProducer
{
    private static final String CREATE_CONDITION_TOPIC = "create_condition";
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    public void sendCreateCondition(ConditionDTO conditionDTO) {
        kafkaTemplate.send(CREATE_CONDITION_TOPIC, conditionDTO);
    }


}
