package com.kth.journalsystem.controller;

import com.kth.journalsystem.domain.Condition;
import com.kth.journalsystem.dto.ConditionDTO;
import com.kth.journalsystem.dto.PatientDTO;
import com.kth.journalsystem.service.producer.ConditionProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/condition")
public class ConditionController {
    @Autowired
    private ConditionProducer conditionProducer;
    @PostMapping("/")
    public ResponseEntity<ConditionDTO> createCondition(@RequestBody ConditionDTO conditionDTO) {
        conditionProducer.sendCreateCondition(conditionDTO);
        ConditionDTO createdConditionDTO = new ConditionDTO(conditionDTO.getId(), conditionDTO.getConditionName(), conditionDTO.getPatient());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConditionDTO);
    }


    @PatchMapping("/{id}")
    public String updateCondition(@PathVariable Long id, @RequestBody ConditionDTO updatedConditionDTO){
        try {
            conditionProducer.sendUpdateConditionTopic(updatedConditionDTO,id);
            return "Updating condition" + id + ".......";

        }catch (Exception e){
            return "updating failed: " + e.toString();
        }
    }
    @GetMapping("/")
    public ResponseEntity<String> getCondition(@RequestParam("id") Long id) { //check here
        try {
            conditionProducer.sendReadConditionEvent(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Retrieving Condition with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCondition(@PathVariable Long id){
        try {
            conditionProducer.sendDeleteConditionEvent(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Deleting condition: " + id + ".......");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(" " + id);

        }
    }


}