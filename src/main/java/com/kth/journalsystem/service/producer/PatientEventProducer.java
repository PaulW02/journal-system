package com.kth.journalsystem.service.producer;

import com.kth.journalsystem.dto.OrderDTO;
import com.kth.journalsystem.dto.PatientDTO;
import com.kth.journalsystem.dto.PatientRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PatientEventProducer {

    private static final String TOPIC = "order_events";
    private static final String CREATE_PATIENT_TOPIC = "create_patient_event";
    private static final String READ_PATIENT_TOPIC = "read_patient_event";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    public void sendOrderEvent(OrderDTO orderEvent) {
        kafkaTemplate.send(TOPIC, orderEvent);
    }

    public void sendCreatePatientEvent(PatientDTO patient) {
         kafkaTemplate.send(CREATE_PATIENT_TOPIC, patient)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }

    public void sendReadPatientEvent(Long patientId) {
        kafkaTemplate.send(READ_PATIENT_TOPIC, patientId)
                .whenComplete((sendResult, throwable) -> {
                    if(throwable == null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                });
    }
}