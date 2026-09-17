package com.example.ex1.Controller;

import com.example.ex1.entity.Marks;
import com.example.ex1.repository.MarksRepository;
import com.example.ex1.repository.StudentRepository;
import com.example.ex1.repository.CourseRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/marks")
public class MarksController {

    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public MarksController(
            MarksRepository marksRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository) {

        this.marksRepository = marksRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    // GET ALL MARKS
    @GetMapping
    public List<Marks> getAllMarks() {
        return marksRepository.findAll();
    }

    // GET MARKS BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMarksById(
            @PathVariable Long id) {

        Optional<Marks> marks =
                marksRepository.findById(id);

        if (marks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(marks.get());
    }

    // CREATE MARKS
    @PostMapping
    public ResponseEntity<?> createMarks(
            @RequestBody Marks marks) {

        // Check student
        if (!studentRepository.existsById(
                marks.getStudentId())) {

            return ResponseEntity.badRequest()
                    .body("Student not found");
        }

        // Check course
        if (!courseRepository.existsById(
                marks.getCourseId())) {

            return ResponseEntity.badRequest()
                    .body("Course not found");
        }

        // Check negative marks
        if (marks.getMarks() < 0) {

            return ResponseEntity.badRequest()
                    .body("Marks cannot be negative");
        }

        // Check marks greater than total marks
        if (marks.getMarks() > marks.getTotalMarks()) {

            return ResponseEntity.badRequest()
                    .body("Marks cannot be greater than total marks");
        }

        Marks saved = marksRepository.save(marks);

        return ResponseEntity.ok(saved);
    }

    // UPDATE MARKS
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMarks(
            @PathVariable Long id,
            @RequestBody Marks marks) {

        Optional<Marks> existing =
                marksRepository.findById(id);

        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check student
        if (!studentRepository.existsById(
                marks.getStudentId())) {

            return ResponseEntity.badRequest()
                    .body("Student not found");
        }

        // Check course
        if (!courseRepository.existsById(
                marks.getCourseId())) {

            return ResponseEntity.badRequest()
                    .body("Course not found");
        }

        // Check negative marks
        if (marks.getMarks() < 0) {

            return ResponseEntity.badRequest()
                    .body("Marks cannot be negative");
        }

        // Check marks greater than total marks
        if (marks.getMarks() > marks.getTotalMarks()) {

            return ResponseEntity.badRequest()
                    .body("Marks cannot be greater than total marks");
        }

        Marks oldMarks = existing.get();

        oldMarks.setStudentId(marks.getStudentId());
        oldMarks.setCourseId(marks.getCourseId());
        oldMarks.setExamName(marks.getExamName());
        oldMarks.setMarks(marks.getMarks());
        oldMarks.setTotalMarks(marks.getTotalMarks());

        Marks updated =
                marksRepository.save(oldMarks);

        return ResponseEntity.ok(updated);
    }

    // DELETE MARKS
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMarks(
            @PathVariable Long id) {

        if (!marksRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        marksRepository.deleteById(id);

        return ResponseEntity.ok(
                "Marks deleted successfully");
    }
}
