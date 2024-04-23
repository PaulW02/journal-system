package com.kth.journalsystem.repository;

import com.kth.journalsystem.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long>
{


     @Query("SELECT e FROM Patient e WHERE e.firstName = :firstName AND e.lastName = :lastName")
     List<Patient> findByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);


     @Modifying
     @Query(value = "INSERT INTO patients (first_name, last_name,age) VALUES (?1, ?2, ?3)", nativeQuery = true)
     void insertPatient(String firstName, String lastName,String age);

     @Query("SELECT e FROM Patient e WHERE e.userId = ?1")
     Patient getPatientByUserId(String id);

}
