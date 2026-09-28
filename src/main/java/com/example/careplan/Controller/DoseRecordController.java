package com.example.careplan.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.careplan.entity.DoseRecord;
import com.example.careplan.repository.DoseRecordRepository;
import com.example.careplan.repository.MedicineRepository;

@RestController
@RequestMapping("/api/doses")
@CrossOrigin(origins = "*")
public class DoseRecordController {

    private final DoseRecordRepository doseRepository;
    private final MedicineRepository medicineRepository;

    public DoseRecordController(
            DoseRecordRepository doseRepository,
            MedicineRepository medicineRepository) {
        this.doseRepository = doseRepository;
        this.medicineRepository = medicineRepository;
    }

    @PostMapping
    public DoseRecord addDose(@RequestBody DoseRecord dose) {
        if (dose.getMedicineId() == null ||
                !medicineRepository.existsById(
                    dose.getMedicineId())) {
            throw new IllegalArgumentException(
                "Medicine not found");
        }

        if (dose.getDoseDate() == null) {
            throw new IllegalArgumentException(
                "Dose date is required");
        }

        if (dose.getStatus() == null ||
                !(dose.getStatus().equals("TAKEN") ||
                  dose.getStatus().equals("MISSED"))) {
            throw new IllegalArgumentException(
                "Status must be TAKEN or MISSED");
        }

        if (dose.getStatus().equals("TAKEN")) {
            dose.setTakenAt(LocalDateTime.now());
        }

        return doseRepository.save(dose);
    }

    @GetMapping
    public List<DoseRecord> getAllDoses() {
        return doseRepository.findAll();
    }

    @GetMapping("/{id}")
    public DoseRecord getDose(@PathVariable Long id) {
        return doseRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Dose record not found"));
    }

    @GetMapping("/medicine/{medicineId}")
    public List<DoseRecord> getDosesByMedicine(
            @PathVariable Long medicineId) {
        return doseRepository.findByMedicineId(medicineId);
    }

    @PutMapping("/{id}")
    public DoseRecord updateDose(
            @PathVariable Long id,
            @RequestBody DoseRecord updated) {

        DoseRecord dose = getDose(id);

        if (updated.getStatus() != null) {
            if (!(updated.getStatus().equals("TAKEN") ||
                  updated.getStatus().equals("MISSED"))) {
                throw new IllegalArgumentException(
                    "Invalid dose status");
            }

            dose.setStatus(updated.getStatus());
            dose.setTakenAt(
                updated.getStatus().equals("TAKEN")
                    ? LocalDateTime.now() : null);
        }

        if (updated.getDoseDate() != null) {
            dose.setDoseDate(updated.getDoseDate());
        }

        return doseRepository.save(dose);
    }

    @DeleteMapping("/{id}")
    public String deleteDose(@PathVariable Long id) {
        DoseRecord dose = getDose(id);
        doseRepository.delete(dose);
        return "Dose record deleted successfully";
    }

    @PutMapping("/{id}/taken")
    public DoseRecord markAsTaken(@PathVariable Long id) {
        DoseRecord dose = getDose(id);
        dose.setStatus("TAKEN");
        dose.setTakenAt(LocalDateTime.now());
        return doseRepository.save(dose);
    }
}