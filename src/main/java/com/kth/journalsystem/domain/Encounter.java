package com.kth.journalsystem.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "encounters")
public class Encounter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate visitDate;
    @OneToMany(mappedBy = "encounter")
    private List<Observation> observations = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient; // Relation till Patient-entiteten
    // New field for file handling
    @Lob
    @Column(name = "image_data")
    private byte[] imageData;



    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public Encounter(LocalDate visitDate, Patient patient) {
        this.visitDate = visitDate;
        this.patient = patient;
    }

    public Encounter(Long id, LocalDate visitDate, Patient patient) {
        this.id = id;
        this.visitDate = visitDate;
        this.patient = patient;
    }

    public Encounter() {
    }

    public List<Observation> getObservations() {
        return observations;
    }

    public void setObservations(ArrayList<Observation> observations) {
        this.observations = observations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }
}

