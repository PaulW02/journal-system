package com.kth.journalsystem.controller;

import com.kth.journalsystem.dto.ObservationDTO;
import com.kth.journalsystem.service.producer.ObservationEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/observation")

public class ObservationController
{
    @Autowired
    private ObservationEventProducer observationEventProducer;
    @PostMapping("/")
    public ResponseEntity<ObservationDTO> createObservation(@RequestBody ObservationDTO observationDTO) {
        observationEventProducer.sendCreateObservationEvent(observationDTO);
       // ConditionDTO createdConditionDTO = new ConditionDTO(conditionDTO.getId(), conditionDTO.getConditionName(), conditionDTO.getPatient());
        return ResponseEntity.status(HttpStatus.CREATED).body(observationDTO);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<String>  updateObservation(@PathVariable Long id, @RequestBody ObservationDTO observationDTO){
        try {
            observationEventProducer.sendUpdateObservationEvent(observationDTO,id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Retrieving observation with id: " + id);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/")
    public ResponseEntity<String> getObservation(@RequestParam("id") Long id) {
        try {
            observationEventProducer.sendReadObservationEvent(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Retrieving observation with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteObservation(@PathVariable Long id){
        try {
            observationEventProducer.sendDeleteObservation(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Deleting observation: " + id + ".......");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(" " + id);

        }
    }
}
