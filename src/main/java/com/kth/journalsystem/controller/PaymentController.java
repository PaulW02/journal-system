package com.kth.journalsystem.controller;

import com.kth.journalsystem.dto.PaymentDTO;
import com.kth.journalsystem.service.producer.PaymentEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @Autowired
    private PaymentEventProducer paymentEventProducer;

    @PostMapping("/payments")
    public ResponseEntity<String> processPayment(@RequestBody PaymentDTO paymentDTO) {
        // Process the received payment event and send it to Kafka
        paymentEventProducer.sendPaymentEvent(paymentDTO);
        return ResponseEntity.ok("Payment processed successfully");
    }
}