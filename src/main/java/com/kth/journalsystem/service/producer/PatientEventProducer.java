package com.kth.journalsystem.service.producer;

import com.kth.journalsystem.domain.Patient;
import com.kth.journalsystem.dto.OrderDTO;
import com.kth.journalsystem.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PatientEventProducer {

    private static final String TOPIC = "order_events";
    private static final String CREATE_PATIENT_TOPIC = "create_patient_event";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    public void sendOrderEvent(OrderDTO orderEvent) {
        kafkaTemplate.send(TOPIC, orderEvent);
    }

    public void sendCreatePatientEvent(PatientDTO patient) {
        kafkaTemplate.send(CREATE_PATIENT_TOPIC, patient);
    }
}