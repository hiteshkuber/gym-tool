package com.example.gymworkout.gymworkout_insight.controller;

import com.example.gymworkout.gymworkout_insight.dto.WorkoutDTO;
import com.example.gymworkout.gymworkout_insight.entity.Exercise;
import com.example.gymworkout.gymworkout_insight.entity.Workout;
import com.example.gymworkout.gymworkout_insight.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutRepository;

    // Log a new workout
    @PostMapping("/workout")
    public Map<String, Object> logWorkout(@RequestBody WorkoutDTO workoutDTO) {
        Workout workout = new Workout();
        workout.setDate(workoutDTO.getDate());

        List<Exercise> exercises = workoutDTO.getExercises().stream().map(dto -> {
            Exercise ex = new Exercise();
            ex.setName(dto.getName());
            ex.setSets(dto.getSets());
            ex.setReps(dto.getReps());
            ex.setWeight(dto.getWeight());
            ex.setWorkout(workout);
            return ex;
        }).collect(Collectors.toList());

        workout.setExercises(exercises);

        Workout saved = workoutRepository.save(workout);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "Workout logged");
        result.put("id", saved.getId());
        return result;
    }

    // Get all workout history
    @GetMapping("/history")
    public List<Map<String, Object>> getHistory() {
        List<Workout> workouts = workoutRepository.findAll();
        return workouts.stream().map(workout -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", workout.getId());
            m.put("date", workout.getDate());
            List<Map<String, Object>> exDtos = workout.getExercises().stream().map(ex -> {
                Map<String, Object> exMap = new LinkedHashMap<>();
                exMap.put("name", ex.getName());
                exMap.put("sets", ex.getSets());
                exMap.put("reps", ex.getReps());
                exMap.put("weight", ex.getWeight());
                return exMap;
            }).collect(Collectors.toList());
            m.put("exercises", exDtos);
            return m;
        }).collect(Collectors.toList());
    }
}
