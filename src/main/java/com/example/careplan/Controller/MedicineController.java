package com.example.careplan.Controller;

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

import com.example.careplan.entity.Medicine;
import com.example.careplan.service.MedicineService;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "*")
public class MedicineController {

    private final MedicineService service;

    public MedicineController(MedicineService service) {
        this.service = service;
    }

    @PostMapping
    public Medicine addMedicine(
            @RequestBody Medicine medicine) {
        return service.addMedicine(medicine);
    }

    @GetMapping
    public List<Medicine> getAllMedicines() {
        return service.getAllMedicines();
    }

    @GetMapping("/{id}")
    public Medicine getMedicine(@PathVariable Long id) {
        return service.getMedicineById(id);
    }

    @PutMapping("/{id}")
    public Medicine updateMedicine(
            @PathVariable Long id,
            @RequestBody Medicine medicine) {
        return service.updateMedicine(id, medicine);
    }

    @DeleteMapping("/{id}")
    public String deleteMedicine(@PathVariable Long id) {
        service.deleteMedicine(id);
        return "Medicine deleted successfully";
    }
}