package com.kth.journalsystem.controller;

import com.kth.journalsystem.dto.ConditionDTO;
import com.kth.journalsystem.service.producer.ConditionEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/condition")
public class ConditionController {
    @Autowired
    private ConditionEventProducer conditionEventProducer;
    @PostMapping("/")
    public ResponseEntity<ConditionDTO> createCondition(@RequestBody ConditionDTO conditionDTO) {
        conditionEventProducer.sendCreateCondition(conditionDTO);
        ConditionDTO createdConditionDTO = new ConditionDTO(conditionDTO.getId(), conditionDTO.getConditionName(), conditionDTO.getPatient());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConditionDTO);
    }


    @PatchMapping("/{id}")
    public String updateCondition(@PathVariable Long id, @RequestBody ConditionDTO updatedConditionDTO){
        try {
            conditionEventProducer.sendUpdateConditionTopic(updatedConditionDTO,id);
            return "Updating condition" + id + ".......";

        }catch (Exception e){
            return "updating failed: " + e.toString();
        }
    }
    @GetMapping("/")
    public ResponseEntity<String> getCondition(@RequestParam("id") Long id) { //check here
        try {
            conditionEventProducer.sendReadConditionEvent(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Retrieving Condition with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCondition(@PathVariable Long id){
        try {
            conditionEventProducer.sendDeleteConditionEvent(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Deleting condition: " + id + ".......");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(" " + id);

        }
    }


}