package com.kth.journalsystem.service.consumer;

import com.kth.journalsystem.dto.PaymentDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {

    @KafkaListener(topics = "payment_events", groupId = "payment_group")
    public void handlePaymentEvent(PaymentDTO paymentDTO) {
        // Process the payment event, e.g., update payment status
        System.out.println("Received Payment Event: " + paymentDTO);
        // Implement the logic for payment processing and order status update
    }
}