package com.kth.journalsystem.service.producer;

import com.kth.journalsystem.dto.ObservationDTO;
import com.kth.journalsystem.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ObservationEventProducer
{
    private static final String CREATE_OBSERVATION = "create_observation_event";

    private static final String DELETE_OBSERVATION = "delete_observation_event";
    private static final String UPDATE_OBSERVATION = "update_observation_event";
    private static final String READ_OBSERVATION = "read_observation_event";
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCreateObservationEvent(ObservationDTO observationDTO) {
        kafkaTemplate.send(CREATE_OBSERVATION, observationDTO)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }
    public void sendReadObservationEvent(Long id) {
        kafkaTemplate.send(READ_OBSERVATION, id)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }

    public void sendUpdateObservationEvent(ObservationDTO observationDTO,Long id) {
        kafkaTemplate.send(UPDATE_OBSERVATION,String.valueOf(id), observationDTO)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }
    public void sendDeleteObservation(Long id) {
        kafkaTemplate.send(DELETE_OBSERVATION,id)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }



}
