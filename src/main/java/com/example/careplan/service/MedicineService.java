package com.example.careplan.service;

import com.example.careplan.entity.Medicine;
import com.example.careplan.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MedicineService {

    private final MedicineRepository repository;

    public MedicineService(MedicineRepository repository) {
        this.repository = repository;
    }

    public Medicine addMedicine(Medicine medicine) {
        validate(medicine);
        return repository.save(medicine);
    }

    public List<Medicine> getAllMedicines() {
        return repository.findAll();
    }

    public Medicine getMedicineById(Long id) {
        return repository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Medicine not found"));
    }

    public Medicine updateMedicine(
            Long id, Medicine updated) {

        Medicine medicine = getMedicineById(id);
        validate(updated);

        medicine.setMedicineName(updated.getMedicineName());
        medicine.setDosage(updated.getDosage());
        medicine.setScheduleTime(updated.getScheduleTime());
        medicine.setStartDate(updated.getStartDate());
        medicine.setEndDate(updated.getEndDate());
        medicine.setPatientName(updated.getPatientName());
        medicine.setCaregiverName(updated.getCaregiverName());

        return repository.save(medicine);
    }

    public void deleteMedicine(Long id) {
        Medicine medicine = getMedicineById(id);
        repository.delete(medicine);
    }

    private void validate(Medicine medicine) {
        if (medicine.getMedicineName() == null ||
                medicine.getMedicineName().isBlank()) {
            throw new IllegalArgumentException(
                "Medicine name is required");
        }

        if (medicine.getDosage() == null ||
                medicine.getDosage().isBlank()) {
            throw new IllegalArgumentException(
                "Dosage is required");
        }

        if (medicine.getScheduleTime() == null) {
            throw new IllegalArgumentException(
                "Schedule time is required");
        }

        if (medicine.getStartDate() == null ||
                medicine.getEndDate() == null ||
                medicine.getEndDate().isBefore(
                    medicine.getStartDate())) {
            throw new IllegalArgumentException(
                "Invalid medicine dates");
        }
    }
}