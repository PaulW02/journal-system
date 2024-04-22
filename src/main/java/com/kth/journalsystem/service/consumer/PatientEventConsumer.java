package com.kth.journalsystem.service.consumer;

import com.kth.journalsystem.dto.OrderDTO;
import com.kth.journalsystem.dto.PatientDTO;
import com.kth.journalsystem.dto.PaymentDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PatientEventConsumer {

    @KafkaListener(topics = "order_events", groupId = "order_group")
    public void handleOrderEvent(OrderDTO orderEvent) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Order Event: " + orderEvent);
        // Implement the logic for order processing and updating inventory
    }

    @KafkaListener(topics = "payment_events", groupId = "payment_group")
    public void handlePaymentEvent(PaymentDTO paymentDTO) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Payment Event ORDDERR: " + paymentDTO);
        // Implement the logic for order processing and updating inventory
    }

    @KafkaListener(topics = "create_patient_event", groupId = "patient_group")
    public void handleCreatePatientEvent(PatientDTO patient) {
        // Process the order event, e.g., store it in the database
        System.out.println("Received Create Patient Event: " + patient);
        // Implement the logic for order processing and updating inventory
    }
}
