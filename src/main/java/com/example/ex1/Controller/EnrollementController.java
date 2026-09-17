package com.example.ex1.Controller;

import com.example.ex1.entity.Enrollement;
import com.example.ex1.repository.EnrollementRepository;
import com.example.ex1.repository.StudentRepository;
import com.example.ex1.repository.CourseRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/enrollments")
public class EnrollementController {

    private final EnrollementRepository enrollementRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollementController(
            EnrollementRepository enrollementRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository) {

        this.enrollementRepository = enrollementRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<?> createEnrollement(
            @RequestBody Enrollement enrollement) {

        if (!studentRepository.existsById(
                enrollement.getStudentId())) {

            return ResponseEntity.badRequest()
                    .body("Student not found");
        }

        if (!courseRepository.existsById(
                enrollement.getCourseId())) {

            return ResponseEntity.badRequest()
                    .body("Course not found");
        }

        if (enrollementRepository.existsByStudentIdAndCourseId(
                enrollement.getStudentId(),
                enrollement.getCourseId())) {

            return ResponseEntity.badRequest()
                    .body("Student already enrolled");
        }

        Enrollement saved =
                enrollementRepository.save(enrollement);

        return ResponseEntity.ok(saved);
    }

    // GET ALL
    @GetMapping
    public List<Enrollement> getAllEnrollements() {
        return enrollementRepository.findAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEnrollementById(
            @PathVariable Long id) {

        Optional<Enrollement> enrollement =
                enrollementRepository.findById(id);

        if (enrollement.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(enrollement.get());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEnrollement(
            @PathVariable Long id,
            @RequestBody Enrollement enrollement) {

        Optional<Enrollement> existing =
                enrollementRepository.findById(id);

        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!studentRepository.existsById(
                enrollement.getStudentId())) {

            return ResponseEntity.badRequest()
                    .body("Student not found");
        }

        if (!courseRepository.existsById(
                enrollement.getCourseId())) {

            return ResponseEntity.badRequest()
                    .body("Course not found");
        }

        Enrollement old = existing.get();

        old.setStudentId(enrollement.getStudentId());
        old.setCourseId(enrollement.getCourseId());
        old.setEnrollmentDate(enrollement.getEnrollmentDate());
        old.setStatus(enrollement.getStatus());

        return ResponseEntity.ok(
                enrollementRepository.save(old));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEnrollement(
            @PathVariable Long id) {

        if (!enrollementRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        enrollementRepository.deleteById(id);

        return ResponseEntity.ok(
                "Enrollment deleted successfully");
    }
}
