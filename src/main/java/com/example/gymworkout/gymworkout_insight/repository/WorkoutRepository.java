package com.example.gymworkout.gymworkout_insight.repository;

import com.example.gymworkout.gymworkout_insight.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    @Query("SELECT w FROM Workout w WHERE w.date >= :startDate ORDER BY w.date ASC")
    List<Workout> findWorkoutsFromDate(@Param("startDate") String startDate);

}

