package com.kth.journalsystem.controller;


import com.kth.journalsystem.dto.EncounterDTO;
import com.kth.journalsystem.service.producer.EncounterEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/condition")
public class EncounterController
{
    @Autowired
    private EncounterEventProducer encounterEventProducer;
    @PostMapping("/")
    public ResponseEntity<EncounterDTO> createEncounter(@RequestBody EncounterDTO encounterDTO) {
        encounterEventProducer.sendCreateEncounterEvent(encounterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(encounterDTO);
    }



    @PatchMapping("/{id}")
    public ResponseEntity<String>  updateEncounter(@PathVariable Long id, @RequestBody EncounterDTO encounterDTO){
        try {
            encounterEventProducer.sendUpdateEncounterEvent(encounterDTO,id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Updating Encounter with id: " + id);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/")
    public ResponseEntity<String> getEncounter(@RequestParam("id") Long id) {
        try {
            encounterEventProducer.sendReadEncounterEvent(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Updating Encounter with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteObservation(@PathVariable Long id){
        try {
            encounterEventProducer.sendDeleteEncounterEvent(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Deleting encounter: " + id + ".......");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(" " + id);

        }
    }
}
