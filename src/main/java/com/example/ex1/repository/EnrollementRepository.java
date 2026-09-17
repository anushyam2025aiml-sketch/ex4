package com.example.ex1.repository;

import com.example.ex1.entity.Enrollement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollementRepository
        extends JpaRepository<Enrollement, Long> {

    boolean existsByStudentIdAndCourseId(
            Long studentId,
            Long courseId);
}