package com.example.gymworkout.gymworkout_insight.repository;

import com.example.gymworkout.gymworkout_insight.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {
    @Query("SELECT m FROM Meal m WHERE m.date >= :startDate ORDER BY m.date DESC")
    List<Meal> findMealsFromDate(@Param("startDate") String startDate);
}
