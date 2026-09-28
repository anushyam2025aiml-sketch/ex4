package com.example.careplan.repository;

import com.example.careplan.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository
        extends JpaRepository<Medicine, Long> {
}