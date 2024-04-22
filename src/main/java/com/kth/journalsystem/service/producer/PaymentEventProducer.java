package com.kth.journalsystem.service.producer;

import com.kth.journalsystem.dto.PaymentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    private static final String TOPIC = "payment_events";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentEvent(PaymentDTO paymentDTO) {
        kafkaTemplate.send(TOPIC, paymentDTO);
    }
}