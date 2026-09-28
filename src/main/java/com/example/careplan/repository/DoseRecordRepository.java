package com.example.careplan.repository;

import com.example.careplan.entity.DoseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DoseRecordRepository
        extends JpaRepository<DoseRecord, Long> {

    List<DoseRecord> findByMedicineId(Long medicineId);
}