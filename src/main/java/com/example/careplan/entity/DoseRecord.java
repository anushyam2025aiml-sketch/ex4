package com.example.careplan.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dose_records")
public class DoseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long medicineId;
    private LocalDate doseDate;
    private String status;
    private LocalDateTime takenAt;

    public DoseRecord() {
    }

    public Long getId() {
        return id;
    }

    public Long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Long medicineId) {
        this.medicineId = medicineId;
    }

    public LocalDate getDoseDate() {
        return doseDate;
    }

    public void setDoseDate(LocalDate doseDate) {
        this.doseDate = doseDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(LocalDateTime takenAt) {
        this.takenAt = takenAt;
    }
}