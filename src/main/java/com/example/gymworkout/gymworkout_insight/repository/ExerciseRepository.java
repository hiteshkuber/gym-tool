package com.example.gymworkout.gymworkout_insight.repository;

import com.example.gymworkout.gymworkout_insight.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
