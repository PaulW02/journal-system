package com.kth.journalsystem.service.producer;


import com.kth.journalsystem.dto.ConditionDTO;
import com.kth.journalsystem.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConditionProducer
{
    private static final String CREATE_CONDITION_TOPIC = "create_condition_event";
    private static final String UPDATE_CONDITION_TOPIC = "update_condition_event";
    private static final String READ_CONDITION_TOPIC = "read_condition_event";
    private static final String DELETE_CONDITION_TOPIC = "delete_condition_event";
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    public void sendCreateCondition(ConditionDTO conditionDTO) {
        kafkaTemplate.send(CREATE_CONDITION_TOPIC, conditionDTO);
    }

    public void sendUpdateConditionTopic(ConditionDTO conditionDTO, Long id) {
        kafkaTemplate.send(UPDATE_CONDITION_TOPIC, String.valueOf(id), conditionDTO)
                .whenComplete((sendResult, throwable) -> {
                    if (throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }

    public void sendReadConditionEvent(Long id) {
        kafkaTemplate.send(READ_CONDITION_TOPIC, id)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }

    public void sendDeleteConditionEvent(Long id){
        kafkaTemplate.send(DELETE_CONDITION_TOPIC, id)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }


}
