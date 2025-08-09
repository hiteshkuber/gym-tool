package com.example.gymworkout.gymworkout_insight.controller;

import com.example.gymworkout.gymworkout_insight.entity.Workout;
import com.example.gymworkout.gymworkout_insight.repository.WorkoutRepository;
import com.example.gymworkout.gymworkout_insight.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
public class InsightController {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private GeminiService geminiService;

    // POST /insight
    @PostMapping("/insight")
    public ResponseEntity<?> getInsight(@RequestBody InsightRequest request) {
        Optional<Workout> optionalWorkout = workoutRepository.findById(request.getWorkoutId());

        if (optionalWorkout.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Workout not found for ID " + request.getWorkoutId()));
        }

        String feedback = geminiService.generateInsight(optionalWorkout.get());
        return ResponseEntity.ok(Map.of("insight", feedback));
    }

    @GetMapping("/insight/last7days")
    public ResponseEntity<?> getLast7DaysInsight() {
        String startDate = LocalDate.now().minusDays(6).toString(); // includes today
        List<Workout> workouts = workoutRepository.findWorkoutsFromDate(startDate);
        if (workouts.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No workouts found in last 7 days"));
        }
        String feedback = geminiService.generateInsightForPeriod(workouts, 7);
        return ResponseEntity.ok(Map.of("insight", feedback));
    }

    @GetMapping("/insight/last30days")
    public ResponseEntity<?> getLast30DaysInsight() {
        String startDate = LocalDate.now().minusDays(29).toString(); // includes today
        List<Workout> workouts = workoutRepository.findWorkoutsFromDate(startDate);
        if (workouts.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No workouts found in last 30 days"));
        }
        String feedback = geminiService.generateInsightForPeriod(workouts, 30);
        return ResponseEntity.ok(Map.of("insight", feedback));
    }

    @GetMapping("/insight/last365days")
    public ResponseEntity<?> getLast365DaysInsight() {
        String startDate = LocalDate.now().minusDays(364).toString(); // includes today
        List<Workout> workouts = workoutRepository.findWorkoutsFromDate(startDate);
        if (workouts.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No workouts found in last year"));
        }
        String feedback = geminiService.generateInsightForPeriod(workouts, 365);
        return ResponseEntity.ok(Map.of("insight", feedback));
    }


    public static class InsightRequest {
        private Long workoutId;

        public Long getWorkoutId() {
            return workoutId;
        }

        public void setWorkoutId(Long workoutId) {
            this.workoutId = workoutId;
        }
    }
}
