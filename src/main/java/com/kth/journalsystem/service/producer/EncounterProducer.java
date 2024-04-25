package com.kth.journalsystem.service.producer;

import com.kth.journalsystem.dto.EncounterDTO;
import com.kth.journalsystem.dto.ObservationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EncounterProducer
{
    private static final String CREATE = "create_encounter_event";

    private static final String DELTE = "delete_encounter_event";
    private static final String UPDATE = "update_encounter_event";
    private static final String READ= "read_encounter_event";
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCreateEncounterEvent(EncounterDTO encounterDTO) {
        kafkaTemplate.send(CREATE, encounterDTO)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }
    public void sendReadEncounterEvent(Long id) {
        kafkaTemplate.send(READ, id)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }

    public void sendUpdateEncounterEvent(EncounterDTO encounterDTO,Long id) {
        kafkaTemplate.send(UPDATE,String.valueOf(id), encounterDTO)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }
    public void sendDeleteEncounterEvent(Long id) {
        kafkaTemplate.send(UPDATE,id)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }

}
