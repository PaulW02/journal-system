package com.kth.journalsystem.controller;

import com.kth.journalsystem.domain.Condition;
import com.kth.journalsystem.dto.ConditionDTO;
import com.kth.journalsystem.dto.PatientDTO;
import com.kth.journalsystem.service.producer.ConditionProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class ConditionController {
    private ConditionProducer conditionProducer;
    @PostMapping("/patient/condition")
    public ResponseEntity<ConditionDTO> Condition(@RequestBody ConditionDTO conditionDTO) {
        conditionProducer.sendCreateCondition(conditionDTO);
        ConditionDTO createdConditionDTO = new ConditionDTO(conditionDTO.getId(), conditionDTO.getConditionName(), conditionDTO.getPatient());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConditionDTO);
    }
}